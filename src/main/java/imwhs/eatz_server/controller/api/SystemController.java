package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.ClientLaunchNoticeResponse;
import imwhs.eatz_server.dto.ClientVersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v0/system")
@RestController
public class SystemController {

    @GetMapping("/client/version/ios")
    public ClientVersionResponse getIOSClientVersion() {
        return new ClientVersionResponse("1.0.0", "1.0.0", null);
    }

    @GetMapping("/client/notice/launch")
    public ClientLaunchNoticeResponse getLatestClientLaunchNotice() {
        return new ClientLaunchNoticeResponse(
                1L,
                "EATZ 베타 공개",
                """
                        ## EATZ - 바로 요리할 수 있는 모든 레시피

                       안녕하세요! EATZ 베타에 참여해주셔서 진심으로 감사드립니다!

                       현재 EATZ는 정식 출시를 앞두고 보완해야 할 부분을 점검하고 있는데요! 동시에 베타 버전을 통해 사용자 분들의 소중한 의견도 함께 받고 있습니다!

                       EATZ 사용 중에 발생한 문제점이나 불편한 점, 또는 건의하실 점이 있다면 아래 경로를 통해 개발자에게 의견을 남겨주세요!

                       ### EATZ 베타 의견 남기기

                       1. 화면 하단의 **'내 계정'** 탭하기
                       2. 화면 상단 우측의 **'설정 및 정보'** 탭하기
                       3. **'개발자에게 편지 쓰기'** 탭하기

                       남겨주신 의견을 최대한 반영해, 이를 바탕으로 더 나은 EATZ를 만들어가겠습니다!
                       """
        );
    }

}
