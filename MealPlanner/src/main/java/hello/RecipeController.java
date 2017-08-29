package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
public class RecipeController {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RecipeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping(value = "/recipe", method = RequestMethod.GET)
    public Collection<Recipe> getRecipe() {
        return jdbcTemplate.query("SELECT id, dayOfWeek, content, searchTag  FROM recipes", (rs, rowNum) -> new Recipe(rs.getLong("id"), rs.getString("dayOfWeek"), rs.getString("content"), rs.getString("searchTag")));
    }

    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.GET)
    public Recipe getRecipe(@PathVariable(value = "id") long id) {
        return jdbcTemplate.queryForObject("SELECT id, dayOfWeek, content, searchTag FROM recipes WHERE id = ?", new Object[]{id}, (rs, rowNum) -> new Recipe(rs.getLong("id"), rs.getString("dayOfWeek"), rs.getString("content"), rs.getString("searchTag")));
    }
    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.DELETE)
    public void deleteRecipe(@PathVariable(value = "id") long id) {
        jdbcTemplate.update("DELETE FROM recipes WHERE id = ?", id);
    }

    @RequestMapping(value = "/recipe", method = RequestMethod.POST)
    public void addRecipe(@RequestBody Recipe recipe) {
        jdbcTemplate.update("INSERT INTO recipes (dayOfWeek, content, searchTag) VALUES (?, ?, ?)", recipe.getDayOfWeek(), recipe.getContent(), recipe.getSearchTag());
    }

    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.PUT)
    public void editRecipe(@PathVariable(value = "id") long id, @RequestBody Recipe recipe) {
        jdbcTemplate.update("UPDATE recipes set dayOfWeek = ?, content = ?, searchTag = ? WHERE id = ?", recipe.getDayOfWeek(), recipe.getContent(),  recipe.getSearchTag(), id);
    }


}