package gdg.beforeonebite.app.food.scheduler;

import gdg.beforeonebite.app.food.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchHistoryScheduler {

    private final SearchHistoryService searchHistoryService;

    // 매일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanOldSearchHistory() {

        int deletedCount = searchHistoryService.deleteOldSearchHistory();

        log.info("5일 지난 검색 기록 삭제 완료 - 삭제된 개수: {}", deletedCount);
    }
}
