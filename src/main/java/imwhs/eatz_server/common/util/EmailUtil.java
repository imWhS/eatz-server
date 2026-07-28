package imwhs.eatz_server.common.util;

import java.util.regex.Pattern;

public final class EmailUtil {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 이메일 주소가 비어 있어요.");
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("이메일 주소(" + email + ")가 올바른 형식이 아니에요.");
        }
    }

    /**
     * 이메일 주소의 로컬 파트(사용자 이름) 일부를 마스킹 처리합니다.
     * <p>
     *     사용자 이름 앞 3자리 이후부터 '@' 기호 전까지의 문자를 '*'로 치환합니다.
     * </p>
     * @param email
     * @return
     */
    public static String maskEmail(String email) {
        validateEmail(email);
        return email.replaceAll("(?<=.{3}).(?=.*@)", "*");
    }

}