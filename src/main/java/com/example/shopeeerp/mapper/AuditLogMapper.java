package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog auditLog);
}
