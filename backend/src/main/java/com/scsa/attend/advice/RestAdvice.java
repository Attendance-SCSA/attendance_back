package com.scsa.attend.advice;

import com.scsa.attend.exception.AddException;
import com.scsa.attend.exception.FindException;
import com.scsa.attend.exception.ModifyException;
import com.scsa.attend.exception.RemoveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 조회 실패 예외 처리.
     *
     * @param e FindException
     * @return 404 NOT_FOUND 응답
     * @throws RuntimeException 내부 처리 중 예외 발생 시
     */
    @ExceptionHandler(FindException.class)
    public ResponseEntity<ErrorResponse> handleFindException(FindException e)
            throws RuntimeException {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),   // 404
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * 생성 실패 예외 처리.
     * (요청 값 유효성 실패 등)
     *
     * @param e AddException
     * @return 400 BAD_REQUEST 응답
     * @throws RuntimeException 내부 처리 중 예외 발생 시
     */
    @ExceptionHandler(AddException.class)
    public ResponseEntity<ErrorResponse> handleAddException(AddException e)
            throws RuntimeException {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 수정 실패 예외 처리.
     * (비즈니스 룰 위반, 유효성 실패 등)
     *
     * @param e ModifyException
     * @return 400 BAD_REQUEST 응답
     * @throws RuntimeException 내부 처리 중 예외 발생 시
     */
    @ExceptionHandler(ModifyException.class)
    public ResponseEntity<ErrorResponse> handleModifyException(ModifyException e)
            throws RuntimeException {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 삭제 실패 예외 처리.
     * (하위 Task 존재 등 삭제 불가 상황)
     *
     * @param e RemoveException
     * @return 409 CONFLICT 응답
     * @throws RuntimeException 내부 처리 중 예외 발생 시
     */
    @ExceptionHandler(RemoveException.class)
    public ResponseEntity<ErrorResponse> handleRemoveException(RemoveException e)
            throws RuntimeException {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),    // 409
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * 잘못된 요청(파라미터 등) 처리.
     * api.yaml의 400 "잘못된 요청"과 매핑.
     *
     * @param e IllegalArgumentException
     * @return 400 BAD_REQUEST 응답
     * @throws RuntimeException 내부 처리 중 예외 발생 시
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e)
            throws RuntimeException {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 예상치 못한 모든 런타임 예외 처리.
     *
     * @param e RuntimeException
     * @return 500 INTERNAL_SERVER_ERROR 응답
     * @throws RuntimeException 내부 처리 중 예외 발생 시
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e)
            throws RuntimeException {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "Unexpected error occurred: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * API 에러 응답 공통 DTO.
     */
    public static class ErrorResponse {
        private final int status;
        private final String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        /**
         * @return HTTP 상태 코드
         */
        public int getStatus() {
            return status;
        }

        /**
         * @return 에러 메시지
         */
        public String getMessage() {
            return message;
        }
    }
}
