package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Ingredient {
    private long id;
    private String name;
    private String quantity;
    private String unit;

    public Ingredient() {
    }

    public Ingredient(long id, String name, String quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    Ingredient(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong("id");
        this.name = resultSet.getString("name");
        this.quantity = resultSet.getString("quantity");
        this.unit = resultSet.getString("unit");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

