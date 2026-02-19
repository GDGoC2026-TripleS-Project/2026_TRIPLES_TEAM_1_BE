package gdg.beforeonebite.app.food.scheduler;

import gdg.beforeonebite.app.food.repository.FoodSelectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FoodSelectionScheduler {

    private final FoodSelectionRepository foodSelectionRepository;
    private final Clock clock;

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    @Transactional
    public void cleanOldSelections() {
        LocalDate cutoffDate = LocalDate.now(clock).minusDays(60);
        foodSelectionRepository.deleteBySelectedDateBefore(cutoffDate);
    }
}
