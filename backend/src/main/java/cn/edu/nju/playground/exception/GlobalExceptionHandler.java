package cn.edu.nju.playground.exception;

import cn.edu.nju.playground.model.vo.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlaygroundException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(PlaygroundException e) {
        log.warn(e.getMessage());
        if (e.getStatus() == 500) {
            e.printStackTrace();
        }
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getCode(), e.getMessage()));
    }
}