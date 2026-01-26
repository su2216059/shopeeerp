package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketScrapeWorker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MarketScrapeWorkerMapper {

    /**
     * 插入Worker
     */
    int insert(MarketScrapeWorker worker);

    /**
     * 根据workerId查询Worker
     */
    MarketScrapeWorker selectByWorkerId(@Param("workerId") String workerId);

    /**
     * 更新Worker心跳时间
     */
    int updateHeartbeat(@Param("workerId") String workerId,
                       @Param("heartbeat") LocalDateTime heartbeat);

    /**
     * 查询在线Worker列表（最后心跳时间在timeout之后）
     */
    List<MarketScrapeWorker> selectOnlineWorkers(@Param("timeout") LocalDateTime timeout);

    /**
     * 更新Worker状态
     */
    int updateWorkerStatus(@Param("workerId") String workerId,
                          @Param("status") String status,
                          @Param("taskId") Long taskId,
                          @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 标记离线Worker（最后心跳时间在timeout之前）
     */
    int markOfflineWorkers(@Param("timeout") LocalDateTime timeout,
                          @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新Worker统计信息
     */
    int updateWorkerStats(@Param("workerId") String workerId,
                         @Param("totalTasks") Integer totalTasks,
                         @Param("successTasks") Integer successTasks,
                         @Param("failedTasks") Integer failedTasks,
                         @Param("updatedAt") LocalDateTime updatedAt);
}
