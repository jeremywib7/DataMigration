package sample.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.linuxense.javadbf.*;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.skins.BarSkin;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kordamp.ikonli.javafx.FontIcon;
import sample.models.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DashboardController implements Initializable {

    @FXML private JFXTabPane paneMain, paneHistory;
    @FXML private JFXHamburger hamburger;
    @FXML private JFXDrawer drawer;
    @FXML private JFXProgressBar progressDialog, progressMigrasiSelected;
    @FXML private FontIcon iconYesNo;
    @FXML private AnchorPane panelDashboard, panelHistoryBackup, anchorid, paneMigrasi, paneTask;
    @FXML private StackPane rootPane;
    @FXML private Label labelProses, labelTotalSelected, labelProsesMigrasi,
            labelDialog, labelDate, backupPath, lMigrationSuccess, lMigrationCancell,
            lBackupSuccess, lBackupCancell;
    @FXML private VBox subHistory;
    @FXML private ScrollPane panelLog;
    @FXML private JFXButton btnDashboard,  btnHistory, btnHistoryBackup, btnCancelSelected,
            btnLog, btnHideProgress, saveId;
    //submenu
    @FXML private JFXButton btnPathBackup, btnBookmarks,
            btnYes, btnNo, btnCancelProgress, btnDataType, btnMigrasiTerpilih;
    @FXML private JFXComboBox bookmarksCb;
    @FXML private JFXDialog dialogYesNo, dialogProgressBar, dialogMigrateAll, dialogBackupBar;
    @FXML private JFXDialogLayout layoutYesNo, layoutProgressBar, layoutMigrateAll;
    @FXML private JFXCheckBox cbPilihSemua;
    @FXML private JFXTextArea textAreaLog;
    @FXML private JFXListView listOfMigrationSchedule, listOfBackupSchedule;
    @FXML private MFXTextField searchHistoryLogin, searchHistoryBackup, searchHistoryMigration;
    @FXML private JFXTextField searchDatabase;
    @FXML private JFXDatePicker loginDatePicker, backupDatePicker, migrationDatePicker;

    @FXML private TableView<DatabaseFoxpro> tableDashboard;//table dashboard
    @FXML private TableColumn<DatabaseFoxpro, BooleanProperty> cbDashboard;
    @FXML private TableColumn<DatabaseFoxpro, SimpleStringProperty> c1Dashboard;
    @FXML private TableColumn<DatabaseFoxpro, SimpleStringProperty> c2Dashboard;
    @FXML private TableColumn<DatabaseFoxpro, SimpleStringProperty> c3Dashboard;
    @FXML private TableColumn<DatabaseFoxpro, SimpleStringProperty> c4Dashboard;
    @FXML private TableColumn<DatabaseFoxpro, StringProperty> c5Dashboard;

    @FXML private TableView<LoginHistoryModel> tableHistoryLogin; //table history login
    @FXML private TableColumn<LoginHistoryModel, String> userLogin;
    @FXML private TableColumn<LoginHistoryModel, String> jamLogin;
    @FXML private TableColumn<LoginHistoryModel, String> jamLogout;

    @FXML private TableView<BackupHistoryModel> tableTaskBackup; //table task backup
    @FXML private TableColumn<BackupHistoryModel, String> c1Backup;
    @FXML private TableColumn<BackupHistoryModel, String> c2Backup;

    @FXML private TableView<MigrationHistoryModel> tableTaskMigrasi; //table history migration in today result menu
    @FXML private TableColumn<MigrationHistoryModel, String> c1Migrasi;
    @FXML private TableColumn<MigrationHistoryModel, String> c2Migrasi;
    @FXML private TableColumn<MigrationHistoryModel, String> c3Migrasi;

    @FXML private TableView<MigrationHistoryTabModel> tableHistoryMigrasi; //table history migration in tab pane
    @FXML private TableColumn<MigrationHistoryTabModel, String> c1HistoryMigrasi;
    @FXML private TableColumn<MigrationHistoryTabModel, String> c2HistoryMigrasi;
    @FXML private TableColumn<MigrationHistoryTabModel, String> c3HistoryMigrasi;
    @FXML private TableColumn<MigrationHistoryTabModel, String> c4HistoryMigrasi;
    @FXML private TableColumn<MigrationHistoryTabModel, String> c5HistoryMigrasi;

    @FXML private TableView<BackupHistoryTabModel> tableHistoryBackup; //table history backup in tab pane
    @FXML private TableColumn<BackupHistoryTabModel, String> c1HistoryBackup;
    @FXML private TableColumn<BackupHistoryTabModel, String> c2HistoryBackup;
    @FXML private TableColumn<BackupHistoryTabModel, String> c3HistoryBackup;
    @FXML private TableColumn<BackupHistoryTabModel, String> c4HistoryBackup;

    public static String departementString = "";
    public static String selectedMigrationBookmarks = "";

    String historyState = "Closed";
    DateTimeFormatter mindateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    DateTimeFormatter slashdateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter dotdateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    DateTimeFormatter dottimeFormatter = DateTimeFormatter.ofPattern("HH.mm.ss");
    DateTimeFormatter hhmmFormat = DateTimeFormatter.ofPattern("HH:mm");
    String autoBackupValue = ""; String activity = "";
    String currentDatabase = "", ekstensi = "";
    public static String loginTime = "";

    //Connection
    Connection con = DatabaseConnection.getSQLConnection();
    String sql; PreparedStatement pst; String sqlite;
    static final Logger logger = Logger.getLogger(DashboardController.class.getName());

    //list
    ObservableList<LoginHistoryModel> loginData = FXCollections.observableArrayList();
    ObservableList<String> listBookmarks = FXCollections.observableArrayList();

    ObservableList<File> filesToAdd =
            FXCollections.observableArrayList();
    Parent toolbarparent;
//    List<File> filesToAdd = List.of();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PropertyConfigurator.configure(getClass().getResource("log4j.properties"));
        //starting task
        loadSettings();
    }

    Timeline timeline = new Timeline();
    private Integer timeSeconds = 0;

    Timeline singleMigrateTimeline = new Timeline();
    private Integer countSingleInt = 0;

    Timeline multiMigrateTimeline = new Timeline();
    private Integer countMultiInt = 0;

    LocalTime schedule;
    private Service<Void> backupService;
    boolean backupDone =  false;

    public void handleClicks(MouseEvent event){
        if (event.getSource() == btnHistory) {
            if( historyState == "Closed") {
                subHistory.setVisible(true);
                subHistory.setManaged(true);
                historyState = "Opened";
            } else {
                subHistory.setVisible(false);
                subHistory.setManaged(false);
                historyState = "Closed";
            }
        } else if (event.getSource() == btnDashboard) {
            panelDashboard.toFront();
        } else if (event.getSource() == paneMain) {
            paneMain.toFront();
        } else if (event.getSource() == btnPathBackup) {
            settingBackupPath();
        } else if (event.getSource() == btnHistoryBackup) {
            panelHistoryBackup.toFront();
        } else if (event.getSource() == btnLog) {
            panelLog.toFront();
        }
    }

    Stage settingsStage;
    Parent settingsparent;

    {
        try {
            settingsparent = FXMLLoader.load(getClass().getResource("/sample/views/settings.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void loadWindow() {
        if (settingsStage == null) {
            FontIcon iconStatus = (FontIcon) settingsparent.lookup("#iconStatus");
            Label labelStatusBackup = (Label) settingsparent.lookup("#labelStatusBackup");
            Label labelTotalTime = (Label) settingsparent.lookup("#labelTotalTime");
            JFXButton btnBackupNow = (JFXButton) settingsparent.lookup("#btnBackupNow");

            btnBackupNow.setOnAction(actionEvent1 -> {
                timeSeconds = 0;
                currentBackupTime = (LocalDateTime.now().format(hhmmFormat));

                //change icon color and status
                FontIcon iconStatuss = (FontIcon) settingsparent.lookup("#iconStatus");
                iconStatuss.setIconColor(Color.YELLOW);

                Label labelStatusBackupp = (Label) settingsparent.lookup("#labelStatusBackup");
                labelStatusBackupp.setText("Running");

                backupService.reset();
                backupService.restart();
                btnBackupNow.setDisable(true);
            });

            settingsStage = new Stage(StageStyle.DECORATED);
            settingsStage.setTitle("Settings");
            Image icon = new Image(getClass().getResourceAsStream("/sample/image/settings.png"));
            settingsStage.getIcons().add(icon);
            settingsStage.setScene(new Scene(settingsparent));
            settingsStage.setResizable(false);
            settingsStage.setOnHiding(event -> Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    logger.info("Settings Closed by click to Close Button(X)");
                }
            }));
            settingsStage.show();
        } else if (settingsStage.isShowing()) {
            settingsStage.toFront();
        } else {
            settingsStage.show();
        }
    }

    Timeline migrationTimeline;
    String lastMigration = "";
    String currentMigration = "";

    Timeline backupTimeline;
    String lastBackup = "";
    String currentTime = "";
    String currentBackupTime = "";

    private void monitorTime() {
        backupTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        event -> {
                            for (String nebular : arrayJadwalBackup) {
                                if(autoBackupValue.equals("True")) {
                                    if (nebular.equals(LocalDateTime.now().format(hhmmFormat))) {
                                        if (!lastBackup.equals(LocalDateTime.now().format(hhmmFormat))) {
                                            lastBackup = LocalDateTime.now().format(hhmmFormat);
                                            backupTimeline.stop();
                                            currentTime = "";
                                            timeSeconds = 0;

                                            JFXButton btnBackupNow = (JFXButton) settingsparent.lookup("#btnBackupNow");
                                            btnBackupNow.setDisable(true);

                                            //change icon color and status
                                            FontIcon iconStatus = (FontIcon) settingsparent.lookup("#iconStatus");
                                            iconStatus.setIconColor(Color.YELLOW);

                                            Label labelStatusBackup = (Label) settingsparent.lookup("#labelStatusBackup");
                                            labelStatusBackup.setText("Running");

                                            backupService.reset();
                                            backupService.restart();
                                            currentBackupTime = nebular;
                                            logger.info("Backup scheduled for (" + nebular + ") has been started");
//                                            if(!backupService.isRunning()) {
//
//                                            }
                                        }
                                    }
                                }
                            }
                        }));
        backupTimeline.setCycleCount(Timeline.INDEFINITE);
        backupTimeline.play();

    }

    private void monitorMigration() {
        migrationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        event -> {
                            for (String nebular : arrayJadwalMigrasi) {
                                if (nebular.equals(LocalDateTime.now().format(hhmmFormat))) {
                                    if (!lastMigration.equals(LocalDateTime.now().format(hhmmFormat))) {
                                        if(!processDatabase.isRunning()) {
                                            lastMigration = LocalDateTime.now().format(hhmmFormat);

                                            String selectedData = "";

                                            //get sqlite data from selected bookmarks combobox
                                            String querry = "SELECT * FROM bookmarksdb where bookmarksname=?";

                                            try {
                                                Connection sqliteConnection = SQLiteConnection.Connector();
                                                PreparedStatement statement = sqliteConnection.prepareStatement(querry);
                                                statement.setString(1, selectedMigrationBookmarks);
                                                ResultSet rs = statement.executeQuery();

                                                while (rs.next()) {
                                                    selectedData = rs.getString(1);
                                                }
                                                String[] row = selectedData.split(",");
                                                sqliteConnection.close();

                                                //unselect all
                                                for(DatabaseFoxpro db : databaseFoxPro) {
                                                    if(cbPilihSemua.isSelected()) {
                                                        db.getSelect().setSelected(false);
                                                    } else {
                                                        db.getSelect().setSelected(false);
                                                    }
                                                }

                                                //set selected if equals
                                                for(DatabaseFoxpro db : databaseFoxPro) {
                                                    for(int i=0; i<row.length; i++) {
                                                        if(row[i].equals(db.getNamadatabase())) {
                                                            db.getSelect().setSelected(true);
                                                        }
                                                    }
                                                }
                                            } catch (SQLException e) {
                                                logger.error(e);
                                                e.printStackTrace();
                                            }

                                            migrationTimeline.stop();

                                            //display migration dialog
                                            countSelected = 0;

                                            multimigration = true;
                                            multiMigrationDate = LocalDateTime.now().format(mindateFormatter);
                                            multiMigrationTime = LocalDateTime.now().format(dottimeFormatter);

                                            listCount =  -1; currentCount = 0; counterSelected = 0;
                                            listCurrentDatabase.clear(); listPath.clear();
                                            dialogYesNo.close();
                                            layoutMigrateAll.setHeading(new Label("Scheduled Migration (Bookmarks : " + selectedMigrationBookmarks + ")" ));
                                            dialogMigrateAll.show();
                                            btnCancelSelected.setText("Cancel");
                                            btnCancelSelected.setOnAction(actionEvent1 -> {
                                                dialogMigrateAll.close();
                                                processDatabase.cancel();
                                            });

                                            for(DatabaseFoxpro bean : databaseFoxPro) {
                                                if(bean.getSelect().isSelected()) {
                                                    counterSelected++;
                                                    currentDatabase = bean.getNamadatabase();
                                                    path = location + "\\"+bean.getEkstensi();
                                                    listCurrentDatabase.add(currentDatabase);
                                                    listPath.add(path);
                                                }
                                            }

                                            labelTotalSelected.setText("Migrasi selesai : " + 0 +" dari "+ counterSelected + "\nProses Migrasi Database : " +
                                                    listCurrentDatabase.get(0) + "\n ");
                                            labelProsesMigrasi.textProperty().bind(processDatabase.messageProperty());
                                            progressMigrasiSelected.progressProperty().bind(processDatabase.progressProperty());

                                            processDatabase.reset();
                                            processDatabase.restart();
                                            currentMigration = nebular;
                                            logger.info("Migration scheduled for (" + currentMigration + ") has been started");
                                        }
                                    }
                                }
                            }
                        }));
        migrationTimeline.setCycleCount(Timeline.INDEFINITE);
        migrationTimeline.play();

    }

    @FXML
    private void changeHistoryLoginDate(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = loginDatePicker.getValue();
        setTableHistoryLogin(formatter.format(date));
    }

    @FXML
    private void changeHistoryMigrationDate(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = migrationDatePicker.getValue();
        setTableHistoryMigration(formatter.format(date));
    }

    @FXML
    private void changeHistoryBackupDate(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = backupDatePicker.getValue();
        setTableHistoryBackup(formatter.format(date));
    }

    ArrayList<String> listCurrentDatabase = new ArrayList<>();
    ArrayList<String> listPath = new ArrayList<>();

    @FXML
    private void migrateSelectedRow(ActionEvent event) {
        for(DatabaseFoxpro bean : databaseFoxPro) {
            if(bean.getSelect().isSelected()) {
                countSelected++;
            }
        }
        if (countSelected < 2) {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.GENERIC, "Information", "Please select at least 2 rows to process multi migration");
            dialog.setAlwaysOnTop(true);
            dialog.show();
        } else {
            layoutYesNo.setHeading(new Label("Migration "));
            labelDialog.setText("Confirm selected migration?");
            btnYes.setText("Migrate");
            iconYesNo.setIconLiteral("fa-exchange");

            btnYes.setOnAction(actionEvent -> {
                countSelected = 0;
                multimigration = true;

                multiMigrationDate = LocalDateTime.now().format(mindateFormatter);
                multiMigrationTime = LocalDateTime.now().format(dottimeFormatter);

                listCount =  -1; currentCount = 0; counterSelected = 0;
                listCurrentDatabase.clear(); listPath.clear();
                dialogYesNo.close();
                layoutMigrateAll.setHeading(new Label("Proses Migrasi Data Terpilih"));
                dialogMigrateAll.show();
                btnCancelSelected.setText("Cancel");
                btnCancelSelected.setOnAction(actionEvent1 -> {
                    dialogMigrateAll.close();
                    processDatabase.cancel();
                });

                for(DatabaseFoxpro bean : databaseFoxPro) {
                    if(bean.getSelect().isSelected()) {
                        counterSelected++;
                        currentDatabase = bean.getNamadatabase();
                        path = location + "\\"+bean.getEkstensi();
                        listCurrentDatabase.add(currentDatabase);
                        listPath.add(path);
                    }
                }

                labelTotalSelected.setText("Migrasi selesai : " + 0 +" dari "+ counterSelected + "\nProses Migrasi Database : " +
                        listCurrentDatabase.get(0) + "\n ");
                labelProsesMigrasi.textProperty().bind(processDatabase.messageProperty());
                progressMigrasiSelected.progressProperty().bind(processDatabase.progressProperty());

                processDatabase.reset();
                processDatabase.restart();

            });
            btnNo.setOnAction(actionEvent -> {
                dialogYesNo.close();
            });
            dialogYesNo.show();
        }
    }

    String loginSearchKeyword = "";
    String migrationSearchKeyword = "";
    String backupSearchKeyword = "";
    Thread t;

    //list for display today result table
    ObservableList<MigrationHistoryModel> migrationHistoryModel = FXCollections.observableArrayList();
    ObservableList<BackupHistoryModel> backupHistoryModel = FXCollections.observableArrayList();
    //list for display history migration
    ObservableList<MigrationHistoryTabModel> migrationHistoryTabModel = FXCollections.observableArrayList();
    //list for display history backup
    ObservableList<BackupHistoryTabModel> backupHistoryTabModel = FXCollections.observableArrayList();

    //list for display database
    private final ObservableList<DatabaseFoxpro> databaseFoxPro = FXCollections.observableArrayList();
    DatabaseFoxpro db;
    String searchKeyword = "";

    private void setTableDashboard() {
        cbDashboard.setCellValueFactory(new PropertyValueFactory<>("select"));
        c1Dashboard.setCellValueFactory(new PropertyValueFactory<>("id"));
        c2Dashboard.setCellValueFactory(new PropertyValueFactory<>("namadatabase"));
        c3Dashboard.setCellValueFactory(new PropertyValueFactory<>("migrasiterakhir"));
        c4Dashboard.setCellValueFactory(new PropertyValueFactory<>("dataterakhir"));
        c5Dashboard.setCellValueFactory(new PropertyValueFactory<>(""));
        tableDashboard.setEditable(true);

        Callback<TableColumn<DatabaseFoxpro, StringProperty>, TableCell<DatabaseFoxpro, StringProperty>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<DatabaseFoxpro, StringProperty> param) {
                        final TableCell<DatabaseFoxpro, StringProperty> cell = new TableCell<DatabaseFoxpro, StringProperty>() {

                            JFXButton button = new JFXButton("Migrasi");
                            JFXButton count = new JFXButton("Hitung");

                            HBox pane = new HBox(button, count);

                            @Override
                            public void updateItem(StringProperty item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    button.setOnAction(event -> {
                                        db = getTableView().getItems().get(getIndex());
                                        currentDatabase = db.getNamadatabase();
                                        ekstensi = db.getEkstensi();
                                        path = location + "\\"+db.getEkstensi();

                                        layoutYesNo.setHeading(new Label("Migrasi Database " + db.getNamadatabase()));
                                        labelDialog.setText("Konfirmasi Migrasi Database " + db.getNamadatabase() + " ?");
                                        btnYes.setText("Migrasi");
                                        iconYesNo.setIconLiteral("fa-exchange");

                                        btnYes.setOnAction(actionEvent -> {
                                            dialogYesNo.close();
                                            multimigration = false;
                                            layoutProgressBar.setHeading(new Label("Proses Migrasi Database "+ db.getNamadatabase() ));
                                            btnCancelProgress.setText("Cancel");
                                            btnCancelProgress.setOnAction(actionEvent1 -> {
                                                dialogProgressBar.close();
                                            });
                                            dialogProgressBar.setOnDialogClosed((JFXDialogEvent event1) ->{
                                                processDatabase.cancel();
                                            });
                                            dialogProgressBar.setOnDialogOpened((JFXDialogEvent event2) ->{
                                                labelProses.textProperty().bind(processDatabase.messageProperty());
                                                progressDialog.progressProperty().bind(processDatabase.progressProperty());
                                                processDatabase.reset();
                                                processDatabase.restart();
                                            });

                                            dialogProgressBar.show();
                                        });
                                        btnNo.setOnAction(actionEvent -> {
                                            dialogYesNo.close();
                                        });
                                        dialogYesNo.setOnDialogClosed((JFXDialogEvent event1) ->{
                                        });
                                        dialogYesNo.show();
                                    });
                                    setGraphic(button);
                                    button.setGraphicTextGap(7);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        c5Dashboard.setCellFactory(cellFactory);
        tableDashboard.setItems(databaseFoxPro);

        FilteredList<DatabaseFoxpro> filteredDatabase = new FilteredList<>(databaseFoxPro, b -> true);
        searchDatabase.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredDatabase.setPredicate(search -> {
                searchKeyword = searchDatabase.getText().toLowerCase();
                if(searchKeyword.isEmpty() || searchKeyword.isBlank()) {
                    return true;
                }
                if (search.getId().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (search.getNamadatabase().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (search.getMigrasiterakhir().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (search.getDataterakhir().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (search.getMigrasiterakhir().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else {
                    return false; //no match found
                }
            });
        });
        SortedList<DatabaseFoxpro> sortedData = new SortedList<>(filteredDatabase);
        sortedData.comparatorProperty().bind(tableDashboard.comparatorProperty());
        tableDashboard.setItems(sortedData);
    }

    private void setTableHistoryLogin(String datequerry) {
        loginData.clear();

        try {
            sqlite = "select * from loginhistory where tanggal =? ORDER BY waktu DESC";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (!rs.getString(2).equals(loginTime)) {
                    loginData.add(new LoginHistoryModel(rs.getString(1), rs.getString(2), rs.getString(3)));
                } else {
                    loginData.add(new LoginHistoryModel(rs.getString(1), rs.getString(2) + " (Current Login)", rs.getString(3)));
                }
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //set table cell for login history
        userLogin.setCellValueFactory(new PropertyValueFactory<>("user"));
        jamLogin.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        jamLogout.setCellValueFactory(new PropertyValueFactory<>("waktuout"));
        tableHistoryLogin.setItems(loginData);

        FilteredList<LoginHistoryModel> filteredDatabase = new FilteredList<>(loginData, b -> true);
        searchHistoryLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredDatabase.setPredicate(search -> {
                loginSearchKeyword = searchHistoryLogin.getText().toLowerCase();
                if(loginSearchKeyword.isEmpty() || loginSearchKeyword.isBlank()) {
                    return true;
                }
                if (search.getUser().toLowerCase().contains(loginSearchKeyword)) {
                    return true;
                } else if (search.getWaktu().toLowerCase().contains(loginSearchKeyword)) {
                    return true;
                } else if (search.getWaktuout().toLowerCase().contains(loginSearchKeyword)) {
                    return true;
                } else {
                    return false; //no match found
                }
            });
        });
        SortedList<LoginHistoryModel> sortedData = new SortedList<>(filteredDatabase);
        sortedData.comparatorProperty().bind(tableHistoryLogin.comparatorProperty());
        tableHistoryLogin.setItems(sortedData);
    }

    private void setTableHistoryMigration(String datequerry) {
        migrationHistoryTabModel.clear();

        try {
            sqlite = "select * from migrationhistory where tanggal =? ORDER BY jam DESC";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                migrationHistoryTabModel.add(new MigrationHistoryTabModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
            sqliteConnection.close();
            tableHistoryMigrasi.setItems(migrationHistoryTabModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setFilterTabHistoryMigration();
    }

    private void setTableHistoryBackup(String datequerry) {
        backupHistoryTabModel.clear();

        try {
            sqlite = "select * from backuphistory where tanggal =? ORDER BY jam DESC";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                backupHistoryTabModel.add(new BackupHistoryTabModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setFilterTabHistoryBackup();
    }

    private void setFilterTabHistoryBackup() {

        FilteredList<BackupHistoryTabModel> filteredDatabase = new FilteredList<>(backupHistoryTabModel, b -> true);
        searchHistoryBackup.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredDatabase.setPredicate(search -> {
                backupSearchKeyword = searchHistoryBackup.getText().toLowerCase();
                if(backupSearchKeyword.isEmpty() || backupSearchKeyword.isBlank()) {
                    return true;
                }
                if (search.getTanggal().toLowerCase().contains(backupSearchKeyword)) {
                    return true;
                } else if (search.getJam().toLowerCase().contains(backupSearchKeyword)) {
                    return true;
                } else if (search.getFolder().toLowerCase().contains(backupSearchKeyword)) {
                    return true;
                } else if (search.getStatus().toLowerCase().contains(backupSearchKeyword)) {
                    return true;
                } else {
                    return false; //no match found
                }
            });
        });

        SortedList<BackupHistoryTabModel> sortedData = new SortedList<>(filteredDatabase);
        sortedData.comparatorProperty().bind(tableHistoryBackup.comparatorProperty());
        tableHistoryBackup.setItems(sortedData);
    }

    @FXML
    private void getBookmarksSelection(ActionEvent event) {
        String selectedData = "";

        //get sqlite data from selected bookmarks combobox
        String querry = "SELECT * FROM bookmarksdb where bookmarksname=?";

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(querry);
            statement.setString(1, String.valueOf(bookmarksCb.getValue()));
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                selectedData = rs.getString(1);
            }
            String[] row = selectedData.split(",");
            sqliteConnection.close();

            //unselect all
            for(DatabaseFoxpro db : databaseFoxPro) {
                if(cbPilihSemua.isSelected()) {
                    db.getSelect().setSelected(false);
                } else {
                    db.getSelect().setSelected(false);
                }
            }

            //set selected if equals
            for(DatabaseFoxpro db : databaseFoxPro) {
                for(int i=0; i<row.length; i++) {
                    if(row[i].equals(db.getNamadatabase())) {
                        db.getSelect().setSelected(true);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }

    }
    FontIcon testFont;

    private void setTableTaskBackup(String datequerry) {
        backupHistoryModel.clear();
        try {
            sqlite = "select * from backuphistory where tanggal =? ORDER BY jam DESC";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (!rs.getString(2).equals(loginTime)) {
                    backupHistoryModel.add(new BackupHistoryModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
                } else {
                    backupHistoryModel.add(new BackupHistoryModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
                }
            }
            sqliteConnection.close();
//            ta.setItems(loginData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        c1Backup.setCellValueFactory(new PropertyValueFactory<>("jam"));
        c2Backup.setCellValueFactory(new PropertyValueFactory<>("status"));
        c2Backup.setCellFactory(column -> new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    Text text = new Text(item);
                    text.getStyleClass().add("coltext");
                    VBox textVBox = new VBox(text);
                    textVBox.setAlignment(Pos.CENTER_LEFT);

                    if (text.getText().equals("Success")) {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.GREEN);
                    } else {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.RED);
                    }

                    VBox fontVBox = new VBox(testFont);
                    fontVBox.setAlignment(Pos.CENTER);

                    fontVBox.setMinWidth(30);
                    fontVBox.setPrefWidth(30);
                    fontVBox.setMaxWidth(30);

                    textVBox.setMinWidth(30);
                    textVBox.setPrefWidth(30);
                    textVBox.setMaxWidth(30);

                    HBox pane = new HBox(fontVBox, textVBox);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(3);
                    setGraphic(pane);

                } else {
                    setGraphic(null);
                }
            }
        });

        tableTaskBackup.setEditable(true);
        tableTaskBackup.setItems(backupHistoryModel);
    }

    private void setTableTaskMigration(String datequerry) {
        migrationHistoryModel.clear();

        //display data in tableview
        try {
            sqlite = "select * from dbhistory where date =? ORDER BY time DESC";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (!rs.getString(2).equals(loginTime)) {
                    migrationHistoryModel.add(new MigrationHistoryModel(rs.getString(3), rs.getString(1), rs.getString(4), rs.getString(5)));
                } else {
                    migrationHistoryModel.add(new MigrationHistoryModel(rs.getString(3), rs.getString(1), rs.getString(4), rs.getString(5)));
                }
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //display success and cancelled migration
        try {
            sqlite = "select count(*) from dbhistory where status = ? and date = ?";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, "Success");
            statement.setString(2, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int i = rs.getInt(1); // access first column in result
                lMigrationSuccess.setText(String.valueOf(i));
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //display success and cancelled migration
        try {
            sqlite = "select count(*) from dbhistory where status = ? and date = ?";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, "Cancelled");
            statement.setString(2, datequerry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int i = rs.getInt(1); // access first column in result
                lMigrationCancell.setText(String.valueOf(i));
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        c1Migrasi.setCellValueFactory(new PropertyValueFactory<>("jam"));
        c2Migrasi.setCellValueFactory(new PropertyValueFactory<>("dbname"));
        c3Migrasi.setCellValueFactory(new PropertyValueFactory<>("status"));
        c3Migrasi.setCellFactory(column -> new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    Text text = new Text(item);
                    text.getStyleClass().add("coltext");
                    VBox textVBox = new VBox(text);
                    textVBox.setAlignment(Pos.CENTER_LEFT);

                    if (text.getText().equals("Success")) {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.GREEN);
                    } else {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.RED);
                    }

                    VBox fontVBox = new VBox(testFont);
                    fontVBox.setAlignment(Pos.CENTER);

                    fontVBox.setMinWidth(30);
                    fontVBox.setPrefWidth(30);
                    fontVBox.setMaxWidth(30);

                    textVBox.setMinWidth(30);
                    textVBox.setPrefWidth(30);
                    textVBox.setMaxWidth(30);

                    HBox pane = new HBox(fontVBox, textVBox);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(3);
                    setGraphic(pane);

                } else {
                    setGraphic(null);
                }
            }
        });

        tableTaskMigrasi.setEditable(true);
        tableTaskMigrasi.setItems(migrationHistoryModel);
    }

    String pathHari = "";
    String todayDate = "";
    String todayTime = "";
    String backupResult = "Success";
    Timeline timer1;

    private void processBackup() {
        backupService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {

                    @Override
                    protected Void call() throws ZipException {
//                        checkFolderExists();
                        timeline.playFromStart();
                        System.out.println("Backup Started");
                        gauge.setValue(0);
                        gauge.setTitle("BACKUP");

//                        buttonPathBackup.setDisable(true);

                        todayDate = LocalDateTime.now().format(dotdateFormatter);
                        todayTime = LocalDateTime.now().format(dottimeFormatter);

                        Calendar cal = Calendar.getInstance();
                        int day = cal.get(Calendar.DAY_OF_WEEK);

                        try {
                            switch (day) {
                                case 1 -> pathHari = backup + "//Minggu//";
                                case 2 -> pathHari = backup + "//Senin//";
                                case 3 -> pathHari = backup + "//Selasa//";
                                case 4 -> pathHari = backup + "//Rabu//";
                                case 5 -> pathHari = backup + "//Kamis//";
                                case 6 -> pathHari = backup + "//Jumat//";
                                case 7 -> pathHari = backup + "//Sabtu//";
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                            e.getCause();
                        }

                        backupResult = "Success";

                        ZipFile zipFile = new ZipFile(pathHari+"/Backup Tradding ("+todayDate+" - "+todayTime+").zip");
                        zipFile.addFiles(filesToAdd);

                        //display successful and cancelled backup
                         logger.info("Backup scheduled for: " + currentBackupTime + ". Result: Success");

                        return null;
                    }
                };
            }
        };

        backupService.setOnSucceeded( e -> {
//          for monitor total backup time
            timeline.stop();
            insertHistoryBackup(currentBackupTime,"Success");
//          start monitoring again for next backup
            backupTimeline.playFromStart();
            displayTotalBackup();

            JFXButton btnBackupNow = (JFXButton) settingsparent.lookup("#btnBackupNow");
            btnBackupNow.setDisable(false);

            FontIcon iconStatus = (FontIcon) settingsparent.lookup("#iconStatus");
            iconStatus.setIconColor(Color.GREEN);

            Label labelStatusBackup = (Label) settingsparent.lookup("#labelStatusBackup");
            labelStatusBackup.setText("Completed");
        });
    }

    private void displayTotalBackup() {
        String slash = LocalDateTime.now().format(slashdateFormatter);
        //display total success backup
        try {
            sqlite = "select count(*) from backuphistory where status = ? and tanggal = ?";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, "Success");
            statement.setString(2, slash);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int i = rs.getInt(1); // access first column in result
                lBackupSuccess.setText(String.valueOf(i));
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //display total cancelled backup
        try {
            sqlite = "select count(*) from backuphistory where status = ?";
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            statement.setString(1, "Cancelled");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int i = rs.getInt(1); // access first column in result
                lBackupCancell.setText(String.valueOf(i));
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertHistoryMigration(String tanggal, String jam, String dbname, String total, String result) {
        //insert into sqlite history
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement psts = sqliteConnection.prepareStatement("INSERT INTO migrationhistory " +
                    "(tanggal, jam, dbname, total, result)" + "VALUES (?,?,?,?,?) ");
            psts.setString(1, tanggal);
            psts.setString(2, jam);
            psts.setString(3, dbname);
            psts.setString(4, total);
            psts.setString(5, result);
            psts.executeUpdate();
        } catch (SQLException throwables) {
            logger.error(throwables);
            throwables.printStackTrace();
        }
        //add into first index in table
        migrationHistoryTabModel.add(0, new MigrationHistoryTabModel(tanggal, jam, dbname, total,result));

    }

    private void insertHistoryBackup(String jam,String status) {

        //insert into sqlite history
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement psts = sqliteConnection.prepareStatement("INSERT INTO backuphistory " +
                    "(tanggal, jam, folder, status)" + "VALUES (?,?,?,?) ");
            psts.setString(1, LocalDateTime.now().format(slashdateFormatter));
            psts.setString(2, jam);
            psts.setString(3, backup);
            psts.setString(4, status);
            psts.executeUpdate();
        } catch (SQLException throwables) {
            logger.error(throwables);
            throwables.printStackTrace();
        }

        backupHistoryModel.add(0, new BackupHistoryModel(LocalDateTime.now().format(slashdateFormatter), jam, backup, status));
        backupHistoryTabModel.add(0, new BackupHistoryTabModel(LocalDateTime.now().format(slashdateFormatter), jam, backup, status));
    }

    String backup = "";

    private void checkFolderExists() {

        File directory = new File(backup);
        if (! directory.exists()){
            directory.mkdir();
        }
        File folderSenin = new File(backup+"\\Senin");
        if (! folderSenin.exists()){
            folderSenin.mkdir();
        }
        File folderSelasa = new File(backup+"\\Selasa");
        if (! folderSelasa.exists()){
            folderSelasa.mkdir();
        }
        File folderRabu = new File(backup+"\\Rabu");
        if (! folderRabu.exists()){
            folderRabu.mkdir();
        }
        File folderKamis = new File(backup+"\\Kamis");
        if (! folderKamis.exists()){
            folderKamis.mkdir();
        }
        File folderJumat = new File(backup+"\\Jumat");
        if (! folderJumat.exists()){
            folderJumat.mkdir();
        }
        File folderSabtu = new File(backup+"\\Sabtu");
        if (! folderSabtu.exists()){
            folderSabtu.mkdir();
        }
        File folderMinggu = new File(backup+"\\Minggu");
        if (! folderMinggu.exists()){
            folderMinggu.mkdir();
        }
    }

    boolean pilihSemua =  false;
    Gauge gauge;
    Parent parent;

    public void loadSettings() {
        //some background task
        initClock();
        setCountdownDialog();
        displayTotalBackup();
        backgroundTask();
        processBackup();
        monitorTime();
        monitorMigration();
        setTableDashboard();

        //creating timeline for counting backup total time timeline
        Label labelTotalTime = (Label) settingsparent.lookup("#labelTotalTime");

        timeSeconds = 0;
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add( new KeyFrame(Duration.seconds(1), new EventHandler() {
            @Override public void handle(Event event) {
                timeSeconds++;
                labelTotalTime.setText("Total Time : " + timeSeconds.toString() + " Seconds");}}));

        //set table and time
        String date = LocalDateTime.now().format(slashdateFormatter);
        String mindate = LocalDateTime.now().format(mindateFormatter);
        LocalDate localDate = LocalDate.parse(date, slashdateFormatter);

        loginDatePicker.setValue(localDate);
        migrationDatePicker.setValue(localDate);
        backupDatePicker.setValue(localDate);

        setTableHistoryLogin(date);
        setTableTaskBackup(date);
        setTableTaskMigration(mindate);

        //set table cell for tableview history
        c1HistoryMigrasi.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        c2HistoryMigrasi.setCellValueFactory(new PropertyValueFactory<>("jam"));
        c3HistoryMigrasi.setCellValueFactory(new PropertyValueFactory<>("dbname"));
        c4HistoryMigrasi.setCellValueFactory(new PropertyValueFactory<>("total"));
        c5HistoryMigrasi.setCellValueFactory(new PropertyValueFactory<>("status"));
        c5HistoryMigrasi.setCellFactory(column -> new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    Text text = new Text(item);
                    text.getStyleClass().add("coltext");
                    VBox textVBox = new VBox(text);
                    textVBox.setAlignment(Pos.CENTER_LEFT);

                    if (text.getText().equals("Success")) {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.GREEN);
                    } else {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.RED);
                    }

                    VBox fontVBox = new VBox(testFont);
                    fontVBox.setAlignment(Pos.CENTER);

                    fontVBox.setMinWidth(30);
                    fontVBox.setPrefWidth(30);
                    fontVBox.setMaxWidth(30);

                    textVBox.setMinWidth(30);
                    textVBox.setPrefWidth(30);
                    textVBox.setMaxWidth(30);

                    HBox pane = new HBox(fontVBox, textVBox);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(3);
                    setGraphic(pane);

                } else {
                    setGraphic(null);
                }
            }
        });

        tableHistoryMigrasi.setEditable(true);
        tableHistoryMigrasi.setItems(migrationHistoryTabModel);


        //set table cell for tab history backup
        c1HistoryBackup.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        c2HistoryBackup.setCellValueFactory(new PropertyValueFactory<>("jam"));
        c3HistoryBackup.setCellValueFactory(new PropertyValueFactory<>("folder"));
        c4HistoryBackup.setCellValueFactory(new PropertyValueFactory<>("status"));
        c4HistoryBackup.setCellFactory(column -> new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    Text text = new Text(item);
                    text.getStyleClass().add("coltext");
                    VBox textVBox = new VBox(text);
                    textVBox.setAlignment(Pos.CENTER_LEFT);

                    if (text.getText().equals("Success")) {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.GREEN);
                    } else {
                        testFont = new FontIcon("fa-circle");
                        testFont.setIconColor(Color.RED);
                    }

                    VBox fontVBox = new VBox(testFont);
                    fontVBox.setAlignment(Pos.CENTER);

                    fontVBox.setMinWidth(30);
                    fontVBox.setPrefWidth(30);
                    fontVBox.setMaxWidth(30);

                    textVBox.setMinWidth(30);
                    textVBox.setPrefWidth(30);
                    textVBox.setMaxWidth(30);

                    HBox pane = new HBox(fontVBox, textVBox);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(3);
                    setGraphic(pane);

                } else {
                    setGraphic(null);
                }
            }
        });

        tableHistoryBackup.setEditable(true);
        tableHistoryBackup.setItems(backupHistoryTabModel);

        //gauge settings
        gauge = new Gauge();

        gauge.setSkin(new BarSkin(gauge));  //ModernSkin : you guys can change the skin
        gauge.setTitle("BACKUP");  //title
        gauge.setUnit("Percent");  //unit
        gauge.setUnitColor(Color.WHITE);
        gauge.setDecimals(0);
        gauge.setValue(0.00); //deafult position of needle on gauage
        gauge.setAnimated(true);

        gauge.setValueColor(Color.YELLOW);
        gauge.setTitleColor(Color.YELLOW);
        gauge.setSubTitleColor(Color.WHITE);
        gauge.setBarColor(Color.rgb(255, 255, 141));
        gauge.setNeedleColor(Color.RED);
        gauge.setThresholdColor(Color.RED);  //color will become red if it crosses thereshold value
        gauge.setThreshold(85);
        gauge.setThresholdVisible(true);
        gauge.setTickLabelColor(Color.rgb(151, 151, 151));
        gauge.setTickMarkColor(Color.WHITE);
        gauge.setTickLabelOrientation(TickLabelOrientation.ORTHOGONAL);

        paneMain.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            paneMain.setTabMinWidth(paneMain.getWidth() / paneMain.getTabs().size());
            paneMain.setTabMaxWidth(paneMain.getWidth() / paneMain.getTabs().size());
        });

        paneHistory.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            paneHistory.setTabMinWidth(paneHistory.getWidth() / paneHistory.getTabs().size());
            paneHistory.setTabMaxWidth(paneHistory.getWidth() / paneHistory.getTabs().size());
        });

        try {
            toolbarparent = new FXMLLoader().load(getClass().getResource("/sample/views/toolbar.fxml"));

            if (departementString.equals("User") || departementString.equals("user")) {
                JFXButton buttonSettings = (JFXButton) toolbarparent.lookup("#buttonSettings");
                buttonSettings.setVisible(false);
                buttonSettings.setManaged(false);

                btnBookmarks.setVisible(false);
                btnDataType.setVisible(false);
                btnMigrasiTerpilih.setVisible(false);
                bookmarksCb.setVisible(false);
                cbPilihSemua.setVisible(false);

                //table dashboard
                tableDashboard.setTableMenuButtonVisible(false);
                cbDashboard.setVisible(false);
                c5Dashboard.setVisible(false);
            }

            VBox toolbar = (VBox) toolbarparent;
            drawer.setSidePane(toolbar);

            for (Node node : toolbar.getChildren()) {

                if (node.getAccessibleText()!=null) {
                    node.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {

                        switch (node.getAccessibleText()) {
                            case "navTask":
                                paneTask.toFront();
                                break;
                            case "navDashboard":
                                paneMain.toFront();
                                break;
                            case "navMigrasi":
                                paneMigrasi.toFront();
                                break;
                            case "navHistory":
                                paneHistory.toFront();
                                break;
                            case "navSettings":
                                loadWindow();
                                break;
                            case "navLogout":
                                signOut();
                                break;
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug(e);
        }

        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            drawer.toggle();
        });
        drawer.setOnDrawerOpening((event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.setMinWidth(220);
        });
        drawer.setOnDrawerClosed((event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.setMinWidth(0);
        });
        drawer.setMinWidth(0);

        cbPilihSemua.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                for(DatabaseFoxpro db : databaseFoxPro) {
                    if(cbPilihSemua.isSelected()) {
                        bookmarksCb.getSelectionModel().clearSelection();
                        countSelected++;
                        db.getSelect().setSelected(true);
                    } else {
                        db.getSelect().setSelected(false);
                    }
                }
                if(pilihSemua) {
                    countSelected = 0;
                    cbPilihSemua.setText("Check All");
                    pilihSemua = false;
                } else {
                    cbPilihSemua.setText("Uncheck All");
                    pilihSemua = true;
                }
            }
        });
        SettingsController.buttonOk.setOnAction(actionEvent -> {
            Task<Void> sleeper = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                    return null;
                }
            };
            sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    databaseFoxPro.clear();
                    readSettings();
                    setTableDashboard();
                }
            });
            new Thread(sleeper).start();
        });

        dialogYesNo.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialogYesNo.setDialogContainer(rootPane);

        dialogProgressBar.setTransitionType(JFXDialog.DialogTransition.TOP);
        dialogProgressBar.setOverlayClose(false);
        dialogProgressBar.setDialogContainer(rootPane);

        dialogMigrateAll.setTransitionType(JFXDialog.DialogTransition.TOP);
        dialogMigrateAll.setDialogContainer(rootPane);
        dialogMigrateAll.setOverlayClose(false);

        dialogBackupBar.setTransitionType(JFXDialog.DialogTransition.TOP);
        dialogBackupBar.setDialogContainer(rootPane);
        dialogBackupBar.setOverlayClose(false);
        readSettings();
    }

    String location;
    File folder;

    private void setCountdownDialog() {
        //timer timeout for closing single migration dialog
        singleMigrateTimeline.setCycleCount(Timeline.INDEFINITE);
        singleMigrateTimeline.getKeyFrames().add( new KeyFrame(Duration.seconds(1), new EventHandler() {
            @Override public void handle(Event event) {
                countSingleInt--;
                if( countSingleInt == 0) {
                    singleMigrateTimeline.stop();
                    dialogProgressBar.close();
                }
                btnCancelProgress.setDisable(false);
                btnCancelProgress.setText("Ok (" +countSingleInt.toString() + ")");}}));

        //timer timeout for closing multi migration dialog
        multiMigrateTimeline.setCycleCount(Timeline.INDEFINITE);
        multiMigrateTimeline.getKeyFrames().add( new KeyFrame(Duration.seconds(1), new EventHandler() {
            @Override public void handle(Event event) {
                countMultiInt--;
                if( countMultiInt == 0) {
                    multiMigrateTimeline.stop();
                    dialogMigrateAll.close();
                }
                btnCancelSelected.setDisable(false);
                btnCancelSelected.setText("Ok (" +countMultiInt.toString() + ")");}}));

    }

    public void readSettings() {
        String todayDate = LocalDateTime.now().format(mindateFormatter);
        String slashdate = LocalDateTime.now().format(slashdateFormatter);

        migrationHistoryTabModel.clear();

        //set table in tab history migration and filter
        setTableHistoryMigration(todayDate);

        //set table history backup in tab pane
        backupHistoryTabModel.clear();

        //set table in tab history backup and filter
        setTableHistoryBackup(slashdate);

        //read newest settings
        databaseFoxPro.clear();
        String sqlite = "SELECT sqlserver, sqldb, foxprodb, autobackup, jadwalbackup, pathbackup, choosedbookmarks FROM settings";

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(sqlite);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                checkFolderExists();
                location = rs.getString(3);
                folder = new File(location);
                autoBackupValue = rs.getString(4);
                String scheduled = rs.getString(5);
                backup = rs.getString(6);
                selectedMigrationBookmarks = rs.getString(7);
                schedule = LocalTime.parse(scheduled);

                File folder = new File(location);
                System.out.println(location);

                filesToAdd.clear();
                //add listOfAllFiles to backup in list filestoadd
                File[] listOfAllFiles = folder.listFiles();

                for (int i = 0; i < Objects.requireNonNull(listOfAllFiles).length; i++) {
                    if (listOfAllFiles[i].isFile()) {
                        String name = listOfAllFiles[i].getAbsolutePath();
                        filesToAdd.add(new File(name));
                    } else if (listOfAllFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfAllFiles[i].getName());
                    }
                }

                File[] listOfPhnFiles = folder.listFiles((dir, name) -> name.endsWith(".phn")|| name.endsWith(".PHN") );

                for (int i = 0; i < Objects.requireNonNull(listOfPhnFiles).length; i++) {
                    if (listOfPhnFiles[i].isFile()) {
                        String name = listOfPhnFiles[i].getName();
                        String sql = "SELECT * FROM dbhistory where dbname = ?";
                        try {
                            PreparedStatement prestmt = sqliteConnection.prepareStatement(sql);
                            if (name.indexOf(".") > 0)
                                name = name.substring(0, name.lastIndexOf("."));
                                prestmt.setString(1, name.toLowerCase());
                            {
                                ResultSet rst = prestmt.executeQuery();
                                if (rst.next()) {
                                    String migrasiTerakhir = rst.getString("date");
                                    String jamTerakhir = rst.getString("time");
                                    String dataTerakhir = rst.getString("ldata");
                                    if (dataTerakhir.equals("0")) {
                                        dataTerakhir = "No Data";
                                    }
                                    databaseFoxPro.add(new DatabaseFoxpro(String.valueOf(i+1), name.toLowerCase(Locale.ROOT), migrasiTerakhir + " - " +jamTerakhir, dataTerakhir, listOfPhnFiles[i].getName().toLowerCase(Locale.ROOT)));
                                } else {
                                    databaseFoxPro.add(new DatabaseFoxpro(String.valueOf(i+1), name.toLowerCase(Locale.ROOT), "-", "-", listOfPhnFiles[i].getName().toLowerCase(Locale.ROOT)));
                                    try {
                                        PreparedStatement psts = sqliteConnection.prepareStatement("INSERT INTO dbhistory " +
                                                "(dbname, ldata, date, time)" + "VALUES (?,?,?,?) ");
                                        psts.setString(1, name.toLowerCase());
                                        psts.setString(2, "");
                                        psts.setString(3, "");
                                        psts.setString(4, "");
                                        psts.executeUpdate();
                                    } catch (SQLException throwables) {
                                        logger.error(throwables);
                                        throwables.printStackTrace();
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            logger.error(e);
                            e.printStackTrace();
                        }
                    } else if (listOfPhnFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfPhnFiles[i].getName());
                    }
                }
            }
            sqliteConnection.close();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }

        //TODO load combobox bookmarks
        loadComboboxBookmarks();

        //TODO load backup schedule
        loadBackupSchedule();

        //TODO load migration schedule
        loadMigrationSchedule();
    }

    private void setFilterTabHistoryMigration() {

        FilteredList<MigrationHistoryTabModel> filteredDatabase = new FilteredList<>(migrationHistoryTabModel, b -> true);
        searchHistoryMigration.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredDatabase.setPredicate(search -> {
                migrationSearchKeyword = searchHistoryMigration.getText().toLowerCase();
                if(migrationSearchKeyword.isEmpty() || migrationSearchKeyword.isBlank()) {
                    return true;
                }
                if (search.getTanggal().toLowerCase().contains(migrationSearchKeyword)) {
                    return true;
                } else if (search.getDbname().toLowerCase().contains(migrationSearchKeyword)) {
                    return true;
                } else if (search.getJam().toLowerCase().contains(migrationSearchKeyword)) {
                    return true;
                } else if (search.getTotal().toLowerCase().contains(migrationSearchKeyword)) {
                    return true;
                } else if (search.getStatus().toLowerCase().contains(migrationSearchKeyword)) {
                    return true;
                }else {
                    return false; //no match found
                }
            });
        });
        SortedList<MigrationHistoryTabModel> sortedData = new SortedList<>(filteredDatabase);
        sortedData.comparatorProperty().bind(tableHistoryMigrasi.comparatorProperty());
        tableHistoryMigrasi.setItems(sortedData);
    }

    ArrayList<String> arrayJadwalMigrasi = new ArrayList<>();

    private void loadMigrationSchedule() {
        listOfMigrationSchedule.getItems().clear();
        arrayJadwalMigrasi.clear();
        String jadwalnyaBackup = "SELECT time FROM migrationschedule";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(jadwalnyaBackup);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayJadwalMigrasi.add(rs.getString(1));
            }
            Collections.sort(arrayJadwalMigrasi);
            Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(arrayJadwalMigrasi);
            arrayJadwalMigrasi = new ArrayList<String>(set);

            for (String nebular : arrayJadwalMigrasi) {
                listOfMigrationSchedule.getItems().add(nebular);
            }

            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    ArrayList<String> arrayJadwalBackup = new ArrayList<>();

    private void loadBackupSchedule() {
        listOfBackupSchedule.getItems().clear();
        arrayJadwalBackup.clear();
        String jadwalnyaBackup = "SELECT time FROM backupschedule";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(jadwalnyaBackup);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayJadwalBackup.add(rs.getString(1));
            }
            Collections.sort(arrayJadwalBackup);
            Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(arrayJadwalBackup);
            arrayJadwalBackup = new ArrayList<String>(set);

            for (String nebular : arrayJadwalBackup) {
                listOfBackupSchedule.getItems().add(nebular);
            }

            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }


    private void loadComboboxBookmarks() {
        //load combobox bookmarks
        bookmarksCb.getItems().clear();
        bookmarksCb.setValue(null);

        String querry = "SELECT * FROM bookmarksdb";

        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(querry);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                listBookmarks.add(rs.getString(2));
            }

            bookmarksCb.setItems(listBookmarks);

            sqliteConnection.close();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    public void settingBackupPath() {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        Stage stage = (Stage) anchorid.getScene().getWindow();
        File file = dirchooser.showDialog(stage);
        if (file != null) {
            backupPath.setText(file.getAbsolutePath()+"\\Backup Tradding Otomatis");
        }
    }

    public void signOut() {
        layoutYesNo.setHeading(new Label("Sign Out"));
        labelDialog.setText("Konfirmasi Sign Out?");
        btnYes.setText("Sign Out");
        iconYesNo.setIconLiteral("fa-sign-out");
        //buttons
        btnYes.setOnAction(actionEvent -> {
            dialogYesNo.close();

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
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
        });
        btnNo.setOnAction(actionEvent -> {
            dialogYesNo.close();
        });
        dialogYesNo.setOnDialogClosed((JFXDialogEvent event1) ->{
        });
        dialogYesNo.show();
    }

    String currentDateTime = "";

    public void initClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            currentDateTime = LocalDateTime.now().format(dateFormatter) +" - "+ LocalDateTime.now().format(timeFormatter);
            labelDate.setText(currentDateTime);
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void backgroundTask() {
        DateTimeFormatter a = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter b = DateTimeFormatter.ofPattern("HH:mm:ss");
        String todayDate = LocalDateTime.now().format(a);
        String todayTime = LocalDateTime.now().format(b);
        activity = "Proses Migrasi Dimulai";

        try {
            con = DatabaseConnection.getSQLConnection();
            databaseTask();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    String database1Status = ""; String path = "";
    private Service<Void> processDatabase;
    DBFReader reader = null; Object[] rowObjects = null;
    int listCount =  -1; int currentCount = 0; int counterSelected = 0;
    int listTypeCount = -1;
    String multiMigrationDate = "";
    String multiMigrationTime = "";
    int id = 0;
    int countdownclose = 0;

    boolean multimigration = false;

    public void databaseTask() {

        processDatabase = new Service<>() {
             @Override
             protected Task<Void> createTask() {
                 return new Task<>() {

                     @Override
                     protected Void call() {
                         if (multimigration) {
                             listCount++;
                             path = listPath.get(listCount);
                             currentDatabase = listCurrentDatabase.get(listCount);
                         }

                         try {
                             reader = new DBFReader(new FileInputStream(path));
                         } catch (FileNotFoundException e) {
                             e.printStackTrace();
                             layoutYesNo.setHeading(new Label("Error"));
                             labelDialog.setText("File tidak ditemukan");
                             btnYes.setVisible(false);
                             btnNo.setText("Ok");
                             btnNo.setOnAction(actionEvent -> {
                                 dialogYesNo.close();
                             });
                             dialogYesNo.setOnDialogClosed((JFXDialogEvent event1) ->{
                             });
                             dialogYesNo.show();
                         }

                         try {
                             DatabaseMetaData md = con.getMetaData();
                             ResultSet rs = md.getTables(null, null, currentDatabase, null);
                             Statement stmt = con.createStatement();
                             con.setAutoCommit(false);
                             int numberOfFields = reader.getFieldCount();
                             int lastField = numberOfFields - 1;
                             String questionMark = ""; String fieldName = "";


                             if (!rs.next()) {
                                 sql = "CREATE TABLE " + currentDatabase.toLowerCase() + "([col_id] INTEGER, ";

                                 for ( int i = 0; i < numberOfFields; i++ ) {
                                     DBFField field = reader.getField(i);
                                     String currentField = field.getName();
                                     DBFDataType dataType = field.getType();
                                     System.out.println(dataType);

                                     switch (dataType) {
                                         default -> {
                                             if (i != lastField) {
                                                 sql = sql + "[" + currentField + "] VARCHAR(255), ";
                                             } else {
                                                 sql = sql + "[" + currentField + "] VARCHAR(255))";
                                             }
                                         }

                                         case NUMERIC -> {
                                             if (i != lastField) {
                                                 sql = sql + "[" + currentField + "] DECIMAL(18,2), ";
                                             } else {
                                                 sql = sql + "[" + currentField + "] DECIMAL(18,2))";
                                             }
                                         }

                                         case DATE -> {
                                             if (i != lastField) {
                                                 sql = sql + "[" + currentField + "] DATE, ";
                                             } else {
                                                 sql = sql + "[" + currentField + "] DATE)";
                                             }
                                         }

                                         case LOGICAL -> {
                                             if (i != lastField) {
                                                 sql = sql + "[" + currentField + "] BIT, ";
                                             } else {
                                                 sql = sql + "[" + currentField + "] BIT)";
                                             }
                                         }

                                     }
                                 }

                                 stmt.executeUpdate(sql);
                                 con.commit();
                             } else {
                                 try {
                                     sql = "DELETE FROM " + currentDatabase;
                                     pst = con.prepareStatement(sql);
                                     pst.executeUpdate();
                                     con.commit();
                                 } catch (SQLException e) {
                                     logger.error(e);
                                     e.printStackTrace();
                                 }
                             }

                             for (int i = 0; i < numberOfFields; i++) {
                                 DBFField field = reader.getField(i);
                                 String currentField = field.getName();
                                 if(i == lastField){
                                     questionMark = questionMark + "?";
                                     fieldName = fieldName + "[" + currentField + "]";
                                 } else {
                                     questionMark = questionMark + "?,";
                                     fieldName = fieldName + "[" +currentField+"],";
                                 }
                             }

                             if (reader.getFieldCount() == 0){
                                 if (multimigration) {
                                     updateProgress(100,100);
                                 } else {
                                     updateProgress(100,100);
                                     updateMessage("Tidak ada data.");
                                 }

                             } else {
                                 id =0;
                                 sql = "INSERT INTO " + currentDatabase + "([col_id]," + fieldName + ") VALUES (?," + questionMark + ")";

                                 try {
                                     reader = new DBFReader(new FileInputStream(path));
                                     Class.forName("net.sourceforge.jtds.jdbc.Driver");
                                     PreparedStatement statement = con.prepareStatement(sql);
                                     con.setAutoCommit(false);
                                     while ((rowObjects = reader.nextRecord()) != null) {
                                         int row = 2; listTypeCount = 0; id++;

                                         statement.setInt(1, id);
                                         for (int index = 0; index < numberOfFields; index++) {
                                             String value = String.valueOf(rowObjects[index]);
                                             DBFField field = reader.getField(index);
                                             DBFDataType dataType = field.getType();

                                             switch (dataType) {

                                                 default -> {
                                                     if (!value.equals("null")) {
                                                         statement.setString(row++, value);
                                                     } else {
                                                         statement.setString(row++, null);
                                                     }
                                                 }

                                                 case NUMERIC -> {
                                                     if (!value.equals("null")) {
                                                         statement.setBigDecimal(row++, (BigDecimal) rowObjects[index]);
                                                     } else {
                                                         statement.setBigDecimal(row++, BigDecimal.valueOf(0));
                                                     }
                                                 }

                                                 case DATE -> {
                                                     if (!value.equals("null")){
                                                         DateTimeFormatter f = DateTimeFormatter.ofPattern( "E MMM dd HH:mm:ss z uuuu" )
                                                                 .withLocale( Locale.US );
                                                         if (compareFormat(value, f)) {
                                                             ZonedDateTime zdt = ZonedDateTime.parse( value, f );
                                                             LocalDate ld = zdt.toLocalDate();
                                                             DateTimeFormatter fLocalDate = DateTimeFormatter.ofPattern( "uuuu-MM-dd" );
                                                             String output = ld.format( fLocalDate) ;
                                                             String check4 = output.substring(0, 4);

                                                             if (Integer.parseInt(check4) >= 1753) {
                                                                 statement.setDate(row++, java.sql.Date.valueOf(output));
                                                             } else {
                                                                 statement.setDate(row++, java.sql.Date.valueOf("1753-01-01"));
                                                             }
                                                         } else {
                                                             statement.setDate(row++, null); //pattern dd/MM/yyyy
                                                         }
                                                     } else {
                                                         statement.setDate(row++, null);
                                                     }
                                                 }

                                                 case LOGICAL -> {
                                                     if (!value.equals("null")) {
                                                         if (value.equals("true")) {
                                                             statement.setBoolean(row++, true);
                                                         } else {
                                                             statement.setBoolean(row++, false);
                                                         }
                                                     } else {
                                                         statement.setBoolean(row++, false);
                                                     }
                                                 }
                                             }
                                         }

                                         statement.executeUpdate();
                                         con.commit();
                                         updateProgress(id,reader.getRecordCount());
                                         updateMessage("Input ke database : "+id+ " dari "+reader.getRecordCount());
                                     }
                                     updateProgress(100, 100);
                                 } catch (SQLException | ClassNotFoundException e) {
                                     logger.error(e);
                                     e.printStackTrace();
                                 }
                             }
                         } catch (SQLException | FileNotFoundException throwables) {
                             logger.error(throwables);
                             throwables.printStackTrace();
                         } finally {
                             DBFUtils.close(reader);
                         }
                         return null;
                     }
                 };
             }
         };


        processDatabase.setOnSucceeded( e -> {
            labelProsesMigrasi.textProperty().unbind();
            progressMigrasiSelected.progressProperty().unbind();

            String todayDate = LocalDateTime.now().format(mindateFormatter);
            String todayTime = LocalDateTime.now().format(dottimeFormatter);

            if (migrationTimeline.getStatus().equals(Animation.Status.STOPPED)) {
                migrationTimeline.play();
            }

            if (multimigration) {
                if (listCount == counterSelected - 1) {
                    DBFUtils.close(reader);
                    countMultiInt = 10;
                    multiMigrateTimeline.playFromStart();
                    insertHistoryMigration(multiMigrationDate,multiMigrationTime,currentDatabase,String.valueOf(id),"Success");
                    updateDbInfo(multiMigrationDate, String.valueOf(id), currentDatabase, multiMigrationTime, "Success");
                    currentCount++;
                    labelProsesMigrasi.setText("Migration Completed");
                    labelTotalSelected.setText("Migration done : " + currentCount +" from "+counterSelected + " table\n ");
                    btnCancelSelected.setDisable(true);
                    btnCancelSelected.setText("Ok");
                    listCount =  -1; currentCount = 0; counterSelected = 0;
                    listCurrentDatabase.clear(); listPath.clear();
                    databaseFoxPro.clear();
                    readSettings();
                    setTableDashboard();
                    setTableTaskMigration(todayDate);
                } else {
                    currentCount++;
                    insertHistoryMigration(multiMigrationDate,multiMigrationTime,currentDatabase,String.valueOf(id),"Success");
                    updateDbInfo(multiMigrationDate, String.valueOf(id), currentDatabase, multiMigrationTime, "Success");
                    labelTotalSelected.setText("Migration done  : " + currentCount +" from "+ counterSelected +
                            "\nCurrent database : " +  listCurrentDatabase.get(currentCount) + "\n ");
                    labelProsesMigrasi.textProperty().bind(processDatabase.messageProperty());
                    progressMigrasiSelected.progressProperty().bind(processDatabase.progressProperty());
                    processDatabase.reset(); processDatabase.restart();
                    setTableTaskMigration(todayDate);
                }
            } else {
                insertHistoryMigration(todayDate,todayTime,currentDatabase,String.valueOf(id),"Success");
                updateDbInfo(todayDate, String.valueOf(id), currentDatabase, todayTime, "Success");

                countSingleInt = 10;
                singleMigrateTimeline.playFromStart();
                db.setMigrasiterakhir(todayDate + " - " + todayTime);
                String dbname =  String.valueOf(id);
                if (dbname.equals("0")) {
                    dbname = "No Data";
                    labelProses.textProperty().unbind();
                    labelProses.setText("No data");
                }
                db.setDataterakhir(dbname);

                btnCancelProgress.setDisable(true);
                btnCancelProgress.setText("Ok");
                setTableTaskMigration(todayDate);
            }
        });

        processDatabase.setOnCancelled( e -> {
            DBFUtils.close(reader);

            String todayDate = LocalDateTime.now().format(mindateFormatter);
            String todayTime = LocalDateTime.now().format(dottimeFormatter);

            if(multimigration){
                multiMigrateTimeline.stop();
                dialogMigrateAll.close();
                insertHistoryMigration(multiMigrationDate,multiMigrationTime,currentDatabase,String.valueOf(id),"Cancelled");
                updateDbInfo(multiMigrationDate, String.valueOf(id), currentDatabase, multiMigrationTime, "Cancelled");
            } else {
                singleMigrateTimeline.stop();
                dialogProgressBar.close();
                insertHistoryMigration(todayDate,todayTime,currentDatabase,String.valueOf(id),"Cancelled");
                updateDbInfo(todayDate, String.valueOf(id) + "(NC)", currentDatabase, todayTime, "Cancelled");
            }

            db.setMigrasiterakhir(todayDate + " - " + todayTime);
            String dbname =  String.valueOf(id);
            if (dbname.equals("0")) {
                dbname = "No Data";
            }
            db.setDataterakhir(dbname + "(NC)");

            labelProses.textProperty().unbind();
            progressDialog.progressProperty().unbind();
            activity = " Migrasi "+currentDatabase+" Berhasil";
            listCount =  -1; currentCount = 0; counterSelected = 0;
            listCurrentDatabase.clear(); listPath.clear();
            setTableTaskMigration(todayDate);
        });
    }

    public static boolean compareFormat(String inputValue, DateTimeFormatter format) {
        try {
            format.parse(inputValue);
            return true;
        } catch (DateTimeParseException dtpe) {
            return false;
        }
    }

    private void updateDbInfo(String date, String lastdata, String currentFile, String time, String status) {
        String querry = "UPDATE dbhistory set date = ?,ldata = ?,time = ?,status = ? where dbname = ?";
        try {
            Connection sqliteConnection = SQLiteConnection.Connector();
            PreparedStatement statement = sqliteConnection.prepareStatement(querry);
            statement.setString(1, date);
            statement.setString(2, lastdata);
            statement.setString(3, time);
            statement.setString(4, status);
            statement.setString(5, currentFile);
            statement.executeUpdate();
            sqliteConnection.close();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }

    }

    int countSelected = 0;
    JFXDialog addBookmarks;

    @FXML
    private void showBookmarks() {
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sample/views/dialogpane.fxml")));
            JFXButton saveBtn = (JFXButton) parent.lookup("#saveId");
            saveBtn.setOnAction( actionEvent -> {
                System.out.println("saved");
            });

            JFXButton closeButton = (JFXButton) parent.lookup("#closeId");
            closeButton.setOnAction( actionEvent -> {
                loadComboboxBookmarks();
                addBookmarks.close();
            });

            String styles =
                    "-fx-background-color: #2F333D;" +
                            " -fx-opacity: 1;" ;
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialogLayout.setMaxHeight(600);
            dialogLayout.setStyle(styles);

            addBookmarks = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
            addBookmarks.setOverlayClose(false);
            addBookmarks.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actionGetInfo() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm.ss");
        String todayDate = LocalDateTime.now().format(dateFormatter);
        String todayTime = LocalDateTime.now().format(timeFormatter);
        for(DatabaseFoxpro bean : databaseFoxPro) {
            if(bean.getSelect().isSelected()) {
                countSelected++;
                try{
                    FileWriter fstream = new FileWriter("typelist/DataType "+todayDate+" - " +todayTime+ ".txt",true);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write("\nTable Name : " + bean.getEkstensi());

                    currentDatabase = bean.getNamadatabase();
                    path = location + "\\"+bean.getEkstensi();

                    DBFReader reader = null;
                    try {
                        reader = new DBFReader(new FileInputStream(path));
                        int numberOfFields = reader.getFieldCount();

                        for (int i = 0; i < numberOfFields; i++) {
                            DBFField field = reader.getField(i);
                            out.write("\n|"+field.getName() + "|" + field.getType());
                        }

                        out.write(" \n");
                        out.close();

                    } catch (DBFException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        DBFUtils.close(reader);
                    }
                }catch (Exception e){//Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        if (countSelected == 0) {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.GENERIC, "Information", "No rows selected. Please select at least 1 row to get the table field datatype.");
            dialog.setAlwaysOnTop(true);
            dialog.show();
        }

        countSelected = 0;
    }

    public void monitorDatabase() {
        final Path path = FileSystems.getDefault().getPath(location+"\\");
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    final Path changed = (Path) event.context();
                    System.out.println(changed);
                    if (changed.endsWith("membermlm.phn")) {
                        System.out.println("MEMBERMLM changed!"); // reset service
                        textAreaLog.appendText(labelDate.getText()+" : Data Member MLM Telah Berubah"+"\n");
                        textAreaLog.appendText(labelDate.getText()+" : Proses Migrasi Member MLM Dimulai..."+"\n");
                        database1Status = "Modified";
                    } else if (changed.endsWith("BAYARP.PHN")) {
                        System.out.println("BAYARP changed!"); //reset service
                        textAreaLog.appendText(labelDate.getText()+" : Data BAYARP Telah Berubah"+"\n");
                        textAreaLog.appendText(labelDate.getText()+" : Proses Migrasi BAYARP Dimulai..."+"\n");
//                        database2Status = "Modified";
                    } else if (changed.endsWith("DBAYARP.PHN")) {
                        System.out.println("DBAYARP changed!"); //reset service
                        textAreaLog.appendText(labelDate.getText()+" : Data DBAYARP Telah Berubah"+"\n");
                        textAreaLog.appendText(labelDate.getText()+" : Proses Migrasi DBAYARP Dimulai..."+"\n");
//                        database3Status = "Modified";
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    System.out.println("Key has been unregisterede");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
