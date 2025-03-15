package imwhs.eatz_server.common;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.exception.DuplicatedEatzUserException;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<Object> handleUsernameNotFoundException(MethodArgumentNotValidException e) {
        log.error(e.getBindingResult().getFieldError().getDefaultMessage());
        return ApiResponse.error("유효하지 않은 사용자 이름입니다.");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicatedEatzUserException.class)
    public ApiResponse<Object> handleDuplicatedEatzUserException(DuplicatedEatzUserException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RecipeNotFoundException.class)
    public ApiResponse<Object> handleRecipeNotFoundException(RecipeNotFoundException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IngredientNotFoundException.class)
    public ApiResponse<Object> handleIngredientNotFoundException(IngredientNotFoundException e) {
        return ApiResponse.error(e.getMessage());
    }

}
