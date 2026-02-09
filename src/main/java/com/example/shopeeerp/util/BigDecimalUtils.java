package com.example.shopeeerp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: BigDecimalUtils
 * @packageName: com.example.shopeeerp.util
 * @description:
 * @date: 2026/2/5 10:19
 */
public class BigDecimalUtils {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    private static BigDecimal nvl(BigDecimal x) { return x == null ? ZERO : x; }
}
