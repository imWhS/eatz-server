package imwhs.eatz_server.config;

import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.liked.LikedRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.repository.ReportCategoryRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.tag.TagRepository;
import imwhs.eatz_server.repository.tag.ThemeRepository;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.repository.liked.LikedRecipeRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Component
public class DataSeeder {

    private final EatzUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final IngredientRepository ingredientRepository;
    private final KitchenwareRepository kitchenwareRepository;
    private final LikedRecipeRepository likedRecipeRepository;
    private final ThemeRepository themeRepository;
    private final TagRepository tagRepository;
    private final ReportCategoryRepository reportCategoryRepository;

    @Bean
    public ApplicationRunner initSampleData() {
        return args -> {
            if (userRepository.count() > 2) {
                log.info("이미 샘플 데이터가 존재해서, DataSeeder를 실행하지 않아요.");
                return;
            }
            log.info("샘플 데이터 생성을 시작할게요...");
            seedData();
            log.info("샘플 데이터를 성공적으로 생성했어요!");
        };
    }

    @Transactional
    public void seedData() {
        createReportReasons();

        // 테마 & 태그
        createThemesAndTags();
        List<Tag> allTags = tagRepository.findAll();

        // 사용자
        List<EatzUser> users = createUsers();

        // 재료
        List<Ingredient> ingredients = createIngredients();

        // 도구
        List<Kitchenware> kitchenwares = createKitchenwares();

        // 레시피 (요리 가능/불가능 확인을 위한 초간단 레시피, 랜덤 레시피)
        List<Recipe> recipes = new ArrayList<>();
        createSpecificSimpleRecipes(users, ingredients, kitchenwares, allTags, recipes);
        createRandomRecipes(80, users, ingredients, kitchenwares, allTags, recipes);

        // 좋아요, 댓글, 평가
        createInteractions(users, recipes);
    }

    // 레시피 생성
    private void createSpecificSimpleRecipes(
            List<EatzUser> users,
            List<Ingredient> ingredients,
            List<Kitchenware> kitchenwares,
            List<Tag> tags,
            List<Recipe> recipes) {
        createAndSaveRecipe(
                users.get(0),
                "초간단 계란 프라이",
                "https://youtube.com/egg",
                "https://picsum.photos/id/10/800/800",
                ingredients,
                List.of("계란", "소금"),
                kitchenwares,
                List.of("프라이팬"),
                tags,
                recipes);

        createAndSaveRecipe(
                users.get(1),
                "완벽 반숙 삶은 계란",
                "https://youtube.com/boiled",
                "https://picsum.photos/id/20/800/800",
                ingredients,
                List.of("계란"),
                kitchenwares,
                List.of("냄비"),
                tags,
                recipes);

        createAndSaveRecipe(
                users.get(2),
                "해장용 대파 라면",
                "https://youtube.com/ramen",
                "https://picsum.photos/id/30/800/800",
                ingredients,
                List.of("라면사리", "대파"),
                kitchenwares,
                List.of("냄비"),
                tags,
                recipes);
    }

