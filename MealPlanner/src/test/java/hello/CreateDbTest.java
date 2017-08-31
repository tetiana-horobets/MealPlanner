package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CreateDbTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void setupDB() {
        jdbcTemplate.update("DELETE FROM recipes");

        jdbcTemplate.update("INSERT INTO recipes (id, dayOfWeek, content, searchTag) VALUES (?, ?, ?, ?)", 1, "Barszcz Ukrainski", "water + potato + beet", "soup");
        jdbcTemplate.update("INSERT INTO recipes (id, dayOfWeek, content, searchTag) VALUES (?, ?, ?, ?)", 2, "Zurek", "water + potato + sausage", "soup");
    }
}
