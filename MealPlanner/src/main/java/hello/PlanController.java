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

    @GetMapping("/plan/{id}")
    public Plan getPlan(@PathVariable(value = "id") long id) {
        return repository.findById(id);
    }

    @GetMapping("/plan")
    public Collection<Plan> getPlans() {
        return repository.findAll();
    }
}
