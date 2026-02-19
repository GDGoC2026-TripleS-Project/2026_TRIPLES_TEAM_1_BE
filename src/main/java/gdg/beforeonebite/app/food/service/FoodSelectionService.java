package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.auth.repository.UserRepository;
import gdg.beforeonebite.app.food.domain.FoodBrand;
import gdg.beforeonebite.app.food.domain.FoodSelection;
import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
import gdg.beforeonebite.app.food.dto.FoodSelectionCreateResponse;
import gdg.beforeonebite.app.food.dto.FoodSelectionPageResponse;
import gdg.beforeonebite.app.food.dto.FoodSelectionResponse;
import gdg.beforeonebite.app.food.repository.FoodBrandRepository;
import gdg.beforeonebite.app.food.repository.FoodSelectionRepository;
import gdg.beforeonebite.app.food.util.SelectionTimeLabeler;
import gdg.beforeonebite.global.exception.BadRequestException;
import gdg.beforeonebite.global.exception.ErrorMessage;
import gdg.beforeonebite.global.exception.NotFoundException;
import gdg.beforeonebite.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodSelectionService {

    private static final int RETENTION_DAYS = 60;

    private final FoodSelectionRepository foodSelectionRepository;
    private final FoodBrandRepository foodBrandRepository;
    private final UserRepository userRepository;
    private final CalorieGuidePolicy calorieGuidePolicy;
    private final Clock clock;
    private final TodaySelectionSummaryPolicy todaySelectionSummaryPolicy;
    private final SelectionConfirmPolicy selectionConfirmPolicy;

    @Transactional
    public FoodSelectionCreateResponse selectFood(AuthUser authUser, Long foodBrandId) {
        if (authUser == null) {
            throw new UnauthorizedException(ErrorMessage.USER_NOT_EXIST);
        }
        if (foodBrandId == null) {
            throw new BadRequestException(ErrorMessage.INVALID_REQUEST);
        }

        FoodBrand foodBrand = foodBrandRepository.findOneWithFoodAndBrandById(foodBrandId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        LocalDate today = LocalDate.now(clock);
        LocalDateTime now = LocalDateTime.now(clock);

        FoodSelection selection = FoodSelection.builder()
                .user(userRepository.getReferenceById(authUser.id()))
                .foodBrand(foodBrand)
                .selectedDate(today)
                .selectedAt(now)
                .build();

        FoodSelection saved = foodSelectionRepository.save(selection);

        var result = selectionConfirmPolicy.from(foodBrand.getCalories());

        return FoodSelectionCreateResponse.builder()
                .selectionId(saved.getId())
                .message(result.message())
                .walkingMinutes(result.walkingMinutes())
                .build();
    }


    @Transactional(readOnly = true)
    public List<FoodSelectionResponse> getSelections(AuthUser authUser) {
        if (authUser == null) {
            return List.of();
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate fromDate = today.minusDays(RETENTION_DAYS - 1);

        return foodSelectionRepository
                .findAllWithFoodBrandByUserAndDateFrom(authUser.id(), fromDate)
                .stream()
                .map(s -> toResponse(s, today))
                .toList();
    }

    @Transactional
    public void deleteTodaySelection(AuthUser authUser, Long selectionId) {
        if (authUser == null) {
            throw new UnauthorizedException(ErrorMessage.USER_NOT_EXIST);
        }

        if (selectionId == null) {
            throw new BadRequestException(ErrorMessage.INVALID_REQUEST);
        }

        FoodSelection foodSelection = foodSelectionRepository.findById(selectionId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.SELECTION_NOT_FOUND));

        if (!foodSelection.getUser().getId().equals(authUser.id())) {
            throw new UnauthorizedException(ErrorMessage.INVALID_REQUEST);
        }

        LocalDate today = LocalDate.now(clock);

        if (!today.equals(foodSelection.getSelectedDate())) {
            throw new BadRequestException(ErrorMessage.TODAY_ONLY_DELETABLE);
        }

        foodSelectionRepository.delete(foodSelection);
    }

    private FoodSelectionResponse toResponse(FoodSelection foodSelection, LocalDate today) {
        FoodBrand foodBrand = foodSelection.getFoodBrand();

        CalorieGuideDto guide = calorieGuidePolicy.from(foodBrand.getCalories());

        String dayLabel = SelectionTimeLabeler.dayLabel(foodSelection.getSelectedDate(), today);
        String timeLabel = SelectionTimeLabeler.timeLabel(foodSelection.getSelectedAt());

        return FoodSelectionResponse.builder()
                .selectionId(foodSelection.getId())
                .foodName(foodBrand.getFood().getFoodName())
                .walkingMinutes(guide.walkingMinutes())
                .activityMinutes(guide.activityMinutes())
                .dayLabel(dayLabel)
                .timeLabel(timeLabel)
                .build();
    }

    @Transactional(readOnly = true)
    public FoodSelectionPageResponse getSelectionPage(AuthUser authUser) {
        if (authUser == null) {
            return FoodSelectionPageResponse.builder()
                    .recent(null)
                    .todaySummary(FoodSelectionPageResponse.TodaySummary.builder()
                            .totalCalories(0)
                            .message("")
                            .walkingMinutes(0)
                            .build())
                    .todaySelections(List.of())
                    .groups(List.of())
                    .build();
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate fromDate = today.minusDays(RETENTION_DAYS - 1);

        List<FoodSelection> all = foodSelectionRepository
                .findAllWithFoodBrandByUserAndDateFrom(authUser.id(), fromDate);

        if (all.isEmpty()) {
            return FoodSelectionPageResponse.builder()
                    .recent(null)
                    .todaySummary(FoodSelectionPageResponse.TodaySummary.builder()
                            .totalCalories(0)
                            .message("아직 선택 기록이 없어요")
                            .walkingMinutes(0)
                            .build())
                    .todaySelections(List.of())
                    .groups(List.of())
                    .build();
        }

        FoodSelection recentEntity = all.getFirst();
        FoodSelectionResponse recent = toResponse(recentEntity, today);

        List<FoodSelection> todayEntities = all.stream()
                .filter(s -> today.equals(s.getSelectedDate()))
                .toList();

        double todayTotalCalories = todayEntities.stream()
                .mapToDouble(s -> s.getFoodBrand().getCalories())
                .sum();

        var summary = todaySelectionSummaryPolicy.from(todayTotalCalories);

        List<FoodSelectionResponse> todaySelections = todayEntities.stream()
                .filter(s -> !s.getId().equals(recentEntity.getId()))
                .map(s -> toResponse(s, today))
                .toList();

        var grouped = all.stream()
                .filter(s -> !today.equals(s.getSelectedDate()))
                .collect(java.util.stream.Collectors.groupingBy(
                        FoodSelection::getSelectedDate,
                        java.util.LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        List<FoodSelectionPageResponse.DayGroup> groups = grouped.entrySet().stream()
                .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
                .map(e -> FoodSelectionPageResponse.DayGroup.builder()
                        .dayLabel(SelectionTimeLabeler.dayLabel(e.getKey(), today)) // 어제/날짜
                        .items(e.getValue().stream().map(s -> toResponse(s, today)).toList())
                        .build()
                )
                .toList();

        return FoodSelectionPageResponse.builder()
                .recent(recent)
                .todaySummary(FoodSelectionPageResponse.TodaySummary.builder()
                        .totalCalories(todayTotalCalories)
                        .message(summary.message())
                        .walkingMinutes(summary.walkingMinutes())
                        .build())
                .todaySelections(todaySelections)
                .groups(groups)
                .build();
    }

}
