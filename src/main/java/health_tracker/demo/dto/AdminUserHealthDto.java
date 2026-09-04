package health_tracker.demo.dto;

public class AdminUserHealthDto {

    private Long id;
    private String name;
    private String email;
    private boolean enabled;

    private int steps;
    private int calories;
    private int water;
    private double weight;

    public AdminUserHealthDto(Long id, String name, String email, boolean enabled,
                              int steps, int calories, int water, double weight) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enabled = enabled;
        this.steps = steps;
        this.calories = calories;
        this.water = water;
        this.weight = weight;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isEnabled() { return enabled; }

    public int getSteps() { return steps; }
    public int getCalories() { return calories; }
    public int getWater() { return water; }
    public double getWeight() { return weight; }
}
