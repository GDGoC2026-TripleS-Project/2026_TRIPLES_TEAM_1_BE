package gdg.beforeonebite.app.food.service;

import org.springframework.stereotype.Component;

@Component
public class SelectionConfirmPolicy {

    public Result from(double calories) {
        if (calories <= 150) {
            return new Result("10분만 걸으면 충분해요", 10);
        }
        if (calories <= 300) {
            return new Result("오늘은 이정도로 마무리해도 괜찮아요", 15);
        }
        if (calories <= 500) {
            return new Result("20분 정도면 충분해요", 20);
        }
        if (calories <= 700) {
            return new Result("이 정도만 해도 오늘은 잘 넘겼어요", 25);
        }

        return new Result("30분만 걸어도 오늘은 괜찮아요", 30); // 700 이상
    }

    public record Result(String message, int walkingMinutes) {
    }
}
