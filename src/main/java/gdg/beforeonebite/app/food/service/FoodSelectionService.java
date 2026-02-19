package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.auth.repository.UserRepository;
import gdg.beforeonebite.app.food.domain.FoodBrand;
import gdg.beforeonebite.app.food.domain.FoodSelection;
import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
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

    @Transactional
    public void selectFood(AuthUser authUser, Long foodBrandId) {
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

        foodSelectionRepository.save(selection);
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
}
