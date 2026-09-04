package health_tracker.demo.repository;




import health_tracker.demo.model.HealthRecord;
import health_tracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HealthRepository extends JpaRepository<HealthRecord, Long> {

    Optional<HealthRecord> findFirstByUserAndDateOrderByIdDesc(User user, LocalDate date);

    List<HealthRecord> findByUserAndDateBetween(
            User user, LocalDate start, LocalDate end
    );

    List<HealthRecord> findByUserAndDateBetweenOrderByDateAsc(
            User user,
            LocalDate start,
            LocalDate end
    );

    HealthRecord findTopByUserOrderByDateDesc(User user);

    @Query("""
    SELECT h FROM HealthRecord h
    WHERE h.user.id = :userId
    ORDER BY h.date DESC
    LIMIT 1
""")
    HealthRecord findLatestByUser(@Param("userId") Long userId);


}
