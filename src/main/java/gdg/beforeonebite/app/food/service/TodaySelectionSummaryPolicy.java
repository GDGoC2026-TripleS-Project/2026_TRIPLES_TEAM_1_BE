package gdg.beforeonebite.app.food.service;

import org.springframework.stereotype.Component;

@Component
public class TodaySelectionSummaryPolicy {

    public TodaySummary from(double totalCalories) {
        if (totalCalories <= 150) {
            return new TodaySummary("오늘은 가볍게 선택했어요", 10);
        }
        if (totalCalories <= 300) {
            return new TodaySummary("오늘은 무리 없는 선택이었어요", 15);
        }
        if (totalCalories <= 500) {
            return new TodaySummary("오늘의 선택이 조금은 느껴져요", 20);
        }
        if (totalCalories <= 700) {
            return new TodaySummary("오늘 선택의 비중이 꽤 있어요", 25);
        }

        return new TodaySummary("오늘 선택이 큰 비중을 차지해요", 30);
    }

    public record TodaySummary(String message, int walkingMinutes) {
    }
}
