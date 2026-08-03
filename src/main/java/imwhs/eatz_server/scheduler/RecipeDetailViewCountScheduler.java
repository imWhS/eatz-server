package imwhs.eatz_server.scheduler;

import imwhs.eatz_server.service.recipe.RecipeDetailViewCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecipeDetailViewCountScheduler {

    private final RecipeDetailViewCountService recipeDetailViewCountService;

    // 매년 매일 매시간마다, 0분부터 시작해서 5분 간격으로 0초가 될 때 실행합니다.
    @Scheduled(cron = "0 0/5 * * * *", zone = "Asia/Seoul")
    public void flushRecipeDetailViewCount() { recipeDetailViewCountService.flushViewCountIncrements(); }

}
