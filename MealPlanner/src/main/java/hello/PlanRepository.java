package hello;

import java.util.Collection;

public interface PlanRepository {
    Plan findById(long id);

    Collection<Plan> findAll();

}
