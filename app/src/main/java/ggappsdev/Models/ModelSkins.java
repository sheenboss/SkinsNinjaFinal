package ggappsdev.Models;

public class ModelSkins {


    private String name;
    private String image;
    private int price;
    private int quantity;
    private String type;
    private String quality;

    public ModelSkins() {
    }

    public ModelSkins(String name, String image, int price, int quantity, String type, String quality) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
