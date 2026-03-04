package com.example.shopeeerp.exception;

import com.example.shopeeerp.adapter.dto.ozon.CommonResult;
import com.example.shopeeerp.adapter.dto.ozon.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResult<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("参数错误: {}", ex.getMessage(), ex);
        CommonResult<Void> resp = CommonResult.fail(HttpStatus.BAD_REQUEST.value(), "参数错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<CommonResult<Void>> handleBiz(BizException ex, HttpServletRequest request) {
        log.warn("参数错误: {}", ex.getMessage(), ex);
        CommonResult<Void> resp = CommonResult.fail(HttpStatus.BAD_REQUEST.value(), "参数错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResult<Void>> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        log.error("运行时异常: {}", ex.getMessage(), ex);
        CommonResult<Void> resp = CommonResult.fail(ResultCode.ERROR, "操作失败");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResult<Void>> handleException(Exception ex, HttpServletRequest request) {
        String method = request != null ? request.getMethod() : "UNKNOWN";
        String uri = request != null ? request.getRequestURI() : "UNKNOWN";
        log.error("系统异常: method={}, uri={}, error={}", method, uri, ex.getMessage(), ex);
        CommonResult<Void> resp = CommonResult.fail(ResultCode.ERROR, "系统错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
