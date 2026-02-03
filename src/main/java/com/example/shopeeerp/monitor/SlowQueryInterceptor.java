package com.example.shopeeerp.monitor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Properties;

@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SlowQueryInterceptor implements Interceptor {

    @Value("${app.mybatis.slow-ms:500}")
    private long slowThreshold;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long duration = System.currentTimeMillis() - start;
        if (duration >= slowThreshold) {
            Object[] args = invocation.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) args[0];
                Object param = args.length > 1 ? args[1] : null;
                BoundSql boundSql = ms.getBoundSql(param);
                String sql = boundSql != null ? boundSql.getSql() : null;
                int rows = resolveRows(result);
                log.warn("慢查询: mapper={}, duration={}ms, rows={}\nSQL: {}",
                        ms.getId(),
                        duration,
                        rows,
                        normalizeSql(sql));
            } else {
                log.warn("慢查询: mapper=unknown, duration={}ms, rows={}", duration, resolveRows(result));
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }

    private int resolveRows(Object result) {
        if (result == null) {
            return 0;
        }
        if (result instanceof Number) {
            return ((Number) result).intValue();
        }
        if (result instanceof Collection) {
            return ((Collection<?>) result).size();
        }
        return -1;
    }

    private String normalizeSql(String sql) {
        if (sql == null) {
            return null;
        }
        return sql.replaceAll("\\s+", " ").trim();
    }
}