    private void createAndSaveRecipe(
            EatzUser author,
            String title,
            String url,
            String imageUrl,
            List<Ingredient> allIngredients,
            List<String> targetIngredientNames,
            List<Kitchenware> allKitchenwares,
            List<String> targetKitchenwareNames,
            List<Tag> allTags,
            List<Recipe> recipes) {
        Recipe recipe = Recipe.create(author, title, url, imageUrl, 600, 2, true, title + " 설명", 300, null, null);
        recipe.setCreatedAt(LocalDateTime.now());

        for (String name : targetIngredientNames) {
            Ingredient ingredient = allIngredients.stream().filter(i -> i.getName().equals(name)).findFirst().orElse(allIngredients.get(0));
            recipe.addIngredient(ingredient);
        }

        for (String name : targetKitchenwareNames) {
            Kitchenware kitchenware = allKitchenwares.stream().filter(k -> k.getName().equals(name)).findFirst().orElse(allKitchenwares.get(0));
            recipe.addKitchenware(kitchenware);
        }

        recipe.addTag(allTags.get(0));

        recipeRepository.save(recipe);
        recipes.add(recipe);
    }
    private void createRandomRecipes(
            int count,
            List<EatzUser> users,
            List<Ingredient> ingredients,
            List<Kitchenware> kitchenwares,
            List<Tag> tags,
            List<Recipe> recipes) {
        Random random = new Random();
        String[] adjectives = {"매콤한", "달콤한", "짭짤한", "담백한", "얼큰한", "시원한", "바삭한", "촉촉한", "건강한", "초간단", "자취생 필수", "엄마표", "실패없는"};
        String[] mainItems = {"김치", "돼지고기", "소고기", "닭고기", "두부", "계란", "감자", "참치", "스팸", "어묵", "라면", "떡볶이", "된장", "콩나물"};
        String[] dishTypes = {"찌개", "국", "볶음", "조림", "구이", "튀김", "덮밥", "비빔밥", "전", "찜", "샐러드", "파스타", "샌드위치"};

        for (int i = 0; i < count; i++) {
            String title = String.format("%s %s %s",
                    adjectives[random.nextInt(adjectives.length)],
                    mainItems[random.nextInt(mainItems.length)],
                    dishTypes[random.nextInt(dishTypes.length)]
            );
            EatzUser author = users.get(random.nextInt(users.size()));

            Recipe recipe = Recipe.create(
                    author,
                    title,
                    "https://www.youtube.com/watch?v=random" + i,
                    "https://picsum.photos/seed/" + (i + 100) + "/800/800",
                    (10 + random.nextInt(50)) * 60,
                    1 + random.nextInt(4),
                    true,
                    title + " 만드는 법! 정말 쉽고 맛있어요. 꼭 집에서 만들어서 드셔보시길 바라요! 외식 줄여보자구요 :-)",
                    (5 + random.nextInt(20)) * 60,
                    null,
                    null
            );
            recipe.setCreatedAt(LocalDateTime.now().minusHours(random.nextInt(365 * 24)));

            // 1. 태그: 1~3개
            List<Tag> shuffledTags = new ArrayList<>(tags);
            Collections.shuffle(shuffledTags);
            for (Tag tag : shuffledTags.subList(0, 1 + random.nextInt(3))) {
                recipe.addTag(tag);
            }

            // 2. 재료: 2~6개 (수정됨)
            List<Ingredient> shuffledIngredients = new ArrayList<>(ingredients);
            Collections.shuffle(shuffledIngredients);
            for (Ingredient ingredient : shuffledIngredients.subList(0, 2 + random.nextInt(5))) {
                System.out.println(recipe.getId() + "번 레시피에 ");
                recipe.addIngredient(ingredient);
            }

            // 3. 도구: 2~6개 (수정됨)
            List<Kitchenware> shuffledKitchenwares = new ArrayList<>(kitchenwares);
            Collections.shuffle(shuffledKitchenwares);
            for (Kitchenware kitchenware : shuffledKitchenwares.subList(0, 2 + random.nextInt(5))) {
                recipe.addKitchenware(kitchenware);
            }

            recipeRepository.save(recipe);
            recipes.add(recipe);
        }
    }

