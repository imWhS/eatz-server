package imwhs.eatz_server.resolver;

import imwhs.eatz_server.exception.InvalidPageRequestException;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PageableValidationHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

    /**
     * 기본 페이지
     */
    private static final int DEFAULT_PAGE = 0;

    /**
     * 기본 페이징 크기
     */
    private static final int DEFAULT_PAGING_SIZE = 10;

    /**
     * 최대 페이징 크기
     */
    private static final int MAX_PAGING_SIZE = 20;

    private static final String ERROR_PAGE = "페이지는 0 이상의 정수만 사용할 수 있어요.";
    private static final String ERROR_PAGING_SIZE = "페이징 크기는 1부터 " + MAX_PAGING_SIZE + " 사이의 자연수만 사용할 수 있어요.";
    private static final String ERROR_INVALID_FORMAT_PAGE = "페이지는 정수만 사용할 수 있어요.";
    private static final String ERROR_INVALID_FORMAT_PAGING_SIZE = "페이징 크기는 정수만 사용할 수 있어요.";

    @Override
    public Pageable resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        // Pageable 생성 전, 유효성을 검증합니다.
        int page = parseIntFromParameter(
                webRequest.getParameter("page"),
                DEFAULT_PAGE,
                ERROR_INVALID_FORMAT_PAGE);

        int size = parseIntFromParameter(
                webRequest.getParameter("size"),
                DEFAULT_PAGING_SIZE,
                ERROR_INVALID_FORMAT_PAGING_SIZE);

        // 페이징 크기의 유효성을 검증합니다.
        if (size < 1 || MAX_PAGING_SIZE < size) { throw new InvalidPageRequestException(ERROR_PAGING_SIZE); }

        // 페이지 번호의 유효성을 검증합니다.
        if (page < 0) { throw new InvalidPageRequestException(ERROR_PAGE); }

        return PageRequest.of(page, size);
    }

    /**
     * 파라미터의 유효성을 검증한 후 정수로 변환합니다.
     * 파라미터가 null이면 기본 값을 반환합니다.
     * @param parameter 파라미터
     * @param defaultValue 기본 값
     * @param message 변환 실패 시, 예외로 전달할 에러 메시지
     * @return 변환된 정수
     * @throws InvalidPageRequestException 변환에 실패한 경우
     */
    private int parseIntFromParameter(String parameter, int defaultValue, String message) {
        // 파라미터가 유효하지 않으면 기본 값을 반환합니다.
        if (parameter == null) { return defaultValue; }

        // 파라미터를 정수로 변환 시도합니다.
        try {
            return Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            // 정수로 변환 실패한 경우 예외를 발생시킵니다.
            throw new InvalidPageRequestException(message);
        }
    }

}
