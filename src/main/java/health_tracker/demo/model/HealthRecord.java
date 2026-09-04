package health_tracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "health_record",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private int steps;
    private int calories;
    private double weight;
    private int waterIntake;

    // 🎯 DAILY GOALS
    private int stepGoal;
    private int calorieGoal;
    private int waterGoal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public int getWaterIntake() { return waterIntake; }
    public void setWaterIntake(int waterIntake) { this.waterIntake = waterIntake; }

    public int getStepGoal() { return stepGoal; }
    public void setStepGoal(int stepGoal) { this.stepGoal = stepGoal; }

    public int getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(int calorieGoal) { this.calorieGoal = calorieGoal; }

    public int getWaterGoal() { return waterGoal; }
    public void setWaterGoal(int waterGoal) { this.waterGoal = waterGoal; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
