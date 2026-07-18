package imwhs.eatz_server.resolver;

import imwhs.eatz_server.auth.EatzUserAuth;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * AuthenticatedEatzUserId 어노테이션이 붙은 컨트롤러 메서드의 파라미터에 로그인한 사용자의 ID를 주입합니다.
 */
@Component
public class AuthenticatedEatzUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(AuthenticatedEatzUserId.class);
        boolean isAssignableParameterType = Long.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && isAssignableParameterType;
    }

    @Nullable
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) {
        AuthenticatedEatzUserId authenticatedEatzUserId = parameter.getParameterAnnotation(AuthenticatedEatzUserId.class);
        boolean required = authenticatedEatzUserId == null ? false : authenticatedEatzUserId.required();
        if (!required) { return EatzUserAuth.getId().orElse(null); }
        return EatzUserAuth.getId().orElseThrow(() -> new UnauthorizedAccessException("로그인한 사용자만 요청할 수 있어요."));
    }

}
