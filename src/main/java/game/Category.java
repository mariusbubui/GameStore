package game;

public enum Category {
    ACTION("Action"),
    STRATEGY("Strategy"),
    ADVENTURE("Adventure"),
    SIMULATION("Simulation"),
    ROLE_PLAYING("Role-Playing"),
    SPORT_RACING("Sport-Racing");

    private final String text;

    Category(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

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