    // 좋아요, 댓글, 평가
    private void createInteractions(List<EatzUser> users, List<Recipe> recipes) {
        Random random = new Random();

        String[] commentPool = {
                "정말 맛있어 보이네요! 오늘 저녁 메뉴로 결정했습니다.",
                "우리 집 옆에 사는 진돌이는 줘도 안 먹는다네요~ ^^",
                "대박! 이 비율대로 양념장 만드니까 파는 것보다 맛있어요.",
                "생각보다 시간이 조금 더 걸리긴 했는데 맛은 최고예요!",
                "이딴 걸 해먹으라고 만든 건가요? ㅎㅎ 그냥 제 혀를 깨물겠어요!",
                "이거 진짜 밥도둑입니다 ㅋㅋㅋ 두 그릇 뚝딱했어요.",
                "이걸 먹을 바에 서울역 앞 무료 급식소를 가겠어요! 히히",
                "부모님 해드렸는데 칭찬받았어요! 감사합니다 ㅎㅎ",
                "영상 보고 따라 하니까 진짜 쉽네요. 구독 누르고 갑니다~",
        };

        // 현실적인 평가(한줄평) 템플릿 풀
        String[] ratingReviews = {
                "인상적인 맛 덕분에 음식물 쓰레기 통에 넣느라 유산소 운동을 할 수 있었어요! 아주 대단해요!",
                "기대 이상입니다.",
                "음...... 제가 봤을 때에는 원물의 식감과 고유의 맛을 살리기 힘든 양념이 아쉽게 느껴지네요. 아쉽게도 이번 레시피는 탈락입니다. 수고하셨습니다. 재료의 본질에 대해 좀 더 연구하고, 더 나은 레시피로 다시 찾아뵙길 기대할게요.",
                "조금 아쉬웠어요.",
                "이걸 먹을 바에 우리 집 똘똘이가 매일 아침에 먹는 개껌을 처 씹어 먹겠어요 ㅎㅎ",
                "우리 집 앞 진돌이도 밥 그릇을 깨고 남길 정도의 맛이었어요!"
        };

        for (Recipe recipe : recipes) {
            // 레시피의 '인기 지수'를 0 ~ 100 사이로 랜덤 설정
            int popularityScore = random.nextInt(100);

            // 인기 지수에 비례하여 최대 유저 수(약 34명)만큼 좋아요가 찍힘
            int maxLikes = (popularityScore * users.size()) / 100;
            int actualLikes = random.nextInt(maxLikes + 1);

            List<EatzUser> shuffledForLikes = new ArrayList<>(users);
            Collections.shuffle(shuffledForLikes);

            for (int i = 0; i < actualLikes; i++) {
                likedRecipeRepository.save(LikedRecipe.create(shuffledForLikes.get(i), recipe));
            }

            // 인기 레시피는 최대 10개까지, 비인기 레시피는 0~1개 달림
            int numComments = popularityScore / 10;
            List<EatzUser> shuffledForComments = new ArrayList<>(users);
            Collections.shuffle(shuffledForComments);

            for (int i = 0; i < numComments && i < shuffledForComments.size(); i++) {
                String randomComment = commentPool[random.nextInt(commentPool.length)];
                commentRepository.save(Comment.create(shuffledForComments.get(i), recipe, randomComment));
            }

            // 인기 레시피는 최대 15개까지, 비인기 레시피는 0~2개 달림
            int numRatings = popularityScore / 6;
            List<EatzUser> shuffledForRatings = new ArrayList<>(users);
            Collections.shuffle(shuffledForRatings);

            for (int i = 0; i < numRatings && i < shuffledForRatings.size(); i++) {
                // 인기 지수가 높은 레시피는 3~5점, 낮으면 1~5점 랜덤
                int score = (popularityScore > 60) ? (3 + random.nextInt(3)) : (1 + random.nextInt(5));
                String randomReview = ratingReviews[random.nextInt(ratingReviews.length)];

                ratingRepository.save(Rating.create(shuffledForRatings.get(i), recipe, score, randomReview));
            }
        }
    }

    /**
     *     SPAM("홍보성 스팸 정보가 포함됨"),
     *     ABUSIVE_LANGUAGE("욕설, 비하, 혐오 표현이 포함됨"),
     *     SENSITIVE_OR_INAPPROPRIATE_CONTENT("민감하거나 부적절한 콘텐츠가 포함됨"),
     *     COPYRIGHT_INFRINGEMENT("저작권을 침해하는 콘텐츠가 포함됨"),
     *     OTHER("기타");
     */
    private void createReportReasons() {
        ReportCategory categorySpam = ReportCategory.create("SPAM", "홍보성 스팸 정보가 포함됨");
        ReportCategory categoryAbusiveLanguage = ReportCategory.create("ABUSIVE_LANGUAGE", "욕설, 비하, 혐오 표현이 포함됨");
        ReportCategory categorySensitiveOrInappropriateContent = ReportCategory.create("SENSITIVE_OR_INAPPROPRIATE_CONTENT", "민감하거나 부적절한 콘텐츠가 포함됨");
        ReportCategory categoryCopyrightInfringement = ReportCategory.create("COPYRIGHT_INFRINGEMENT", "저작권을 침해하는 콘텐츠가 포함됨");
        ReportCategory categoryOther = ReportCategory.create("OTHER", "기타");

        reportCategoryRepository.save(categorySpam);
        reportCategoryRepository.save(categoryAbusiveLanguage);
        reportCategoryRepository.save(categorySensitiveOrInappropriateContent);
        reportCategoryRepository.save(categoryCopyrightInfringement);
        reportCategoryRepository.save(categoryOther);
    }

    private List<EatzUser> createUsers() {
        List<EatzUser> users = new ArrayList<>();
        String password = passwordEncoder.encode("1q2w3e4r!");

        // 1. 메인 테스트 유저 4명
        users.add(userRepository.save(EatzUser.createAdmin("heextory", "heextory@eatz.io", password, "https://picsum.photos/id/101/200/200")));
        users.add(userRepository.save(EatzUser.createMember("curve4403", "curve4403@eatz.io", password, "https://picsum.photos/id/102/200/200")));
        users.add(userRepository.save(EatzUser.createMember("curvefxcx330", "curve4403333@eatz.io", password, "https://picsum.photos/id/103/200/200")));
        users.add(userRepository.save(EatzUser.createMember("text.ccnt", "test_account3@eatz.io", password, "https://picsum.photos/id/104/200/200")));

        // 2. 다이나믹한 상호작용을 위한 '가짜 커뮤니티 유저' 30명 대량 생성
        for (int i = 1; i <= 30; i++) {
            String dummyUsername = "eatz_user_" + i;
            String dummyEmail = dummyUsername + "@eatz.io";
            String dummyImage = "https://picsum.photos/seed/user" + i + "/200/200";
            users.add(userRepository.save(EatzUser.createMember(dummyUsername, dummyEmail, password, dummyImage)));
        }

        return users;
    }

