package imwhs.eatz_server.config;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageableValidationHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

    // 기본 페이지 번호
    private static final int DEFAULT_PAGE = 0;

    // 기본 페이지 크기
    private static final int DEFAULT_SIZE = 10;

    // 최대 페이지 크기
    private static final int MAX_PAGE_SIZE = 20;

    private static final String ERROR_PAGE_NUMBER = "페이지 번호는 0 이상의 정수여야 합니다.";

    private static final String ERROR_PAGE_SIZE = "페이징 크기는 1부터 " + MAX_PAGE_SIZE + " 사이의 정수여야 합니다.";

    private static final String ERROR_INVALID_FORMAT = "페이지 번호와 페이징 크기는 숫자 형태여야 합니다.";

    @Override
    public Pageable resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        // Pageable 생성 전, 유효성을 검증합니다.
        int page = parseIntOfParameter(webRequest.getParameter("page"), DEFAULT_PAGE, ERROR_INVALID_FORMAT);
        int size = parseIntOfParameter(webRequest.getParameter("size"), DEFAULT_SIZE, ERROR_INVALID_FORMAT);

        // 페이징 크기 값의 유효성을 검증합니다.
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(ERROR_PAGE_SIZE);
        }

        // 페이지 번호 값의 유효성을 검증합니다.
        if (page < 0) {
            throw new IllegalArgumentException(ERROR_PAGE_NUMBER);
        }

        return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    /**
     * 파라미터 값을 정수로 변환합니다. 파라미터가 null이면 기본 값을 반환합니다.
     *
     * @param parameter 파라미터
     * @param defaultVal 파라미터가 null일 경우 반환할 기본 값
     * @param message 변환 실패 시 예외와 함께 전달할 에러 메시지
     * @return 변환된 값
     */
    private int parseIntOfParameter(String parameter, int defaultVal, String message) {
        // 파라미터가 null이면 기본 값을 반환합니다.
        if (parameter == null) {
            return defaultVal;
        }

        // 파라미터를 정수로 변환 시도합니다.
        try {
            return Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            // 정수로 변환 실패한 경우 예외를 발생시킵니다.
            throw new IllegalArgumentException(message);
        }
    }

}
