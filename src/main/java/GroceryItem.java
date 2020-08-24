import java.time.LocalDate;

public class GroceryItem {

    private String name;
    private Double price;
    private String type;
    private LocalDate expiration;

    public GroceryItem(){
        this("", 0.00, "", null);
    }

    public GroceryItem(String name, Double price, String type, LocalDate expir){
        this.name = name;
        this.price = price;
        this.type = type;
        this.expiration = expir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
