package health_tracker.demo.dto;

import java.util.List;

public class ChartDataDto {

    private List<String> dates;
    private List<Integer> steps;
    private List<Integer> calories;
    private List<Integer> water;

    public ChartDataDto(List<String> dates,
                        List<Integer> steps,
                        List<Integer> calories,
                        List<Integer> water) {
        this.dates = dates;
        this.steps = steps;
        this.calories = calories;
        this.water = water;
    }

    public List<String> getDates() { return dates; }
    public List<Integer> getSteps() { return steps; }
    public List<Integer> getCalories() { return calories; }
    public List<Integer> getWater() { return water; }
}
