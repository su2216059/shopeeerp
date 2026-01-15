package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProfitOperation;

import java.time.LocalDateTime;
import java.util.List;

public interface OzonProfitOperationService {

    OzonProfitOperation getByOperationId(Long operationId);

    List<OzonProfitOperation> getByPostingNumbers(List<String> postingNumbers);

    boolean saveBatch(List<OzonProfitOperation> list);

    /**
     * 清理指定时间段的数据，便于重跑。
     */
    boolean deleteByDateRange(LocalDateTime from, LocalDateTime to);

    boolean deleteByPostingNumber(String postingNumber);
}
