package sample.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kordamp.ikonli.javafx.FontIcon;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable{

    @FXML private Label loginMessageLabel;

    @FXML private AnchorPane panelLogin, panelServer;

    @FXML private JFXTextField usernameTextField, serverTextField, databaseTextField;

    @FXML private JFXPasswordField enterPasswordField;

    @FXML private JFXButton closeLogin, btnBack;

    @FXML private JFXComboBox comboBoxDepartement;

    static final Logger logger = Logger.getLogger(DashboardController.class.getName());
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String loginTime = "";
    RequiredFieldValidator validator;
    FontIcon warnIcon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PropertyConfigurator.configure(getClass().getResource("log4j.properties"));
        logger.info("Initializing login page");
        readSettings();
    }

    private void readSettings() {
        comboBoxDepartement.getItems().addAll(
                "Admin",
                "User"
        );


        String sqlite = "SELECT sqlserver, sqldb FROM settings";

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                serverTextField.setText(rs.getString(1));
                databaseTextField.setText(rs.getString(2));
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void handleClicks(MouseEvent event) {
        if (event.getSource() == closeLogin) {
            Platform.exit();
        } else if (event.getSource() == btnBack) {
            panelServer.toFront();
        }
    }

    public void loginButtonOnAction(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() && enterPasswordField.getText().isEmpty()) {
            loginMessageLabel.setText("Harap isi semua field");
        } else {
            validateLogin();
        }
    }

    Connection con;
    Stage dashboardstage;

    public void connectButtonOnAction(ActionEvent event) {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://"+serverTextField.getText()+";databaseName="+databaseTextField.getText());
            DatabaseConnection.serverName = serverTextField.getText();
            DatabaseConnection.databaseName = databaseTextField.getText();
            loginMessageLabel.setText("");
            panelLogin.toFront();
            updateSqlServer();
        } catch (Exception e) {
            loginMessageLabel.setText("Unknown Connection");
        }
    }

    public void validateLogin() {

        String sql = "SELECT count(1) FROM login WHERE username = '" +
                usernameTextField.getText().toLowerCase() + "' AND password = '" +
                enterPasswordField.getText().toLowerCase() + "' AND departement = '" +
                comboBoxDepartement.getValue() +"'";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()){
                if (rs.getInt(1) == 1) {

                    // send choosen departement to dashboard controller
                    DashboardController.departementString = String.valueOf(comboBoxDepartement.getValue()).toLowerCase();

                    try {
                        insertLoginHistory();
                        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/sample/views/dashboard.fxml"));
                        Parent root1 = (Parent) fxmlLoader.load();

                        Label labelUser = (Label) root1.lookup("#labelUser");
                        labelUser.setText(usernameTextField.getText());

                        Scene scene = new Scene(root1);
                        dashboardstage = new Stage();
//                        stage.setMaximized(true);
                        dashboardstage.setMinWidth(1000);
                        dashboardstage.setMinHeight(600);
                        scene.setFill(Color.TRANSPARENT);
                        Image icon = new Image(getClass().getResourceAsStream("/sample/image/bridge.png"));
                        dashboardstage.getIcons().add(icon);
                        dashboardstage.setTitle("Database Bridge");
                        dashboardstage.setScene(scene);

                        dashboardstage.setOnHiding(new EventHandler<WindowEvent>() {

                            @Override
                            public void handle(WindowEvent event) {
                                Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        logger.info("Application Closed by click to Close Button(X)");
                                        String logoutTime = LocalDateTime.now().format(timeFormatter);
                                        String querry = "UPDATE loginhistory set waktuout = ? where waktu = ?";
                                        try {
                                            Connection sqliteConnection = SQLiteConnection.Connector();
                                            PreparedStatement statement = sqliteConnection.prepareStatement(querry);
                                            statement.setString(1, logoutTime);
                                            statement.setString(2, loginTime);
                                            statement.executeUpdate();
                                            sqliteConnection.close();
                                        } catch (SQLException e) {
                                            logger.error(e);
                                            e.printStackTrace();
                                        }
                                        System.exit(0);
                                    }
                                });
                            }
                        });

                        dashboardstage.show();

                        Stage loginstage = (Stage) closeLogin.getScene().getWindow();
                        loginstage.close();

                    } catch (IOException e) {
                        logger.error(e);
                        System.exit(0);
                        e.printStackTrace();
                    }
                } else {
                    validator = new RequiredFieldValidator();
                    validator.setMessage("Departement Required");
                    warnIcon = new FontIcon();
                    warnIcon.setIconLiteral("fa-warning");
                    validator.setIcon(warnIcon);
                    comboBoxDepartement.getValidators().add(validator);

                    if (comboBoxDepartement.getSelectionModel().isEmpty()) {
                        //check if combobox not choosen
                        comboBoxDepartement.validate();
                    } else {
                        comboBoxDepartement.resetValidation();
                        //check if no credentials matched
                        loginMessageLabel.setText("Incorrect Credentials");
                    }


                }
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            logger.error(throwables);
            throwables.printStackTrace();
            throwables.getCause();
        }
    }

    private void updateSqlServer() {
        String querry = "UPDATE settings set sqlserver = ?,sqldb = ?";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(querry);
            statement.setString(1, serverTextField.getText());
            statement.setString(2, databaseTextField.getText());
            statement.executeUpdate();
            sqliteConnection.close();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    private void insertLoginHistory() {
        String currentDateTime = LocalDateTime.now().format(dateFormatter);
        loginTime = LocalDateTime.now().format(timeFormatter);
        DashboardController.loginTime = loginTime;

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement psts = sqliteConnection.prepareStatement("INSERT INTO loginhistory " +
                    "(user, waktu, waktuout, tanggal)" + "VALUES (?,?,?,?) ");
            psts.setString(1, usernameTextField.getText().toLowerCase());
            psts.setString(2, loginTime);
            psts.setString(3, "-");
            psts.setString(4, currentDateTime);
            psts.executeUpdate();
            sqliteConnection.close();
        } catch (SQLException throwables) {
            logger.error(throwables);
//            try {
//                sqliteConnection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            throwables.printStackTrace();
        }
    }
}
