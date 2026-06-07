package model;

public abstract class AbstractItem {
    protected String id;
    protected String name;
    protected double price;

    public AbstractItem(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public abstract void displayDetails(int rowNum);
}