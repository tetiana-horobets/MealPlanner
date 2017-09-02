package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class IngredientController {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public IngredientController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/ingredient/{id}")
    public Ingredient getIngredient(@PathVariable(value = "id") long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM ingredient WHERE id = ?", (resultSet, rowNum) -> new Ingredient(resultSet), id);
    }

    @GetMapping("/ingredient")
    public Collection<Ingredient> getIngredient() {
        return jdbcTemplate.query("SELECT * FROM ingredient", (resultSet, rowNum) -> new Ingredient(resultSet));
    }

    @DeleteMapping("/ingredient/{id}")
    public void deleteIngredient(@PathVariable(value = "id") long id) {
        jdbcTemplate.update("DELETE FROM ingredient WHERE id = ?", id);
    }

    @PostMapping("/ingredient")
    public void addIngredient(@RequestBody Ingredient ingredient) {
        jdbcTemplate.update("INSERT INTO ingredient (name, quantity, unit) VALUES (?, ?, ?)", ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
    }

    @PutMapping("/ingredient/{id}")
    public void editIngredient(@PathVariable(value = "id") long id, @RequestBody Ingredient ingredient) {
        jdbcTemplate.update("UPDATE ingredient set name = ?, quantity = ?, unit = ? WHERE id = ?", ingredient.getName(), ingredient.getQuantity(),  ingredient.getUnit(), id);
    }

}
