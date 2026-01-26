package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketScrapeTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MarketScrapeTaskMapper {

    int insert(MarketScrapeTask task);

    int insertBatch(@Param("list") List<MarketScrapeTask> list);

    List<MarketScrapeTask> selectPendingForUpdate(@Param("now") LocalDateTime now,
                                                  @Param("limit") int limit);

    int markInProgress(@Param("ids") List<Long> ids,
                       @Param("lockOwner") String lockOwner,
                       @Param("lockAt") LocalDateTime lockAt,
                       @Param("updatedAt") LocalDateTime updatedAt);

    int markSuccess(@Param("id") Long id,
                    @Param("fetchedAt") LocalDateTime fetchedAt,
                    @Param("updatedAt") LocalDateTime updatedAt);

    int markFailure(@Param("id") Long id,
                    @Param("error") String error,
                    @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新任务进度
     */
    int updateProgress(@Param("id") Long id,
                      @Param("workerId") String workerId,
                      @Param("progressJson") String progressJson,
                      @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 完成任务
     */
    int completeTask(@Param("id") Long id,
                    @Param("workerId") String workerId,
                    @Param("status") String status,
                    @Param("fetchedAt") LocalDateTime fetchedAt,
                    @Param("errorMessage") String errorMessage,
                    @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 释放超时任务
     */
    int releaseTimeoutTasks(@Param("timeout") LocalDateTime timeout,
                           @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查询任务列表
     */
    List<MarketScrapeTask> selectTasks(@Param("status") String status,
                                      @Param("limit") int limit);
}
