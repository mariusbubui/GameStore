package game;

/**
 * Enumeration that specifies the possible
 * categories of a game.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public enum Category {
    ACTION("Action"),
    STRATEGY("Strategy"),
    ADVENTURE("Adventure"),
    SIMULATION("Simulation"),
    ROLE_PLAYING("Role-Playing"),
    SPORT_RACING("Sport-Racing");

    /**
     * The string value of the object.
     */
    private final String text;

    /**
     * Constructor specifying the string value.
     * @param text the value
     */
    Category(String text) {
        this.text = text;
    }

    /**
     * Transforms the object into a string.
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Getter for the category
     * @param c the string name
     * @return the category object
     */
    public static Category getCategory(String c){
        switch (c.toUpperCase()) {
            case "ACTION" -> {
                return Category.ACTION;
            }
            case "STRATEGY" -> {
                return Category.STRATEGY;
            }
            case "ADVENTURE" -> {
                return Category.ADVENTURE;
            }
            case "SIMULATION" -> {
                return Category.SIMULATION;
            }
            case "ROLE-PLAYING" -> {
                return Category.ROLE_PLAYING;
            }
            case "SPORT-RACING" -> {
                return Category.SPORT_RACING;
            }
            default -> {
                return null;
            }
        }
    }
}