package sample.controllers;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class SettingsController implements Initializable {

    @FXML private JFXToggleButton toggleBackup;

    @FXML private JFXTextField textfieldServer, textfieldNamaSql;

    @FXML private JFXButton btnConnectServer, btnPath, btnPathBackup, backupPath;

    @FXML private JFXTimePicker jadwalBackup, jadwalMigrasi;

    @FXML private Label foxproPath;

    @FXML private AnchorPane anchorid;

    @FXML private JFXListView listOfJadwal, listOfMigrasi;

    @FXML private JFXComboBox bookMarksCb;

    @FXML private HBox hbox;

    static JFXButton buttonOk = new JFXButton();
    static JFXButton buttonCancel = new JFXButton();

    Connection con = DatabaseConnection.getSQLConnection();
    String autoBackupValue = "";
    PreparedStatement statement;

    String location, backup, choosedbookmarks;
    File folder;
    LocalTime schedule;

    JFXPopup popup;
    JFXPopup popupMigrasi;
    ArrayList<String> arrayJadwal = new ArrayList<>();
    ArrayList<String> arrayMigrasi = new ArrayList<>();
    ObservableList<String> listBookmarks = FXCollections.observableArrayList();
    static final Logger logger = Logger.getLogger(SettingsController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PropertyConfigurator.configure(getClass().getResource("log4j.properties"));
        logger.debug("Settings openned");
        readSettings();
        bookMarksCb.hide(); //before you set new visibleRowCount value

        buttonOk.setText("Ok");
        buttonOk.setPrefHeight(38);
        buttonOk.setPrefWidth(88);
        buttonOk.setOnMouseClicked(actionEvent -> {
            if(toggleBackup.isSelected()) {
                autoBackupValue = "True";
            } else {
                autoBackupValue = "False";
            }
            saveSettings(textfieldServer.getText(), textfieldNamaSql.getText(), foxproPath.getText(), autoBackupValue,
                    jadwalBackup.getValue(), backupPath.getText());
        });

        buttonCancel.setText("Cancel");
        buttonCancel.setPrefHeight(38);
        buttonCancel.setPrefWidth(88);
        buttonCancel.setOnMouseClicked(actionEvent -> {
            readSettings();
            Stage settingsstage = (Stage) buttonCancel.getScene().getWindow();
            settingsstage.hide();
        });

        HBox.setMargin(buttonCancel, new Insets(0, 30, 0, 0));
        hbox.getChildren().add(buttonOk);
        hbox.getChildren().add(buttonCancel);
    }

    public void handleClicks(javafx.scene.input.MouseEvent event) {
        if (textfieldServer.equals(event.getSource())) {
            System.out.println("Hello");
        }else if (event.getSource() == btnConnectServer) {
            updateSqlServerSettings();
        } else if (event.getSource() == toggleBackup) {
            setToggleBackup();
        } else if (event.getSource() == btnPath) {
            settingPath();
        } else if (event.getSource() == btnPathBackup) {
            backupPath();
        } else if (event.getSource() == bookMarksCb) {
            loadComboboxBookmarks();
        } else if (event.getSource() == backupPath) {
            try {
                Desktop.getDesktop().open(new File(backupPath.getText()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateSqlServerSettings() {
        try {
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://"+textfieldServer.getText()+";databaseName="+textfieldNamaSql.getText()+";integratedSecurity=true");
//            DatabaseConnection.serverName = textfieldServer.getText();
//            DatabaseConnection.databaseName = textfieldNamaSql.getText();
            btnConnectServer.setText("Connected");
            btnConnectServer.setStyle("-fx-background-color: GREEN; -fx-background-radius: 1em");
        } catch (Exception e) {
            btnConnectServer.setText("Unknown Connection");
            btnConnectServer.setStyle("-fx-background-color: RED; -fx-background-radius: 1em");
        }
    }

    private void loadComboboxBookmarks() {
        //load combobox bookmarks
        bookMarksCb.hide(); //before you set new visibleRowCount value
        bookMarksCb.getItems().clear();
        bookMarksCb.setValue(null);

        String querry = "SELECT * FROM bookmarksdb";

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(querry);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                listBookmarks.add(rs.getString(2));
            }

            bookMarksCb.setItems(listBookmarks);

            sqliteConnection.close();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        bookMarksCb.hide(); //before you set new visibleRowCount value
        bookMarksCb.setValue(choosedbookmarks);
        bookMarksCb.setVisibleRowCount(10); // set new visibleRowCount value
        bookMarksCb.show(); //after you set new visibleRowCount value
    }

    public void readSettings() {

        listOfJadwal.getItems().clear();
        listOfMigrasi.getItems().clear();
        textfieldServer.setText(DatabaseConnection.serverName);
        textfieldNamaSql.setText(DatabaseConnection.databaseName);

        String sqlite = "SELECT sqlserver, sqldb, foxprodb, autobackup, jadwalbackup, pathbackup, choosedbookmarks FROM settings";

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            statement = sqliteConnection.prepareStatement(sqlite);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                location = rs.getString(3);
                autoBackupValue = rs.getString(4);
                String scheduled = rs.getString(5);
                backup = rs.getString(6);
                choosedbookmarks = rs .getString(7);
                bookMarksCb.setValue(rs.getString(7));

                folder = new File(location);
                schedule = LocalTime.parse(scheduled);
                jadwalBackup.setValue(LocalTime.parse(scheduled));
                backupPath.setText(backup);
                foxproPath.setText(location);
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }

        loadComboboxBookmarks();

        //load backup schedule to listview
        String jadwalnyaBackup = "SELECT time FROM backupschedule";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            statement = sqliteConnection.prepareStatement(jadwalnyaBackup);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayJadwal.add(rs.getString(1));
            }
            Collections.sort(arrayJadwal);
            Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(arrayJadwal);
            arrayJadwal = new ArrayList<String>(set);

            for (String nebular : arrayJadwal) {
                listOfJadwal.getItems().add(nebular);
            }

            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }

        //load migration schedule to listview
        String jadwalnyaMigrasi = "SELECT time FROM migrationschedule";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            statement = sqliteConnection.prepareStatement(jadwalnyaMigrasi);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayMigrasi.add(rs.getString(1));
            }
            Collections.sort(arrayMigrasi);
            Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(arrayMigrasi);
            arrayMigrasi = new ArrayList<String>(set);

            for (String nebular : arrayMigrasi) {
                listOfMigrasi.getItems().add(nebular);
            }

            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }

        if (autoBackupValue.equals("True")) {
            toggleBackup.setSelected(true);
            toggleBackup.setText("ON");
            jadwalBackup.setDisable(false);
        } else {
            toggleBackup.setSelected(false);
            toggleBackup.setText("OFF");
            jadwalBackup.setDisable(true);
        }

        //popup for listOfJadwal
        Label delete = new Label("Delete");
        delete.setOnMouseClicked(actionEvent -> {
            int selectedID = listOfJadwal.getSelectionModel().getSelectedIndex();

            try {
                String sql = "DELETE FROM backupschedule where time=?";
                Connection sqliteConnection = SQLiteConnection.Connector();
                PreparedStatement statement = sqliteConnection.prepareStatement(sql);
                statement.setString(1, arrayJadwal.get(selectedID));
                statement.executeUpdate();
                sqliteConnection.close();
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
            }

            listOfJadwal.getItems().remove(selectedID);
            arrayJadwal.remove(selectedID);
            popup.hide();
        });
        delete.setPadding(new Insets(10));
        VBox vbox = new VBox(delete);
        popup = new JFXPopup(vbox);

        //popup for listOfMigrasi
        Label deleteMigrasi = new Label("Delete");
        deleteMigrasi.setOnMouseClicked(actionEvent -> {
            int selectedID = listOfMigrasi.getSelectionModel().getSelectedIndex();

            try {
                String sql = "DELETE FROM migrationschedule where time=?";
                Connection sqliteConnection = SQLiteConnection.Connector();
                PreparedStatement statement = sqliteConnection.prepareStatement(sql);
                statement.setString(1, arrayMigrasi.get(selectedID));
                statement.executeUpdate();
                sqliteConnection.close();
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
            }

            listOfMigrasi.getItems().remove(selectedID);
            arrayMigrasi.remove(selectedID);
            popupMigrasi.hide();
        });
        deleteMigrasi.setPadding(new Insets(10));
        VBox vboxMigrasi = new VBox(deleteMigrasi);
        popupMigrasi = new JFXPopup(vboxMigrasi);
    }

    public void handleAction(javafx.event.ActionEvent actionEvent) {
        if(actionEvent.getSource() == jadwalBackup) {
            String backupTime = (jadwalBackup.getValue()!= null ? jadwalBackup.getValue().toString() : "");
            String[] splitTime =backupTime.split(":");
            String hours=splitTime[0];
            String minutes=splitTime[1];
        }
    }

    @FXML
    private void addJadwalToList(ActionEvent event) {
        listOfJadwal.getItems().clear();

        String sql = "SELECT count(1) FROM backupschedule WHERE time = '" + jadwalBackup.getValue() + "'";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                if (rs.getInt(1) == 1) {
                } else {
                    sqliteConnection.close();
                    System.out.println("inserting");
                    try {
                        Connection sqliteConnectionn = SQLiteConnection.Connector();
                        PreparedStatement psts = sqliteConnectionn.prepareStatement("INSERT INTO backupschedule " +
                                "(time)" + "VALUES (?) ");
                        psts.setString(1, String.valueOf(jadwalBackup.getValue()));
                        psts.executeUpdate();
                    } catch (SQLException throwables) {
                        logger.error(throwables);
                        throwables.printStackTrace();
                    }
                }
                sqliteConnection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        arrayJadwal.add(String.valueOf(jadwalBackup.getValue()));
        Collections.sort(arrayJadwal);

        Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(arrayJadwal);
        arrayJadwal = new ArrayList<String>(set);

        for (String nebular : arrayJadwal) {
            listOfJadwal.getItems().add(nebular);
        }
    }

    @FXML
    private void addMigrasiToList(ActionEvent event) {
        if (jadwalMigrasi.getValue() != null) {
            listOfMigrasi.getItems().clear();

            //check if time already exists
            String sql = "SELECT count(1) FROM migrationschedule WHERE time = '" + jadwalMigrasi.getValue() + "'";
            try {
                Connection sqliteConnection = SQLiteConnection.Connector();
                PreparedStatement statement = sqliteConnection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    if (rs.getInt(1) == 1) {
                    } else {
                        sqliteConnection.close();
                        try {
                            Connection sqliteConnectionn = SQLiteConnection.Connector();
                            PreparedStatement psts = sqliteConnectionn.prepareStatement("INSERT INTO migrationschedule " +
                                    "(time)" + "VALUES (?) ");
                            psts.setString(1, String.valueOf(jadwalMigrasi.getValue()));
                            psts.executeUpdate();
                        } catch (SQLException throwables) {
                            logger.error(throwables);
                            throwables.printStackTrace();
                        }
                    }
                    sqliteConnection.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            arrayMigrasi.add(String.valueOf(jadwalMigrasi.getValue()));
            Collections.sort(arrayMigrasi);

            Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(arrayMigrasi);
            arrayMigrasi = new ArrayList<String>(set);

            for (String nebular : arrayMigrasi) {
                listOfMigrasi.getItems().add(nebular);
            }

        }
    }

    @FXML
    private void showPopup(javafx.scene.input.MouseEvent event) {
        int selectedID = listOfJadwal.getSelectionModel().getSelectedIndex();
        if (event.getButton() == MouseButton.SECONDARY && selectedID != -1) {
            popup.show(listOfJadwal, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
        }
    }

    @FXML
    private void showPopupMigrasi(javafx.scene.input.MouseEvent event) {
        int selectedID = listOfMigrasi.getSelectionModel().getSelectedIndex();
        if (event.getButton() == MouseButton.SECONDARY && selectedID != -1) {
            popupMigrasi.show(listOfMigrasi, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
        }
    }

    public void settingPath() {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        Stage stage = (Stage) anchorid.getScene().getWindow();
        File file = dirchooser.showDialog(stage);
        if (file != null) {
            foxproPath.setText(file.getAbsolutePath());
        }
    }

    public void backupPath() {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        Stage stage = (Stage) anchorid.getScene().getWindow();
        File file = dirchooser.showDialog(stage);
        if (file != null) {
            backupPath.setText(file.getAbsolutePath());
        }
    }

    private void saveSettings(String sqlserver, String sqldb, String foxprodb, String autobackup, LocalTime jadwalbackup, String pathbackup) {
        String sql = "UPDATE settings set sqlserver = ?,sqldb = ?,foxprodb = ?,autobackup = ?," +
                "jadwalbackup = ?,pathbackup = ?,choosedbookmarks = ?  where id = ?";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            statement = sqliteConnection.prepareStatement(sql);
            statement.setString(1, sqlserver);
            statement.setString(2, sqldb);
            statement.setString(3, foxprodb);
            statement.setString(4, autobackup);
            statement.setString(5, String.valueOf(jadwalbackup));
            statement.setString(6, pathbackup);
            statement.setString(7, String.valueOf(bookMarksCb.getValue()));
            statement.setString(8, "1");
            statement.executeUpdate();
            sqliteConnection.close();
            DashboardController.selectedMigrationBookmarks = String.valueOf(bookMarksCb.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
        Stage settingsstage = (Stage) buttonCancel.getScene().getWindow();
        settingsstage.close();

    }

    public void setToggleBackup() {
        if (toggleBackup.isSelected()) {
            toggleBackup.setText("ON");
            jadwalBackup.setDisable(false);
            autoBackupValue = "True";
        } else {
            toggleBackup.setText("OFF");
            jadwalBackup.setDisable(true);
            autoBackupValue = "False";
        }
    }
}
