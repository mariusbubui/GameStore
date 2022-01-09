package game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new Rating(5.7, 4));
        assertThrows(IllegalArgumentException.class, () -> new Rating(0, -5));
    }

    @Test
    void getNumberOfRatings() {
        Rating rating = new Rating(3.8, 10);
        assertEquals(10, rating.getNumberOfRatings());
    }

    @Test
    void getStars() {
        Rating rating = new Rating(3.8, 10);
        assertEquals(3.8, rating.getStars());
    }

    @Test
    void setStars() {
        Rating rating = new Rating(3.8, 10);
        rating.setStars(5);
        assertEquals(5, rating.getStars());
    }

    @Test
    void setNumberOfRatings() {
        Rating rating = new Rating(3.8, 10);
        rating.setNumberOfRatings(1);
        assertEquals(1, rating.getNumberOfRatings());
    }

    @Test
    void testToString() {
        Rating rating = new Rating(3.8, 10);
        assertEquals("Rating{stars=3.8, numberOfRatings=10}", rating.toString());
    }
}