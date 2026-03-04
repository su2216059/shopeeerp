package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog auditLog);
    @Select("SELECT * FROM audit_log WHERE status = #{status}")
    Cursor<AuditLog> scanByStatus(Integer status);
}
