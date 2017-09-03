package hello;

import java.util.Collection;

interface IngredientRepository {
    Ingredient findById(long id);

    Collection<Ingredient> findAll();

    void delete(long id);

    void add(Ingredient ingredient);

    void edit(long id, Ingredient ingredient);
}
