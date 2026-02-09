package com.example.shopeeerp.service;

import com.example.shopeeerp.adapter.dto.ozon.ProfitQueryDto;
import com.example.shopeeerp.adapter.dto.ozon.ProfitResultResponse;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitService
 * @packageName: com.example.shopeeerp.service.impl
 * @description:
 * @date: 2026/2/4 16:34
 */
public interface ProfitService {
    ProfitResultResponse calcByOrder(String orderId);

    ProfitResultResponse summary(ProfitQueryDto query);
}
