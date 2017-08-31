package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngredientController {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public IngredientController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
