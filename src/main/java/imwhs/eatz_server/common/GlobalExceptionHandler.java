package imwhs.eatz_server.common;

import imwhs.eatz_server.common.error.ErrorCodeUser;
import imwhs.eatz_server.dto.apiresponse.ApiResponse;
import imwhs.eatz_server.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        return ApiResponse.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ApiResponse.error("경로 변수 " + e.getName() + "의 타입은 "
                + e.getRequiredType().getSimpleName() + " 이어야 합니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResponse<Object> handleMissingPathVariableException(MissingPathVariableException e) {
        return ApiResponse.error("경로 변수(" + e.getVariableName() + ")가 누락됐습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ApiResponse.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ErrorCodeUser errorCode = ErrorCodeUser.EATZ_USER_USERNAME_NOT_FOUND;
        return ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
    }

    @ExceptionHandler(DuplicatedEatzUserEmailException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicatedEatzUserEmailException(DuplicatedEatzUserEmailException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRecipeNotFoundException(RecipeNotFoundException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(IngredientNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleIngredientNotFoundException(IngredientNotFoundException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidVerificationCodeException(InvalidVerificationCodeException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailNotVerifiedException(EmailNotVerifiedException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

}
