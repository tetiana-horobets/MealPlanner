package hello;

import java.util.Collection;

interface PlanRepository {
    Plan findById(long id);

    Collection<Plan> findAll();

    void delete(long id);

    void add(Plan plan);

    void edit(long id, Plan plan);

}
