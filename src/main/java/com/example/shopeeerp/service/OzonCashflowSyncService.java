package com.example.shopeeerp.service;

import java.time.LocalDateTime;

public interface OzonCashflowSyncService {
    void sync(LocalDateTime from, LocalDateTime to);
}
