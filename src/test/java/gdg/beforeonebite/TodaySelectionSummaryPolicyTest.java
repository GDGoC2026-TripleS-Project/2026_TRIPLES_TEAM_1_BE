package gdg.beforeonebite;

import gdg.beforeonebite.app.food.service.TodaySelectionSummaryPolicy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodaySelectionSummaryPolicyTest {

    private final TodaySelectionSummaryPolicy policy = new TodaySelectionSummaryPolicy();

    @Test
    void summaryPolicyTest() {
        var result = policy.from(600);

        assertThat(result.walkingMinutes()).isEqualTo(25);
        assertThat(result.message()).contains("꽤");
    }
}
