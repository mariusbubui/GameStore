package dao;

import game.Game;
import game.Rating;

import java.sql.SQLException;
import java.util.List;

public interface IGameDAO {
     List<Game> getAllGames() throws SQLException;
     List<Game> getGamesName(String name) throws SQLException;
     Game getGame(int id) throws SQLException;
     boolean insertGame(String name, String publisher, String description, List<String> platform, List<String> category, double price, boolean status, Rating rating, String image) throws SQLException;
     boolean updateGame(Game game) throws SQLException;

}