package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
class JdbcIngredientRepository implements IngredientRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Ingredient findById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM ingredient WHERE id = ?", (resultSet, rowNum) -> new Ingredient(resultSet), id);
    }

    @Override
    public Collection<Ingredient> findAll() {
        return jdbcTemplate.query("SELECT * FROM ingredient", (resultSet, rowNum) -> new Ingredient(resultSet));
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM ingredient WHERE id = ?", id);
    }

    @Override
    public void add(Ingredient ingredient) {
        jdbcTemplate.update("INSERT INTO ingredient (name, quantity, unit) VALUES (?, ?, ?)", ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
    }

    @Override
    public void edit(long id, Ingredient ingredient) {
        jdbcTemplate.update("UPDATE ingredient set name = ?, quantity = ?, unit = ? WHERE id = ?", ingredient.getName(), ingredient.getQuantity(),  ingredient.getUnit(), id);
    }
}