    private List<Ingredient> createIngredients() {
        List<String> ingredientNames = List.of(
                "닭 다리살", "닭 가슴살", "돼지 목살", "돼지 앞다리살", "삼겹살", "소 등심", "소 안창살", "차돌박이", "오징어", "새우", "조개", "참치캔", "스팸",
                "양파", "대파", "쪽파", "마늘", "당근", "감자", "고구마", "애호박", "오이", "청양고추", "콩나물", "숙주", "배추", "무", "양배추", "상추", "깻잎", "토마토",
                "두부", "순두부", "계란", "우유", "체다 치즈", "모짜렐라 치즈", "버터",
                "고추장", "된장", "간장", "설탕", "소금", "후추", "고춧가루", "다진마늘", "참기름", "식용유", "밀가루", "부침 가루", "김치", "밥", "라면 사리", "떡국 떡"
        );
        List<Ingredient> ingredients = new ArrayList<>();
        for (String name : ingredientNames) {
            ingredients.add(ingredientRepository.save(Ingredient.create(name)));
        }
        return ingredients;
    }

    private List<Kitchenware> createKitchenwares() {
        List<String> kitchenwareNames = List.of("전자레인지", "에어프라이어", "프라이팬", "냄비", "가스레인지", "인덕션", "찜기", "믹서기", "오븐", "채반");
        List<Kitchenware> kitchenwares = new ArrayList<>();
        for (String name : kitchenwareNames) {
            kitchenwares.add(kitchenwareRepository.save(Kitchenware.create(name)));
        }
        return kitchenwares;
    }

    private void createThemesAndTags() {
        Theme themeA = Theme.create("나라 및 지역");
        themeA.addTag(tagRepository.save(Tag.create("한식", "한국 요리", "🇰🇷")));
        themeA.addTag(tagRepository.save(Tag.create("일식", "일본 요리", "🇯🇵")));
        themeA.addTag(tagRepository.save(Tag.create("중식", "중국 요리", "🇨🇳")));
        themeA.addTag(tagRepository.save(Tag.create("이탈리아", "이탈리아 요리", "🇮🇹")));
        themeA.addTag(tagRepository.save(Tag.create("프랑스", "프랑스 요리", "🇫🇷")));
        themeA.addTag(tagRepository.save(Tag.create("베트남", "베트남 요리", "🇻🇳")));
        themeRepository.save(themeA);

        Theme themeB = Theme.create("상황 및 목적");
        themeB.addTag(tagRepository.save(Tag.create("반찬", "식탁을 풍성하게", "🍚")));
        themeB.addTag(tagRepository.save(Tag.create("안주", "술과 곁들이는", "🍻")));
        themeB.addTag(tagRepository.save(Tag.create("식이요법", "건강 관리", "🥗")));
        themeB.addTag(tagRepository.save(Tag.create("야식", "밤에 즐기는 별미", "🌙")));
        themeB.addTag(tagRepository.save(Tag.create("초간단", "빠르게 만드는", "⏱️")));
        themeB.addTag(tagRepository.save(Tag.create("건강한", "영양 가득", "💪")));
        themeRepository.save(themeB);

        Theme themeC = Theme.create("요리 방법");
        themeC.addTag(tagRepository.save(Tag.create("전자레인지", "간편한 조리", "♨️")));
        themeC.addTag(tagRepository.save(Tag.create("에어프라이어", "바삭한 튀김", "🌬️")));
        themeC.addTag(tagRepository.save(Tag.create("찜", "촉촉한 요리", "🍲")));
        themeC.addTag(tagRepository.save(Tag.create("볶음", "빠른 요리", "🍳")));
        themeC.addTag(tagRepository.save(Tag.create("조림", "깊은 맛", "🥘")));
        themeC.addTag(tagRepository.save(Tag.create("튀김", "겉바속촉", "🍤")));
        themeC.addTag(tagRepository.save(Tag.create("구이", "불맛", "🥩")));
        themeRepository.save(themeC);
    }

}