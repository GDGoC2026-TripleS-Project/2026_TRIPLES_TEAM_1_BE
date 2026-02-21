package gdg.beforeonebite;

import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
import gdg.beforeonebite.app.food.service.CalorieGuidePolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalorieGuidePolicyTest {

    private final CalorieGuidePolicy policy = new CalorieGuidePolicy();

    @Test
    @DisplayName("150kcal 이하는 가장 가벼운 가이드 반환")
    void under150() {
        CalorieGuideDto result = policy.from(100);

        assertThat(result.message()).contains("부담 없이");
        assertThat(result.walkingMinutes()).isEqualTo(10);
        assertThat(result.activityMinutes()).isEqualTo(5);
    }

    @Test
    @DisplayName("900kcal 초과 시 마지막 구간 반환")
    void over900() {
        CalorieGuideDto result = policy.from(950);

        assertThat(result.walkingMinutes()).isEqualTo(90);
        assertThat(result.activityMinutes()).isEqualTo(60);
    }
}
