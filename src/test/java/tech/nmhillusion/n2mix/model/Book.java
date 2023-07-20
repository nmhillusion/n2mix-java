package tech.nmhillusion.n2mix.model;

/**
 * date: 2023-07-20
 * <p>
 * created-by: nmhillusion
 */

public class Book {
    private String title;
    private String author;
    private int price;
    
    public String getTitle() {
        return title;
    }
    
    public Book setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public Book setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public int getPrice() {
        return price;
    }
    
    public Book setPrice(int price) {
        this.price = price;
        return this;
    }
}
