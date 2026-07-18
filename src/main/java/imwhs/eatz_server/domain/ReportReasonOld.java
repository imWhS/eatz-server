package imwhs.eatz_server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReasonOld {
    SPAM("홍보성 스팸 정보가 포함됨"),
    ABUSIVE_LANGUAGE("욕설, 비하, 혐오 표현이 포함됨"),
    SENSITIVE_OR_INAPPROPRIATE_CONTENT("민감하거나 부적절한 콘텐츠가 포함됨"),
    COPYRIGHT_INFRINGEMENT("저작권을 침해하는 콘텐츠가 포함됨"),
    OTHER("기타");

    private final String description;
}