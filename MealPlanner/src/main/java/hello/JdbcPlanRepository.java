package hello;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
class JdbcPlanRepository implements PlanRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcPlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Plan findById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM plan WHERE id = ?", (resultSet, rowNum) -> new Plan(resultSet), id);
    }

    @Override
    public Collection<Plan> findAll() {
        return jdbcTemplate.query("SELECT * FROM plan", (resultSet, rowNum) -> new Plan(resultSet));
    }
}