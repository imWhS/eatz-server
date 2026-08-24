package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.ClientLaunchNoticeResponse;
import imwhs.eatz_server.dto.ClientVersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
                2L,
                "환영합니다!",
                """
                        EATZ는 가지고 있는 재료와 도구만으로 지금 당장 요리할 수 있는 레시피를 찾아주고, 다른 사람들과 레시피를 공유할 수 있는 소셜 레시피 플랫폼이에요.
                       
                        또한, 플래너와 체크리스트를 통해 요리 일정 별로 준비해야 할 재료와 도구까지 체계적으로 관리할 수 있어요.
                       
                        EATZ의 주요 기능을 알려드릴게요.
                       
                        ### 보관함 기반의 요리할만한 레시피 탐색
                       
                        - 더 이상 레시피에 재료와 도구를 맞출 필요 없어요. 가지고 계신 재료와 도구를 보관함에 추가해보세요.
                        - 지금 당장 모든 재료와 도구가 완벽하게 준비되어 있지 않아도 돼요. 최소한의 추가 준비물로 요리할만한 레시피부터 보여드릴 수도 있어요.
                        
                        ### 레시피 요리 일정 별 재료와 도구 관리
                        
                        - 원하는 레시피를 찾으셨다면, 요리할 날짜를 플래너에 추가해보세요. 
                        - 플래너에서 특정한 날짜나 기간 동안 요리해야 할 모든 레시피를 바탕으로 미리 준비해야 할 재료와 도구를 체크리스트로 한눈에 정리해드려요.
                        - 체크리스트를 통해서 레시피 별 요리 가능 여부 뿐 아니라 모든 레시피를 요리하기 위해 준비해야 할 재료와 도구를 한눈에 파악하실 수도 있어요.
                        
                        ### 레시피 공유 및 소셜 커뮤니케이션
                        
                        - 나만의 레시피를 직접 등록하고, 사람들과 공유해보세요.
                        - 나와 다른 사람들이 올린 레시피에 댓글을 남기고, 평가를 등록하며 다른 사람들과 소통해보세요.
                        - 요리하고 싶은 레시피를 키워드로 검색하고, 다른 사람들의 레시피를 탐색하면서 새로운 영감을 얻을 수도 있어요.
                        - 마음에 드는 레시피에 '좋아요'를 표시해 반응을 남겨보세요. 나중에 다시 보거나, 자주 요리하고 싶은 레시피는 저장해둘 수 있어요.
                        
                        레시피 탐색부터 체계적인 재료와 도구 관리, 그리고 사람들과의 소통까지, EATZ와 함께 더 효율적인 요리 생활을 시작해보세요 :-)
                        
                        ### EATZ 의견 남기기
                        
                        사용자 분들의 소중한 의견도 함께 받고 있어요!
                        
                        EATZ 사용 중에 발생한 문제점이나 불편한 부분, 아이디어, 추가로 필요한 재료 혹은 도구, 새로운 레시피 등록 등과 같이 제안해주실만한 게 있다면 아래 경로를 통해 개발자에게 의견을 남겨주세요!
                        
                        1. 화면 하단의 **'내 계정'** 탭하기
                        2. 화면 우측 상단의 **'설정 및 정보'** 탭하기
                        3. **'개발자에게 편지 쓰기'** 탭하기
                       """,
                false
        );
    }

}
