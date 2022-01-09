package game;

public class Rating {
    private double stars;
    private int numberOfRatings;

    public Rating(double stars, int numberOfRatings) {
        if(stars < 0 || stars > 5)
            throw new IllegalArgumentException("Rating stars can get a value only between 0 and 5!");
        this.stars = stars;

        if(numberOfRatings < 0)
            throw new IllegalArgumentException("There can't be a negative amount of ratings!");
        this.numberOfRatings = numberOfRatings;
    }

    @Override
    public String toString() {
        return "Rating{" + "stars=" + stars + ", numberOfRatings=" + numberOfRatings + '}';
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }
}
