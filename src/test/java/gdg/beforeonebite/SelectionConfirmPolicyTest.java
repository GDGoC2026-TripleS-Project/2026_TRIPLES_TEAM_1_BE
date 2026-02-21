package gdg.beforeonebite;

import gdg.beforeonebite.app.food.service.SelectionConfirmPolicy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelectionConfirmPolicyTest {

    private final SelectionConfirmPolicy policy = new SelectionConfirmPolicy();

    @Test
    void confirmPolicyTest() {
        var result = policy.from(450);

        assertThat(result.walkingMinutes()).isEqualTo(20);
        assertThat(result.message()).contains("20분");
    }
}
