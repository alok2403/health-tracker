package health_tracker.demo.service;

import health_tracker.demo.dto.AdminUserHealthDto;
import health_tracker.demo.model.HealthRecord;
import health_tracker.demo.model.User;
import health_tracker.demo.repository.HealthRepository;
import health_tracker.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepo;
    private final HealthRepository healthRepo;

    public AdminService(UserRepository userRepo, HealthRepository healthRepo) {
        this.userRepo = userRepo;
        this.healthRepo = healthRepo;
    }

    public List<AdminUserHealthDto> getUsersWithHealthData() {

        return userRepo.findAll().stream().map(user -> {

            HealthRecord latest = healthRepo.findLatestByUser(user.getId());

            return new AdminUserHealthDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.isEnabled(),
                    latest != null ? latest.getSteps() : 0,
                    latest != null ? latest.getCalories() : 0,
                    latest != null ? latest.getWaterIntake() : 0,
                    latest != null ? latest.getWeight() : 0.0
            );
        }).toList();
    }
}
