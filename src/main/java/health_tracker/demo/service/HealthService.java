package health_tracker.demo.service;




import health_tracker.demo.dto.ChartDataDto;
import health_tracker.demo.model.HealthRecord;
import health_tracker.demo.model.User;
import health_tracker.demo.repository.HealthRepository;
import org.springframework.stereotype.Service;
import health_tracker.demo.dto.WeeklySummary;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthService {

    private final HealthRepository repo;

    public HealthService(HealthRepository repo) {
        this.repo = repo;
    }

    // ✅ SAFE CUMULATIVE SAVE
    public void saveOrUpdate(HealthRecord incoming, User user) {

        LocalDate today = LocalDate.now();

        HealthRecord record = repo
                .findFirstByUserAndDateOrderByIdDesc(user, today)
                .orElseGet(() -> {
                    HealthRecord r = new HealthRecord();
                    r.setUser(user);
                    r.setDate(today);
                    r.setStepGoal(10000);
                    r.setCalorieGoal(500);
                    r.setWaterGoal(3);
                    return r;
                });

        // cumulative
        record.setSteps(record.getSteps() + incoming.getSteps());
        record.setCalories(record.getCalories() + incoming.getCalories());
        record.setWaterIntake(record.getWaterIntake() + incoming.getWaterIntake());

        // latest weight
        if (incoming.getWeight() > 0) {
            record.setWeight(incoming.getWeight());
        }

        // update goals only if provided
        if (incoming.getStepGoal() > 0) {
            record.setStepGoal(incoming.getStepGoal());
        }
        if (incoming.getCalorieGoal() > 0) {
            record.setCalorieGoal(incoming.getCalorieGoal());
        }
        if (incoming.getWaterGoal() > 0) {
            record.setWaterGoal(incoming.getWaterGoal());
        }

        repo.save(record);
    }


    // ✅ SAFE FETCH FOR DASHBOARD
    public HealthRecord getTodayRecord(User user) {
        return repo.findFirstByUserAndDateOrderByIdDesc(user, LocalDate.now())
                .orElseGet(() -> {
                    HealthRecord r = new HealthRecord();
                    r.setUser(user);
                    r.setDate(LocalDate.now());
                    r.setSteps(0);
                    r.setCalories(0);
                    r.setWaterIntake(0);
                    r.setWeight(0);
                    return r;
                });
    }

    public WeeklySummary getWeeklySummary(User user) {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6); // last 7 days

        List<HealthRecord> records =
                repo.findByUserAndDateBetween(user, start, end);

        WeeklySummary summary = new WeeklySummary();

        int totalSteps = 0;
        int totalCalories = 0;
        int totalWater = 0;
        double totalWeight = 0;
        int weightCount = 0;

        for (HealthRecord r : records) {
            totalSteps += r.getSteps();
            totalCalories += r.getCalories();
            totalWater += r.getWaterIntake();

            if (r.getWeight() > 0) {
                totalWeight += r.getWeight();
                weightCount++;
            }
        }

        summary.setTotalSteps(totalSteps);
        summary.setTotalCalories(totalCalories);
        summary.setTotalWater(totalWater);
        summary.setAverageWeight(
                weightCount > 0 ? totalWeight / weightCount : 0
        );

        return summary;
    }

    public void updateTargets(User user, int stepGoal, int calorieGoal, int waterGoal) {

        LocalDate today = LocalDate.now();

        HealthRecord record = repo
                .findFirstByUserAndDateOrderByIdDesc(user, today)
                .orElseGet(() -> {
                    HealthRecord r = new HealthRecord();
                    r.setUser(user);
                    r.setDate(today);
                    return r;
                });

        record.setStepGoal(stepGoal);
        record.setCalorieGoal(calorieGoal);
        record.setWaterGoal(waterGoal);

        repo.save(record);
    }

    public ChartDataDto getLast7DaysChart(User user) {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);

        List<HealthRecord> records =
                repo.findByUserAndDateBetweenOrderByDateAsc(user, start, end);

        List<String> dates = new ArrayList<>();
        List<Integer> steps = new ArrayList<>();
        List<Integer> calories = new ArrayList<>();
        List<Integer> water = new ArrayList<>();

        for (HealthRecord r : records) {
            dates.add(r.getDate().toString());
            steps.add(r.getSteps());
            calories.add(r.getCalories());
            water.add(r.getWaterIntake());
        }

        return new ChartDataDto(dates, steps, calories, water);
    }

    public ChartDataDto getChartData(User user) {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);

        List<HealthRecord> records =
                repo.findByUserAndDateBetweenOrderByDateAsc(user, start, end);

        List<String> dates = records.stream()
                .map(r -> r.getDate().toString())
                .collect(Collectors.toList());

        List<Integer> steps = records.stream()
                .map(HealthRecord::getSteps)
                .collect(Collectors.toList());

        List<Integer> calories = records.stream()
                .map(HealthRecord::getCalories)
                .collect(Collectors.toList());

        List<Integer> water = records.stream()
                .map(HealthRecord::getWaterIntake)
                .collect(Collectors.toList());

        return new ChartDataDto(dates, steps, calories, water);
    }

    public List<HealthRecord> getLast7Days(User user) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        return repo.findByUserAndDateBetweenOrderByDateAsc(user, start, end);
    }



}
