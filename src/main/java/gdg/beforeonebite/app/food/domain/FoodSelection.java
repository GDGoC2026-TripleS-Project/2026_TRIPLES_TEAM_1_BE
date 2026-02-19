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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "food_selection",
        indexes = {
                @Index(name = "idx_selection_user_date", columnList = "user_id, selected_date"),
                @Index(name = "idx_selection_selected_at", columnList = "selected_at")
        }
)
public class FoodSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private FoodBrand foodBrand;

    @Column(nullable = false)
    private LocalDate selectedDate;

    @Column(nullable = false)
    private LocalDateTime selectedAt;
}
