module GameStore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires java.sql;
    requires bcrypt;
    requires activation;
    requires java.mail;
    requires org.controlsfx.controls;
    requires Spire.Doc;

    opens gui;
    opens dao;
    opens service;
    opens game;
    opens user;
    opens order;
}