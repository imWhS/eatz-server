package imwhs.eatz_server.common;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.common.error.EatzUserErrorType;
import imwhs.eatz_server.dto.auth.EmailVerificationCodeResponse;
import imwhs.eatz_server.dto.ErrorResponse;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.exception.base.BaseAuthenticationException;
import imwhs.eatz_server.exception.base.BaseException;
import imwhs.eatz_server.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 애플리케이션의 Spring MVC 계층(Controller, Service, Repository 등)에서 발생한 모든 예외를 공통으로 처리합니다.
 * 예외 발생 원인 등을 ErrorResponse에 담은 후, 상태 코드와 함께 오류 응답을 클라이언트에게 전송합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("없는 리소스를 요청했어요. | {}", e.getResourcePath());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorResponse.create("NO_RESOURCE_FOUND", e.getLocalizedMessage()));
    }

    @ExceptionHandler(BaseAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleBaseAuthenticationException(BaseAuthenticationException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.create(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorResponse.create("MAX_UPLOAD_SIZE_EXCEEDED", e.getLocalizedMessage()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.create(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ResponseEntity<ErrorResponse> response = ResponseEntity
                .badRequest()
                .body(ErrorResponse.create(EatzCommonErrorType.INVALID_REQUEST_ARGUMENT, e.getMessage()));
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse;
        if (e.getRequiredType() != null) {
            String simpleName = e.getRequiredType().getSimpleName();
            errorResponse = ErrorResponse.create(
                    EatzCommonErrorType.INVALID_REQUEST_ARGUMENT,
                    "경로 변수 " + e.getName() + "의 타입은 " + simpleName + " 이어야 해요.");
        } else {
            errorResponse = ErrorResponse.create(
                    EatzCommonErrorType.INVALID_REQUEST_ARGUMENT,
                    "경로 변수(" + e.getName() + ")가 올바르지 않아요.");
        }

        return ResponseEntity
                .badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException e) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.create(
                        EatzCommonErrorType.INVALID_REQUEST_ARGUMENT,
                        "경로 변수(" + e.getVariableName() + ")가 누락됐어요."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors =  e.getBindingResult().getAllErrors();
        if (allErrors.size() == 1) {
            String defaultMessage = allErrors.get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ErrorResponse.create(
                    EatzCommonErrorType.INVALID_REQUEST_ARGUMENT, defaultMessage));
        } else {
            return ResponseEntity.badRequest().body(ErrorResponse.create(EatzCommonErrorType.INVALID_REQUEST_ARGUMENTS));
        }
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.create(
                        EatzCommonErrorType.INVALID_REQUEST_ARGUMENT,
                        "저장 또는 업데이트하려는 데이터가 올바르지 않아요."));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException e) {
        EatzUserErrorType errorType = EatzUserErrorType.USERNAME_NOT_FOUND;
        return ResponseEntity
                .status(errorType.getStatus())
                .body(ErrorResponse.create(errorType.getCode(), errorType.getMessage()));
    }

    @ExceptionHandler(VerificationCodeDailyCreationLimitExceededException.class)
    public ResponseEntity<EmailVerificationCodeResponse> handleVerificationCodeCreationReachedLimit(
            VerificationCodeDailyCreationLimitExceededException e) {
        // 인증 번호 전송 가능 횟수가 초과됐을 경우에도, 오류 응답 코드와 함께 DTO를 생성해서 보냅니다.
        EmailVerificationCodeResponse errorBody = new EmailVerificationCodeResponse(
                AuthService.MAX_SEND_COUNT_PER_DAY,
                0
        );

        return new ResponseEntity<>(errorBody, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("서버 내부에서 오류가 발생했어요.", e);
        return ResponseEntity
                .status(EatzCommonErrorType.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.create(EatzCommonErrorType.INTERNAL_SERVER_ERROR));
    }

}
