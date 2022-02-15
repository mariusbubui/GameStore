package service;

import game.Category;
import game.Game;
import dao.GameDAO;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * ServiceGame is a utility class that contains the
 * possible actions performed on a set of games, or it's attributes.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class ServiceGame {
    /**
     * Methode that allows sorting on a list of games
     * given a comparator.
     *
     * @param comparator the comparator that is used
     * @return a new list of games retrieved from the database or null
     */
    private static List<Game> sortGames(Comparator<Game> comparator) {
        try {
            List<Game> games = new GameDAO().getAllGames();
            games.sort(comparator);
            return games;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Methode that allows sorting on a list
     * of games by their name.
     *
     * @param ascending indicated whether the sorting is ascending or not
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> sortGamesName(boolean ascending) {
        return ascending ? sortGames(Comparator.comparing(Game::getName)) : sortGames(Comparator.comparing(Game::getName).reversed());
    }

    /**
     * Methode that allows sorting on a list
     * of games by their price.
     *
     * @param ascending indicated whether the sorting is ascending or not
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> sortGamesPrice(boolean ascending) {
        return ascending ? sortGames(Comparator.comparing(Game::getPrice)) : sortGames(Comparator.comparing(Game::getPrice).reversed());
    }

    /**
     * Methode that allows sorting on a list
     * of games by their rating.
     *
     * @param ascending indicated whether the sorting is ascending or not
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> sortGamesRating(boolean ascending) {
        return ascending ? sortGames(Comparator.comparingDouble(g -> g.getRating().getStars()))
                : sortGames((g1, g2) -> Double.compare(g2.getRating().getStars(), g1.getRating().getStars()));
    }

    /**
     * Methode that allows filtering on a list of games
     * given a predicate.
     *
     * @param filter the predicate that is used from the filtration
     * @return a new list of games retrieved from the database or null
     */
    private static List<Game> filterGames(Predicate<? super Game> filter) {
        try {
            List<Game> games = new GameDAO().getAllGames();
            return games.stream().filter(filter).collect(Collectors.toList());
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Methode that allows filtering on a
     * list of games given a platform.
     *
     * @param platform the string that is used from the filtration
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> filterGamesPlatform(String platform) {
        return filterGames(game -> {
            for (String p : game.getPlatform())
                if (p.equalsIgnoreCase(platform))
                    return true;
            return false;
        });
    }

    /**
     * Methode that allows filtering on a
     * list of games given a category.
     *
     * @param category the string that is used from the filtration
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> filterGamesCategory(String category) {
        return filterGames(game -> {
            for (Category c : game.getCategory())
                if (c.toString().equalsIgnoreCase(category))
                    return true;
            return false;
        });
    }

    /**
     * Methode that allows filtering on a
     * list of games given a publisher.
     *
     * @param publisher the string that is used from the filtration
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> filterGamesPublisher(String publisher) {
        return filterGames(game -> game.getPublisher().equalsIgnoreCase(publisher));
    }

    /**
     * Methode that allows filtering on a
     * list of games given a rating.
     *
     * @param rating the integer that is used from the filtration
     * @return a new list of games retrieved from the database or null
     */
    public static List<Game> filterGamesRating(int rating) {
        return filterGames(game -> Math.ceil(game.getRating().getStars()) == rating);
    }

    /**
     * Methode that return a list of all the
     * platforms in the database.
     *
     * @return a new list of platform or null
     */
    public static List<String> getPlatforms() {
        try {
            return new GameDAO().getPlatforms();
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Methode that return a list of all the
     * publishers in the database.
     *
     * @return a new list of publishers or null
     */
    public static List<String> getPublishers() {
        try {
            return new GameDAO().getPublishers();
        } catch (SQLException e) {
            return null;
        }
    }
}
