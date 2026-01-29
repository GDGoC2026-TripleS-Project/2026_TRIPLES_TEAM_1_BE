package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
import org.springframework.stereotype.Component;

@Component
public class CalorieGuidePolicy {

    public CalorieGuideDto from(double calories) {

        if (calories <= 150) {
            return guide("부담 없이 넘어갈 수 있는 선택이에요", 10, 5);
        }

        if (calories <= 300) {
            return guide("이 정도는 무리 없는 편이에요", 20, 10);
        }

        if (calories <= 500) {
            return guide("여기서부터는 선택의 무게가 느껴져요", 35, 20);
        }

        if (calories <= 700) {
            return guide("여기서부터는 조금 고민해볼 만해요", 50, 35);
        }

        if (calories <= 900) {
            return guide("이 선택은 오늘의 비중이 꽤 커요", 70, 45);
        }

        return guide("이 정도면 오늘에선 큰 편이에요", 90, 60);
    }

    private CalorieGuideDto guide(String message, int walking, int activity) {
        return CalorieGuideDto.builder()
                .message(message)
                .walkingMinutes(walking)
                .activityMinutes(activity)
                .build();
    }
}
