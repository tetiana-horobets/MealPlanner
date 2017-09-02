package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
class IngredientRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public IngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Ingredient findById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM ingredient WHERE id = ?", (resultSet, rowNum) -> new Ingredient(resultSet), id);
    }

    Collection<Ingredient> findAll() {
        return jdbcTemplate.query("SELECT * FROM ingredient", (resultSet, rowNum) -> new Ingredient(resultSet));
    }

    void delete(long id) {
        jdbcTemplate.update("DELETE FROM ingredient WHERE id = ?", id);
    }

    void add(Ingredient ingredient) {
        jdbcTemplate.update("INSERT INTO ingredient (name, quantity, unit) VALUES (?, ?, ?)", ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
    }

    void edit(long id, Ingredient ingredient) {
        jdbcTemplate.update("UPDATE ingredient set name = ?, quantity = ?, unit = ? WHERE id = ?", ingredient.getName(), ingredient.getQuantity(),  ingredient.getUnit(), id);
    }
}
