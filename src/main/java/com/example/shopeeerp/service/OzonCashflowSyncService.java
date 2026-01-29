package com.example.shopeeerp.service;

import java.time.LocalDateTime;

public interface OzonCashflowSyncService {
    default void sync(LocalDateTime from, LocalDateTime to) {
        sync(from, to, null);
    }

    void sync(LocalDateTime from, LocalDateTime to, Long shopId);
}
