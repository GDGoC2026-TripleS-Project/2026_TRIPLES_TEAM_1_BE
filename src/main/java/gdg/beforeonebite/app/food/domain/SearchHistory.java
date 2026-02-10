package gdg.beforeonebite.app.food.domain;

import gdg.beforeonebite.app.auth.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "search_history",
        indexes = {
                @Index(name = "idx_search_user", columnList = "user_id"),
                @Index(name = "idx_search_food", columnList = "food_id"),
                @Index(name = "idx_search_searched_at", columnList = "searched_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_search_user_food",
                        columnNames = {"user_id", "food_id"}
                )
        }
)
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Food food;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    public void updateSearchedAt(LocalDateTime time) {
        this.searchedAt = time;
    }
}
