package game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final int id;
    private String name;
    private String publisher;
    private String description;
    private List<String> platform;
    private List<Category> category;
    private double price;
    private boolean status;
    private Rating rating;
    private String imageName;

    public Game(int id, String name, String publisher, String description, List<String> platform, List<String> category, double price, boolean status, double stars, int numberOfRatings, String imageName) {
        if (id < 0)
            throw new IllegalArgumentException("Id can't be a negative number!");
        this.id = id;

        if (name.length() == 0)
            throw new IllegalArgumentException("The name can't be empty!");
        this.name = name;

        if (publisher.length() == 0)
            throw new IllegalArgumentException("The publisher can't be empty!");
        this.publisher = publisher;

        this.description = description;

        for(String p: platform){
            if(p.length() == 0)
                throw new IllegalArgumentException("The platform can't be empty!");
        }
        this.platform = platform;

        this.category = new ArrayList<>();
        for(String cat: category) {
            Category c = Category.getCategory(cat);

            if(c!=null)
                this.category.add(c);
            else
                throw new IllegalArgumentException("The category is incorrect");
        }

        if (price < 0)
            throw new IllegalArgumentException("The price can't be negative!");
        this.price = price;

        this.status = status;

        this.rating = new Rating(stars, numberOfRatings);

        if (imageName.length() == 0)
            throw new IllegalArgumentException("The image name can't be empty!");
        this.imageName = imageName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Game that = (Game) obj;
        return id == that.id && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (id != 0) {
            result = 31 * result + id;
        }
        if (name != null) {
            result = 31 * result + name.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "Game{" + "id=" + id + ", name='" + name + '\'' +
                ", publisher='" + publisher + '\'' + ", description='" + description + '\'' +
                ", platform=" + platform + ", category=" + category + ", price=" + price +
                ", status=" + status + ", rating=" + rating + ", image='" + imageName + "'}";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPlatform() {
        return platform;
    }

    public List<Category> getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public boolean getStatus() {
        return status;
    }

    public Rating getRating() {
        return rating;
    }

    public String getImageName() {
        return imageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlatform(List<String> platform) {
        this.platform = platform;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
