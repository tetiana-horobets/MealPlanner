package hello;

public class Ingredient {
    private long id;
    private String nameIngridient;
    private String quantity;
    private String unit;

    public Ingredient() {
    }

    public Ingredient(long id, String nameIngridient, String quantity, String unit) {
        this.id = id;
        this.nameIngridient = nameIngridient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameIngridient() {
        return nameIngridient;
    }

    public void setNameIngridient(String nameIngridient) {
        this.nameIngridient = nameIngridient;
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

