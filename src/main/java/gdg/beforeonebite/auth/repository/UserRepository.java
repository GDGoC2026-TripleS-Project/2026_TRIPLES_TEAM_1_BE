package gdg.beforeonebite.auth.repository;

import gdg.beforeonebite.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
