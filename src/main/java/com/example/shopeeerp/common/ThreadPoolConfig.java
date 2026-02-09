package com.example.shopeeerp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolConfig.class);

    // 统一读 CPU 核心数
    private static final int CPU = Runtime.getRuntime().availableProcessors();

    // 允许通过配置覆盖（不给也能跑）
    @Value("${app.tp.io.core:0}")
    private int ioCoreOverride;
    @Value("${app.tp.io.max:0}")
    private int ioMaxOverride;
    @Value("${app.tp.io.queue:2000}")
    private int ioQueue;

    @Value("${app.tp.cpu.queue:200}")
    private int cpuQueue;

    @Value("${app.tp.export.queue:200}")
    private int exportQueue;

    @Value("${app.tp.scheduled.queue:500}")
    private int scheduledQueue;

    /**
     * 任务装饰器：把 MDC/TraceId 之类上下文“带过去”
     * 目前给空实现，你如果用了 MDC，可以在这里 copy。
     */
    @Bean
    public TaskDecorator taskDecorator() {
        return runnable -> runnable;
    }

    /**
     * 统一的线程工厂：命名 + 异常兜底日志
     */
    private ThreadFactory namedThreadFactory(String prefix) {
        AtomicLong idx = new AtomicLong(1);
        return r -> {
            Thread t = new Thread(r);
            t.setName(prefix + idx.getAndIncrement());
            t.setDaemon(false);
            t.setUncaughtExceptionHandler((th, ex) ->
                    log.error("Uncaught exception in thread: {}", th.getName(), ex)
            );
            return t;
        };
    }

    /**
     * 自定义拒绝策略：记录关键信息 + 反压（CallerRuns）
     */
    private RejectedExecutionHandler loggingCallerRuns(String poolName) {
        return (r, executor) -> {
            // 反压：让提交任务的线程自己执行，系统“慢一点但不死”
            if (!executor.isShutdown()) {
                log.warn("[{}] Rejected task. active={}, poolSize={}, queueSize={}, remainingQueue={}",
                        poolName,
                        executor.getActiveCount(),
                        executor.getPoolSize(),
                        executor.getQueue().size(),
                        executor.getQueue().remainingCapacity()
                );
                r.run();
            }
        };
    }

    /**
     * 创建 ThreadPoolTaskExecutor 的统一工厂方法
     */
    private ThreadPoolTaskExecutor build(String poolName,
                                        int core,
                                        int max,
                                        int queueCapacity,
                                        int keepAliveSeconds,
                                        boolean allowCoreTimeout,
                                        TaskDecorator decorator,
                                        RejectedExecutionHandler rejectedHandler) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setThreadNamePrefix(poolName + "-");
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);

        // spring 的默认 ThreadFactory 命名够用，但我更建议接管 ThreadFactory，异常日志更稳
        executor.setThreadFactory(namedThreadFactory(poolName + "-"));

        // 拒绝策略（强烈建议显式指定）
        executor.setRejectedExecutionHandler(rejectedHandler);

        // 优雅停机：等待正在执行/队列中的任务完成（配合 server.shutdown=graceful 更丝滑）
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        // 上下文传递
        executor.setTaskDecorator(decorator);

        // 核心线程是否允许超时回收
        executor.setAllowCoreThreadTimeOut(allowCoreTimeout);

        executor.initialize();

        log.info("Init pool [{}] core={}, max={}, queue={}, keepAlive={}s, allowCoreTimeout={}",
                poolName, core, max, queueCapacity, keepAliveSeconds, allowCoreTimeout);

        return executor;
    }

    /**
     * 默认/主线程池：你不指定 executor 时会用它（@Async 默认也可指到它）
     * 建议用 IO 池作为 Primary（大多数业务是 IO 为主）
     */
    @Bean(name = "ioExecutor")
    @Primary
    public ThreadPoolTaskExecutor ioExecutor(@Qualifier("taskDecorator") TaskDecorator decorator) {
        int core = (ioCoreOverride > 0) ? ioCoreOverride : CPU * 2;
        int max  = (ioMaxOverride > 0) ? ioMaxOverride : CPU * 4;

        return build(
                "io",
                core,
                max,
                ioQueue,
                60,
                true,
                decorator,
                loggingCallerRuns("io")
        );
    }

    /**
     * CPU 密集池：适合计算/解析/压缩等
     * 线程数别太大，避免上下文切换把 CPU 烤糊
     */
    @Bean(name = "cpuExecutor")
    public ThreadPoolTaskExecutor cpuExecutor(@Qualifier("taskDecorator") TaskDecorator decorator) {
        int core = Math.max(1, CPU);
        int max  = Math.max(1, CPU);

        return build(
                "cpu",
                core,
                max,
                cpuQueue,
                30,
                false,
                decorator,
                loggingCallerRuns("cpu")
        );
    }

    /**
     * 导出/大任务池：比如报表导出、批量同步、生成文件等
     * 特点：队列更小，尽量让它“排队受控”，避免吃掉你主业务资源
     */
    @Bean(name = "exportExecutor")
    public ThreadPoolTaskExecutor exportExecutor(@Qualifier("taskDecorator") TaskDecorator decorator) {
        int core = Math.max(1, CPU);      // 也可以给小一点：CPU/2
        int max  = Math.max(2, CPU * 2);  // 看你导出任务的 IO/CPU 占比

        return build(
                "export",
                core,
                max,
                exportQueue,
                120,
                true,
                decorator,
                loggingCallerRuns("export")
        );
    }

    /**
     * 轻量定时/异步事件池：比如发 MQ、更新缓存、写审计日志、轻量通知
     * 用小一点的线程数，避免“定时任务挤爆线程”
     */
    @Bean(name = "scheduledExecutor")
    public ThreadPoolTaskExecutor scheduledExecutor(@Qualifier("taskDecorator") TaskDecorator decorator) {
        int core = Math.max(2, CPU);          // 轻任务通常少量线程即可
        int max  = Math.max(4, CPU * 2);

        return build(
                "scheduled",
                core,
                max,
                scheduledQueue,
                60,
                true,
                decorator,
                loggingCallerRuns("scheduled")
        );
    }

    /**
     * 可选：给 CompletableFuture 用的 ScheduledExecutorService（延迟/重试/超时）
     * 例如：schedule 重试，或为 future 设置超时兜底。
     */
    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService timerExecutor() {
        ThreadFactory tf = namedThreadFactory("timer-");
        return Executors.newScheduledThreadPool(Math.max(1, CPU), tf);
    }

    /**
     * 可选：简单的池子状态打印（每 30s）
     * 你也可以换成 Micrometer 指标上报，这里先给“可落地”的最小版本。
     */
    @Bean
    public PoolMetricsLogger poolMetricsLogger(
            @Qualifier("ioExecutor") ThreadPoolTaskExecutor io,
            @Qualifier("cpuExecutor") ThreadPoolTaskExecutor cpu,
            @Qualifier("exportExecutor") ThreadPoolTaskExecutor export,
            @Qualifier("scheduledExecutor") ThreadPoolTaskExecutor scheduled
    ) {
        Map<String, ThreadPoolTaskExecutor> poolMap = new HashMap<>();
        poolMap.put("io", io);
        poolMap.put("cpu", cpu);
        poolMap.put("export", export);
        poolMap.put("scheduled", scheduled);

        return new PoolMetricsLogger(poolMap);
    }

    public static class PoolMetricsLogger {
        private final Map<String, ThreadPoolTaskExecutor> pools;
        private final ScheduledExecutorService reporter =
                Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r);
                    t.setName("tp-metrics");
                    t.setDaemon(true);
                    return t;
                });

        public PoolMetricsLogger(Map<String, ThreadPoolTaskExecutor> pools) {
            this.pools = pools;
            reporter.scheduleAtFixedRate(this::report, 10, 30, TimeUnit.SECONDS);
        }

        private void report() {
            for (Map.Entry<String, ThreadPoolTaskExecutor> e : pools.entrySet()) {
                String name = e.getKey();
                ThreadPoolExecutor ex = e.getValue().getThreadPoolExecutor();
                if (ex == null) continue;

                log.info("[tp:{}] active={}, poolSize={}, core={}, max={}, queueSize={}, queueRemaining={}, completed={}",
                        name,
                        ex.getActiveCount(),
                        ex.getPoolSize(),
                        ex.getCorePoolSize(),
                        ex.getMaximumPoolSize(),
                        ex.getQueue().size(),
                        ex.getQueue().remainingCapacity(),
                        ex.getCompletedTaskCount()
                );
            }
        }
    }
}
