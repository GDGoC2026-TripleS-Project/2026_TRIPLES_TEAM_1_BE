package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
