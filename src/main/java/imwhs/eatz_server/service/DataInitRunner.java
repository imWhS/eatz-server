package imwhs.eatz_server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeInitDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ReportCategoryRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.tag.TagRepository;
import imwhs.eatz_server.repository.tag.ThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Order(2)
@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitRunner implements ApplicationRunner {

    private final EatzUserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final TagRepository tagRepository;
    private final ThemeRepository themeRepository;
    private final KitchenwareRepository kitchenwareRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initKitchenwares();
        initIngredients();
        initThemesAndTags();
        initRecipes();
        initReportReasons();
    }

    private void initRecipes() {
        if (recipeRepository.count() > 0) {
            log.info("레시피 데이터가 이미 저장돼있어서, 레시피 일괄 초기화를 진행하지 않아요.");
            return;
        }

        try {
            // AdminInitializer가 생성해 둔 관리자 계정을 작성자로 사용합니다.
            EatzUser author = userRepository.findByEmailAndDeletedAtIsNull("heextory@eatz.io")
                    .orElseThrow(() -> new IllegalStateException("레시피 작성용 계정이 없어요. AdminInitializer가 먼저 실행되어야 해요."));

            ClassPathResource resource = new ClassPathResource("EATZ-recipes.json");
            ObjectMapper mapper = new ObjectMapper();
            List<RecipeInitDto> recipeDtos = mapper.readValue(resource.getInputStream(), new TypeReference<>() {});

            for (RecipeInitDto dto : recipeDtos) {
                Recipe recipe = Recipe.create(
                        author,
                        dto.getTitle(),
                        dto.getUrl(),
                        dto.getImageUrl(),
                        dto.getCookingTime(),
                        dto.getServings(),
                        dto.getIsCommentEnabled(),
                        dto.getDescription(),
                        dto.getPrepTime(),
                        dto.getCreatorName(),
                        dto.getCreatorUrl()
                );

                // 재료 매핑
                if (dto.getIngredientNames() != null) {
                    for (String ingName : dto.getIngredientNames()) {
                        ingredientRepository.findFirstByNameAndDeletedAtIsNull(ingName)
                                .ifPresent(recipe::addIngredient);
                    }
                }

                // 도구 매핑
                if (dto.getKitchenwareNames() != null) {
                    for (String kitName : dto.getKitchenwareNames()) {
                        kitchenwareRepository.findFirstByNameAndDeletedAtIsNull(kitName)
                                .ifPresent(recipe::addKitchenware);
                    }
                }

                // 태그 매핑
                if (dto.getTagNames() != null) {
                    for (String tagName : dto.getTagNames()) {
                        tagRepository.findFirstByNameAndDeletedAtIsNull(tagName)
                                .ifPresent(recipe::addTag);
                    }
                }

                recipeRepository.save(recipe);
            }

            log.info("EATZ 초기 레시피 {}개 일괄 초기화를 완료했어요!", recipeDtos.size());

        } catch (Exception e) {
            log.error("EATZ 레시피 일괄 초기화 중 오류가 발생했어요. | {}", e.getMessage(), e);
        }
    }

    private void initReportReasons() {
        ReportCategory categorySpam = ReportCategory.create(
                "SPAM",
                "홍보성 스팸 정보가 포함됨");
        ReportCategory categoryAbusiveLanguage = ReportCategory.create(
                "ABUSIVE_LANGUAGE",
                "욕설, 비하, 혐오 표현이 포함됨");
        ReportCategory categorySensitiveOrInappropriateContent = ReportCategory.create(
                "SENSITIVE_OR_INAPPROPRIATE_CONTENT",
                "민감하거나 부적절한 콘텐츠가 포함됨");
        ReportCategory categoryCopyrightInfringement = ReportCategory.create(
                "COPYRIGHT_INFRINGEMENT",
                "저작권을 침해하는 콘텐츠가 포함됨");
        ReportCategory categoryOther = ReportCategory.create(
                "OTHER",
                "기타");

        reportCategoryRepository.save(categorySpam);
        reportCategoryRepository.save(categoryAbusiveLanguage);
        reportCategoryRepository.save(categorySensitiveOrInappropriateContent);
        reportCategoryRepository.save(categoryCopyrightInfringement);
        reportCategoryRepository.save(categoryOther);
    }

    private void initThemesAndTags() {
        if (tagRepository.count() > 0 || themeRepository.count() > 0) {
            log.info("테마 및 태그 데이터가 이미 저장돼있어서, 재료 일괄 초기화를 진행하지 않아요.");
            return;
        }

        Theme themeA = Theme.create("나라 및 지역");
        themeA.addTag(tagRepository.save(Tag.create("한식", "한국 요리", "🇰🇷")));
        themeA.addTag(tagRepository.save(Tag.create("일식", "일본 요리", "🇯🇵")));
        themeA.addTag(tagRepository.save(Tag.create("중식", "중국 요리", "🇨🇳")));
        themeA.addTag(tagRepository.save(Tag.create("이탈리아", "이탈리아 요리", "🇮🇹")));
        themeA.addTag(tagRepository.save(Tag.create("프랑스", "프랑스 요리", "🇫🇷")));
        themeA.addTag(tagRepository.save(Tag.create("베트남", "베트남 요리", "🇻🇳")));
        themeA.addTag(tagRepository.save(Tag.create("타이", "태국 요리", "🇹🇭")));
        themeA.addTag(tagRepository.save(Tag.create("멕시칸", "멕시코 요리", "🌮")));
        themeA.addTag(tagRepository.save(Tag.create("아메리칸", "미국 요리", "🍔")));
        themeA.addTag(tagRepository.save(Tag.create("양식", "양식 요리", "🍝")));
        themeA.addTag(tagRepository.save(Tag.create("퓨전", "경계를 넘어 새로운 맛을 만들어낸 요리", "🛸")));
        themeRepository.save(themeA);

        Theme themeB = Theme.create("상황 및 목적");
        themeB.addTag(tagRepository.save(Tag.create("반찬", "식탁을 보다 풍성하게 만들어주는 요리", "🍚")));
        themeB.addTag(tagRepository.save(Tag.create("안주", "술과 곁들이기 좋은 요리", "🍻")));
        themeB.addTag(tagRepository.save(Tag.create("간식", "끼니 사이에 간단하게 즐기기 좋은 요리", "🍰")));
        themeB.addTag(tagRepository.save(Tag.create("야식", "밤에 즐기는 별미", "🌙")));
        themeB.addTag(tagRepository.save(Tag.create("초간단", "빠르게 만드는 요리", "⏱️")));
        themeB.addTag(tagRepository.save(Tag.create("혼밥", "나를 위한 한 끼 요리", "🎧")));
        themeB.addTag(tagRepository.save(Tag.create("홈 파티", "소중한 사람을 위한 접대용 요리", "🎉")));
        themeB.addTag(tagRepository.save(Tag.create("도시락", "언제 어디서나 즐길 수 있는 요리", "🍱")));
        themeB.addTag(tagRepository.save(Tag.create("해장", "숙취가 풀리는 국물 요리", "🥵")));
        themeB.addTag(tagRepository.save(Tag.create("캠핑", "야외에서 즐기기 좋은 요리", "⛺")));
        themeB.addTag(tagRepository.save(Tag.create("디저트", "달콤한 마무리를 위한 요리", "🍰")));
        themeB.addTag(tagRepository.save(Tag.create("브런치", "아침 잠을 깨워주는 요리", "🥞")));
        themeRepository.save(themeB);

        Theme themeC = Theme.create("요리 종류 및 방법");
        themeC.addTag(tagRepository.save(Tag.create("전자레인지", "간편하게 조리할 수 있는 요리", "♨️")));
        themeC.addTag(tagRepository.save(Tag.create("에어프라이어", "간편하게 즐길 수 있는 바삭한 튀김 요리", "🌬️")));
        themeC.addTag(tagRepository.save(Tag.create("찜", "촉촉한 요리", "🍲")));
        themeC.addTag(tagRepository.save(Tag.create("볶음", "빠른 요리", "🍳")));
        themeC.addTag(tagRepository.save(Tag.create("조림", "깊은 맛을 느낄 수 있는 요리", "🥘")));
        themeC.addTag(tagRepository.save(Tag.create("국수", "목구멍으로 면을 후루룩 넘기는 맛을 즐길 수 있는 요리", "🥘")));
        themeC.addTag(tagRepository.save(Tag.create("국물", "속을 따뜻하게 데워주는 요리", "🥘")));
        themeC.addTag(tagRepository.save(Tag.create("튀김", "바삭한 쾌감을 느낄 수 있는 요리", "🍤")));
        themeC.addTag(tagRepository.save(Tag.create("구이", "불맛을 느낄 수 있는 요리", "🥩")));
        themeC.addTag(tagRepository.save(Tag.create("덮밥", "빠른 요리", "🍳")));
        themeC.addTag(tagRepository.save(Tag.create("국/탕", "속까지 따뜻하게 데워주는 요리", "🥘")));
        themeC.addTag(tagRepository.save(Tag.create("찌개", "속까지 따뜻하게 데워주는 요리", "🥘")));
        themeC.addTag(tagRepository.save(Tag.create("파스타", "이탈리아를 대표하는 요리", "🍝")));
        themeC.addTag(tagRepository.save(Tag.create("무침", "신선한 재료를 맛있게 버무린 요리", "🥗")));
        themeC.addTag(tagRepository.save(Tag.create("부침/전", "노릇노릇한 요리", "🥞")));
        themeC.addTag(tagRepository.save(Tag.create("베이킹", "오븐을 활용한 요리", "🍞")));
        themeC.addTag(tagRepository.save(Tag.create("스테이크", "육식파들이 환장할 요리", "🥩")));
        themeC.addTag(tagRepository.save(Tag.create("생식", "불을 쓰지 않는 요리", "🧊")));
        themeRepository.save(themeC);

        Theme themeD = Theme.create("식단 및 체질");
        themeD.addTag(tagRepository.save(Tag.create("건강식", "영양 밸런스를 지키기 좋은 요리", "💪")));
        themeD.addTag(tagRepository.save(Tag.create("식이 요법(다이어트)", "건강한 몸을 만들 수 있게 도와주는 요리", "🏃‍♂️")));
        themeD.addTag(tagRepository.save(Tag.create("비건", "동물성 재료 ZERO", "🌱")));
        themeD.addTag(tagRepository.save(Tag.create("저탄고지", "키토제닉 식단을 위한 요리", "🥓")));
        themeD.addTag(tagRepository.save(Tag.create("고단백", "득근득근", "🥚")));
        themeD.addTag(tagRepository.save(Tag.create("글루텐프리", "속이 편안한 요리", "🌾")));
        themeD.addTag(tagRepository.save(Tag.create("저당", "혈당 스파이크를 방지할 수 있게 도와주는 요리", "📉")));
        themeRepository.save(themeD);

        Theme themeE = Theme.create("맛 및 취향");
        themeE.addTag(tagRepository.save(Tag.create("매콤한 맛", "스트레스가 확 풀리는 화끈함", "🔥")));
        themeE.addTag(tagRepository.save(Tag.create("순한 맛", "자극 없이 부드럽고 편안한 맛", "☁️")));
        themeE.addTag(tagRepository.save(Tag.create("단짠", "절대 실패할 수 없는 마성의 조합", "🧂")));
        themeE.addTag(tagRepository.save(Tag.create("새콤달콤한 맛", "집 나간 입맛도 돌아오는 상큼함", "🍋")));
        themeE.addTag(tagRepository.save(Tag.create("담백한 맛", "재료 본연의 맛을 살린 깔끔함", "🍵")));
        themeE.addTag(tagRepository.save(Tag.create("고소한 맛", "입 안 가득 퍼지는 깊은 풍미", "🥜")));
        themeE.addTag(tagRepository.save(Tag.create("마라 맛", "혀 끝을 자극하는 중독적인 얼얼함", "🌶️")));
        themeE.addTag(tagRepository.save(Tag.create("불맛", "입안을 꽉 채우는 스모키한 향", "🪵")));
        themeE.addTag(tagRepository.save(Tag.create("감칠 맛", "자꾸만 생각나는 깊은 여운", "🤤")));
        themeE.addTag(tagRepository.save(Tag.create("달달한 맛", "기분까지 좋아지는 달콤함", "🍯")));
        themeE.addTag(tagRepository.save(Tag.create("짭짤한 맛", "밥 도둑 혹은 안주로 제격인 맛", "🥨")));
        themeE.addTag(tagRepository.save(Tag.create("향신료", "이국적인 향이 매력적인 맛", "🌿")));

        themeRepository.save(themeE);
    }

    private void initKitchenwares() {
        if (kitchenwareRepository.count() > 0) {
            log.info("도구 데이터가 이미 저장돼있어서, 재료 일괄 초기화를 진행하지 않아요.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("EATZ-kitchenwares.csv");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String row;
            boolean isFirstRow = true;

            while ((row = br.readLine()) != null) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // 빈 행은 무시합니다.
                if (row.trim().isEmpty()) continue;


                // CSV 내 각 행의 ','를 기준으로 문자열을 나눠서, 개별 배열의 요소로 포함시킵니다.
                String[] columns = row.split(",", -1);

                // CSV 내 각 행 별로 최소한 도구 이름과 커플링 여부까지 총 2개의 요소는 반드시 있어야 합니다.
                // 해당 행이 이를 충족하지 않은 경우 다음 행으로 넘어갑니다.
                if (columns.length < 2) {
                    log.warn("잘못된 형식인 도구 데이터 행을 스킵할게요. | {}", row);
                    continue;
                }

                // CSV 내 각 행의 첫 번째 열에 위치한 문자열을 도구 이름으로 읽어옵니다.
                String kitchenwareName = columns[0].trim();

                // CSV 내 각 행의 두 번째 열에 위치한 문자열을 도구 이미지 URL로 읽어옵니다.
                String kitchenwareImageUrl = columns[1].trim();

                // 해당 행에서 더 이상 순회할 열(도구의 이름)이 없다면, 다음 행으로 넘어갑니다.
                if (kitchenwareName.isEmpty()) continue;

                findOrCreateKitchenware(kitchenwareName, kitchenwareImageUrl);
            }

            log.info("EATZ 도구 일괄 초기화를 완료했어요!");
        } catch (Exception e) {
            log.error("EATZ 도구 일괄 초기화 중 오류가 발생했어요. | {}", e.getMessage(), e);
        }
    }

    private void initIngredients() {
        if (ingredientRepository.count() > 0) {
            log.info("재료 데이터가 이미 저장돼있어서, 재료 일괄 초기화를 진행하지 않아요.");
            return;
        }

        // 재료 목록을 포함하는 CSV 파일 resources/ingredients.csv를 읽어옵니다.
        ClassPathResource resource = new ClassPathResource("EATZ-ingredients.csv");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String row;
            boolean isFirstRow = true;

            while ((row = br.readLine()) != null) {
                // 첫 행에 위치한 헤더는 자동 스킵합니다.
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // 빈 행은 무시합니다.
                if (row.trim().isEmpty()) continue;

                // CSV 내 각 행의 ','를 기준으로 문자열을 나눠서, 개별 배열의 요소로 포함시킵니다. 빈 문자열도 포함시키기 위해 -1 옵션을 사용합니다.
                // Ex. 신선 식품,육류,소,등심,,TRUE -> ["신선 식품", "육류", "소", "등심", "", "TRUE"]
                String[] columns = row.split(",", -1);

                // CSV 내 각 행 별로 최소한 재료 이름과 상위 재료와의 커플링 여부까지 총 2개의 요소는 반드시 있어야 합니다.
                // 해당 행이 이를 충족하지 않은 경우 다음 행으로 넘어갑니다.
                if (columns.length < 2) {
                    log.warn("잘못된 형식인 재료 데이터 행을 스킵할게요. | {}", row);
                    continue;
                }

                // CSV 내 각 행의 맨 마지막 열에 해당하는 상위 재료와의 커플링 여부를 추출해 boolean 타입의 변수에 할당합니다.
                String isParentCoupledColumn = columns[columns.length - 1].trim();
                boolean isParentCoupled = Boolean.parseBoolean(isParentCoupledColumn);

                Ingredient parent = null;

                // 행의 첫 번째 열부터 시작해 맨 마지막 열을 제외한 모든 열을 순회하며, 재료 이름을 가져와 저장합니다.
                for (int i = 0; i < columns.length - 1; i++) {
                    String ingredientName = columns[i].trim();

                    // 해당 행에서 더 이상 순회할 열(하위 재료의 이름)이 없다면, 다음 행으로 넘어갑니다.
                    if (ingredientName.isEmpty()) break;

                    // 현재 순회 중인 열이 마지막으로 최하위 재료 이름을 포함하는 타깃 재료에 해당하는지 확인합니다.
                    boolean isLastColumn = (i == columns.length - 2) || columns[i + 1].trim().isEmpty();

                    // 타깃 재료에 대한 엔티티 인스턴스를 가져옵니다.
                    Ingredient current = findOrCreateIngredient(ingredientName, parent);

                    if (isLastColumn) {
                        // 타깃 재료에 해당하면, 더 이상 순회할 하위 재료가 없기 때문에
                        // 재료 엔티티 인스턴스에 상위 재료와의 커플링 여부를 설정합니다.
                        current.updateIsParentCoupled(isParentCoupled);
                    } else {
                        // 루트 재료부터 타깃 재료 사이에 해당하는 모든 재료는 일괄 false로 설정합니다.
                        current.updateIsParentCoupled(false);
                    }

                    // 방금 찾거나 생성한 재료 엔티티 인스턴스를를 다음 하위 계층 재료의 상위 계층 재료로 설정합니다.
                    parent = current;
                }
            }
            log.info("EATZ 재료 일괄 초기화를 완료했어요!");

        } catch (Exception e) {
            log.error("EATZ 재료 일괄 초기화 중 오류가 발생했어요. | {}", e.getMessage(), e);
        }
    }

    private Ingredient findOrCreateIngredient(String name, Ingredient parent) {
        Optional<Ingredient> optionalIngredient;

        if (parent == null) {
            optionalIngredient = ingredientRepository.findByNameAndParentIdIsNullAndDeletedAtIsNull(name);
        } else {
            optionalIngredient = ingredientRepository.findByNameAndParentIdAndDeletedAtIsNull(name, parent.getId());
        }

        // 이미 데이터베이스 혹은 영속성 컨텍스트에 존재하면, 가져온 엔티티 인스턴스를 그대로 반환합니다.
        if (optionalIngredient.isPresent()) {
            return optionalIngredient.get();
        }

        // 데이터베이스에 존재하지 않으면 엔티티 인스턴스를 생성하고, 상위 재료를 설정한 후 반환합니다.
        Ingredient newIngredient = Ingredient.create(name);
        if (parent != null) {
            newIngredient.setParent(parent);
        }

        return ingredientRepository.save(newIngredient);
    }

    private Kitchenware findOrCreateKitchenware(String name, String imageUrl) {
        Optional<Kitchenware> optionalKitchenware = kitchenwareRepository.findByNameAndDeletedAtIsNull(name);

        // 이미 데이터베이스 혹은 영속성 컨텍스트에 존재하면, 가져온 엔티티 인스턴스를 그대로 반환합니다.
        if (optionalKitchenware.isPresent()) {
            return optionalKitchenware.get();
        }

        Kitchenware kitchenware = Kitchenware.create(name, imageUrl);
        return kitchenwareRepository.save(kitchenware);
    }

}
