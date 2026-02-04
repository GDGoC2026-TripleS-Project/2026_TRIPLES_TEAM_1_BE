package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByBrandNameIn(Set<String> brandNames);
}
