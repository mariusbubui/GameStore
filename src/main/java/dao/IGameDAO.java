package dao;

import game.Game;

import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public interface IGameDAO {
     /**
      *
      */
     List<Game> getAllGames() throws SQLException;

     /**
      *
      */
     List<Game> getGamesName(String name) throws SQLException;

     /**
      *
      */
     Game getGame(int id) throws SQLException;

     /**
      *
      */
     Game insertGame(Game game) throws SQLException;

     /**
      *
      */
     Game updateGame(Game game) throws SQLException;

     /**
      *
      */
     List<String> getPlatforms() throws SQLException;

     /**
      *
      */
     List<String> getPublishers() throws SQLException;
}