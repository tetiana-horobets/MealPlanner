package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class IngredientController {

    private IngredientRepository repository;

    @Autowired
    public IngredientController(IngredientRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/ingredient/{id}")
    public Ingredient getIngredient(@PathVariable(value = "id") long id) {
        return repository.findById(id);
    }

    @GetMapping("/api/ingredient")
    public Collection<Ingredient> getIngredient() {
        return repository.findAll();
    }

    @DeleteMapping("/api/ingredient/{id}")
    public void deleteIngredient(@PathVariable(value = "id") long id) {
        repository.delete(id);
    }

    @PostMapping("/api/ingredient")
    public void addIngredient(@RequestBody Ingredient ingredient) {
        repository.add(ingredient);
    }

    @PutMapping("/api/ingredient/{id}")
    public void editIngredient(@PathVariable(value = "id") long id, @RequestBody Ingredient ingredient) {
        repository.edit(id, ingredient);
    }
}
