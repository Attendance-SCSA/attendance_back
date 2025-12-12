package com.scsa.attend.advice;

import com.scsa.attend.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 공통 예외 처리 핸들러.
 *
 * 서비스 계층에서 던지는 도메인 예외를
 * api.yaml에 정의된 HTTP 상태 코드와 맞춰서 변환한다.
 */
@RestControllerAdvice
public class RestAdvice {

    /**
     * API 에러 응답 공통 DTO.
     */
    public static class ErrorResponse {
        private final int status;
        private final String code;
        private final String message;

        public ErrorResponse(int status, String code, String message) {
            this.status = status;
            this.code = code;
            this.message = message;
        }

        /**
         * @return HTTP 상태
         */
        public int getStatus() {
            return status;
        }

        /**
         * @ return 에러 요약 코드
         */
        public String getCode() {return code; }

        /**
         * @return 에러 메시지
         */
        public String getMessage() {
            return message;
        }
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> PermissionDeniedExceptionHandler(Exception e)
        throws RuntimeException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse body = new ErrorResponse(status.value(),
                "PERMISSION_DENIED",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class, // @RequestBody 유효성 위반
            ConstraintViolationException.class    // @RequestParam, @PathVariable 유효성 위반
    })
    public ResponseEntity<ErrorResponse> InvalidInputExceptionHandler(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse body = new ErrorResponse(
                status.value(), // 400
                "INVALID_INPUT",
                "누락된 필드가 있거나 입력형태가 적절하지 않습니다."
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> NotFoundExceptionHandler(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse body = new ErrorResponse(
                status.value(), // 400
                "NOT_FOUND",
                e.getMessage()
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> ResourceConflictExceptionHandler(Exception e) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse body = new ErrorResponse(
                status.value(), // 400
                "RESOURCE_CONFLICT",
                e.getMessage()
        );

        return ResponseEntity.status(status).body(body);
    }

}
