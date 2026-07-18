package imwhs.eatz_server.scheduler;

import imwhs.eatz_server.service.recipe.RecipeOutboundCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeOutboundCountScheduler {

    private final RecipeOutboundCountService recipeOutboundCountService;

    // 매일 매시간마다, 3분(정시)부터 시작해서 5분 간격으로 0초가 될 때 실행합니다.
    @Scheduled(cron = "0 3/5 * * * *", zone = "Asia/Seoul")
    public void flushRecipeOutboundCount() {
        recipeOutboundCountService.flushOutboundCountIncrements();
    }

}
