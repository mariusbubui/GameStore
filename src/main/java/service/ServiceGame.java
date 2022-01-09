package service;

import game.Category;
import game.Game;
import dao.GameDAO;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServiceGame {
    private static List<Game> games;

    static {
        try {
            games = new GameDAO().getAllGames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Game> sortGames(Comparator<Game> comparator) {
        try {
            List<Game> games = new GameDAO().getAllGames();
            games.sort(comparator);
            return games;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Game> sortGamesName(boolean ascending) {
        return ascending ? sortGames(Comparator.comparing(Game::getName)) : sortGames(Comparator.comparing(Game::getName).reversed());
    }

    public static List<Game> sortGamesPrice(boolean ascending) {
        return ascending ? sortGames(Comparator.comparing(Game::getPrice)) : sortGames(Comparator.comparing(Game::getPrice).reversed());
    }

    public static List<Game> sortGamesRating(boolean ascending) {
        return ascending ? sortGames(Comparator.comparingDouble(g -> g.getRating().getStars()))
                : sortGames((g1, g2) -> Double.compare(g2.getRating().getStars(), g1.getRating().getStars()));
    }

    private static List<Game> filterGames(Predicate<? super Game> filter){
        return games.stream().filter(filter).collect(Collectors.toList());
    }

    public static List<Game> filterGamesPlatform(String platform) {
        return filterGames(game -> {
            for(String p: game.getPlatform())
                if(p.equalsIgnoreCase(platform))
                    return true;
            return false;
        });
    }

    public static List<Game> filterGamesCategory(String category) {
        return filterGames(game -> {
            for(Category c: game.getCategory())
                if(c.toString().equalsIgnoreCase(category))
                    return true;
            return false;
        });
    }

    public static List<Game> filterGamesPublisher(String publisher) {
        return filterGames(game -> game.getPublisher().equalsIgnoreCase(publisher));
    }

    public static List<Game> filterGamesPrice(double p1, double p2) {
        return filterGames(game -> game.getPrice() >= p1 && game.getPrice() <= p2);
    }

    public static List<Game> filterGamesRating(int rating) {
        return filterGames(game -> Math.ceil(game.getRating().getStars()) == rating);
    }

    public static void main(String[] args) {
        /*User u = ServiceUser.login();
        System.out.println(u);
        if (u != null) {
            ServiceUser.logout(u);
        }
        System.out.println(u);*/
        //System.out.println(service.ServiceUser.register());

        for (Game g : Objects.requireNonNull(ServiceGame.sortGamesName(false))) {
            System.out.println(g);
        }
        System.out.println();

        for (Game g : Objects.requireNonNull(ServiceGame.filterGamesPlatform("PS5"))) {
            System.out.println(g);
        }
        System.out.println();

        for (Game g : Objects.requireNonNull(ServiceGame.sortGamesName(true))) {
            System.out.println(g);
        }
    }
}
