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

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM plan WHERE id = ?", id);
    }

    @Override
    public void add(Plan plan) {
        jdbcTemplate.update("INSERT INTO plan (name, startDate) VALUES (?, ?)", plan.getName(), plan.getStartDate());
    }

    @Override
    public void edit(long id, Plan plan) {
        jdbcTemplate.update("UPDATE plan set name = ?, startDate = ? WHERE id = ?", plan.getName(), plan.getStartDate(), id);
    }
}
