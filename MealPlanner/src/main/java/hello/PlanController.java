package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class PlanController {
    private PlanRepository repository;

    @Autowired
    public PlanController(PlanRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/plan/{id}")
    public Plan getPlan(@PathVariable(value = "id") long id) {
        return repository.findById(id);
    }

    @GetMapping("/api/plan")
    public Collection<Plan> getPlans() {
        return repository.findAll();
    }

    @DeleteMapping("/api/plan/{id}")
    public void deletePlan(@PathVariable(value = "id") long id) {
        repository.delete(id);
    }

    @PostMapping("/api/plan")
    public void addPlan(@RequestBody Plan plan) {
        repository.add(plan);
    }

    @PutMapping("/api/plan/{id}")
    public void editPlan(@PathVariable(value = "id") long id, @RequestBody Plan plan) {
        repository.edit(id, plan);
    }
}
