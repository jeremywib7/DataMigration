package sample.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kordamp.ikonli.javafx.FontIcon;
import sample.models.AddBookmarks;
import sample.models.DatabaseFoxpro;
import sample.models.EditBookmarks;
import sample.models.ListBookmarks;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class BookmarkController implements Initializable {

    @FXML private TableView<AddBookmarks> tableAddBookmarks;
    @FXML private TableColumn<AddBookmarks, BooleanProperty> cb1;
    @FXML private TableColumn<AddBookmarks, SimpleStringProperty> cb2;

    @FXML private TableView<EditBookmarks> tableEditBookmarks;
    @FXML private TableColumn<EditBookmarks, BooleanProperty> cb1Edit;
    @FXML private TableColumn<EditBookmarks, SimpleStringProperty> cb2Edit;

    @FXML private TableView<ListBookmarks> tableListBookmarks;
    @FXML private TableColumn<ListBookmarks, String> cbList1;
    @FXML private TableColumn<ListBookmarks, String> cbList2;

    @FXML
    private JFXComboBox cbViewSelected, cbViewSelectedEdit;
    @FXML
    private JFXButton saveId, backId, backIdEdit, resetId, editId;
    @FXML
    private JFXTextField bookmarksName, bookmarksNameEdit;
    @FXML
    private AnchorPane anchorAdd, anchorEdit, anchorList;
    @FXML
    private Label labelTitle;

    public int countSelected = 0;
    static final Logger logger = Logger.getLogger(BookmarkController.class.getName());
    JFXPopup popup;

    Connection con;

    ListBookmarks lb;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PropertyConfigurator.configure(getClass().getResource("log4j.properties"));
        readSettings();
        dialog = new MFXStageDialog(DialogType.GENERIC, "Information", "Please select at least 1 rows to save bookmarks");
        dialog.setAllowDrag(false);
        dialog.setAlwaysOnTop(true);
        existsdialog = new MFXStageDialog(DialogType.GENERIC, "Information", "Bookmarks name already exists");
        existsdialog.setAllowDrag(false);
        existsdialog.setAlwaysOnTop(true);
    }

    String location = "";
    String selectedstring = "";
    RequiredFieldValidator validator;
    FontIcon warnIcon;
    MFXStageDialog dialog;
    MFXStageDialog existsdialog;
    private Text text;


    private final ObservableList<ListBookmarks> listBookmarks = FXCollections.observableArrayList();
    private final ObservableList<AddBookmarks> addBookmarks = FXCollections.observableArrayList();
    private final ObservableList<EditBookmarks> editBookmarks = FXCollections.observableArrayList();

    public void handleClicks(MouseEvent event) {
        if (event.getSource() == cbViewSelected) {
            countSelected = 0;
            cbViewSelected.getItems().removeAll(cbViewSelected.getItems());
            cbViewSelected.getItems().clear();
            cbViewSelected.setValue(null);


            for (AddBookmarks bean : addBookmarks) {
                if (bean.getSelect().isSelected()) {
                    countSelected++;
                }
            }

            cbViewSelected.getItems().add("Total selected : " + countSelected);

            for (AddBookmarks bean : addBookmarks) {
                if (bean.getSelect().isSelected()) {
                    cbViewSelected.getItems().add(bean.getId());
                    countSelected++;
                }
            }

            cbViewSelected.hide(); //before you set new visibleRowCount value
            cbViewSelected.setVisibleRowCount(10); // set new visibleRowCount value
            cbViewSelected.show(); //after you set new visibleRowCount value

        }  else if (event.getSource() == cbViewSelectedEdit) {
            countSelected = 0;
            cbViewSelectedEdit.getItems().clear();

            for (EditBookmarks bean : editBookmarks) {
                if (bean.getSelect().isSelected()) {
                    countSelected++;
                }
            }

            cbViewSelectedEdit.getItems().add("Total selected : " + countSelected);

            for (EditBookmarks bean : editBookmarks) {
                if (bean.getSelect().isSelected()) {
                    cbViewSelectedEdit.getItems().add(bean.getId());
                    countSelected++;
                }
            }

            cbViewSelectedEdit.hide(); //before you set new visibleRowCount value
            cbViewSelectedEdit.setVisibleRowCount(10); // set new visibleRowCount value
            cbViewSelectedEdit.show(); //after you set new visibleRowCount value

        }else if (event.getSource() == saveId) {
            refreshTableListBookmarks();

            validator = new RequiredFieldValidator();
            validator.setMessage("Name Required");
            warnIcon = new FontIcon();
            warnIcon.setIconLiteral("fa-warning");
            validator.setIcon(warnIcon);
            bookmarksName.getValidators().add(validator);

            int selected = 0;
            if (bookmarksName.getText().equals("") || bookmarksName.getText().isEmpty()) {
                bookmarksName.validate();
            } else {
                bookmarksName.resetValidation();

                for (AddBookmarks bean : addBookmarks) {
                    if (bean.getSelect().isSelected()) {
                        selected++;
                        if (selected == 1) {
                            selectedstring = bean.getId();
                        } else {
                            selectedstring = selectedstring + "," + bean.getId();
                        }
                    }
                }

                if (selected == 0) {
                    dialog.show();
                } else {
                    // TODO set first word to capitalize and the rest lowercase
                    String output = bookmarksName.getText().substring(0, 1).toUpperCase() + bookmarksName.getText().substring(1).toLowerCase(Locale.ROOT);

                    if (checkBookmarksNameExists(output)) {
                        // TODO bookmarks name already exists
                        existsdialog.show();
                    } else {
                        // TODO data valid, insert into sqlite
                        try {
                            con = SQLiteConnection.Connector();
                            PreparedStatement psts = con.prepareStatement("INSERT INTO bookmarksdb " +
                                    "(selected, bookmarksname)" + "VALUES (?,?) ");
                            psts.setString(1, selectedstring);
                            psts.setString(2, output);
                            psts.executeUpdate();
                            reloadListBookmarks();
                            con.close();

                        } catch (SQLException throwables) {
                            logger.error(throwables);
                            throwables.printStackTrace();
                        }

                        // TODO clear add bookmarks page and reset table
                        bookmarksName.setText("");
                        bookmarksName.resetValidation();
                        for (AddBookmarks bean : addBookmarks) {
                            bean.getSelect().setSelected(false);
                        }
                        tableEditBookmarks.scrollTo(0);
                        anchorList.toFront();
                    }
                }
            }
        } else if (event.getSource() == editId) {
            ListBookmarks p = tableListBookmarks.getSelectionModel().getSelectedItem();
            String bookmarksname = p.getBookmarksname();

            refreshTableListBookmarks();

            validator = new RequiredFieldValidator();
            validator.setMessage("Name Required");
            warnIcon = new FontIcon();
            warnIcon.setIconLiteral("fa-warning");
            validator.setIcon(warnIcon);
            bookmarksNameEdit.getValidators().add(validator);

            int selected = 0;
            if (bookmarksNameEdit.getText().equals("") || bookmarksNameEdit.getText().isEmpty()) {
                bookmarksNameEdit.validate();
            } else {
                bookmarksNameEdit.resetValidation();

                for (EditBookmarks bean : editBookmarks) {
                    if (bean.getSelect().isSelected()) {
                        selected++;
                        if (selected == 1) {
                            selectedstring = bean.getId();
                        } else {
                            selectedstring = selectedstring + "," + bean.getId();
                        }
                    }
                }

                if (selected == 0) {
                    dialog.show();
                } else {
                    // TODO set first word to capitalize and the rest lowercase
                    String output = bookmarksNameEdit.getText().substring(0, 1).toUpperCase() + bookmarksNameEdit.getText().substring(1).toLowerCase(Locale.ROOT);

                    if (checkBookmarksNameExists(output)) {
                        // TODO bookmarks name already exists
                        existsdialog.show();
                    } else {
                        // TODO data valid, insert into sqlite
                        String querry = "UPDATE bookmarksdb set bookmarksname = ?,selected = ? where bookmarksname = ?";
                        try {
                            con = SQLiteConnection.Connector();
                            PreparedStatement statement = con.prepareStatement(querry);
                            statement.setString(1, output);
                            statement.setString(2, selectedstring);
                            statement.setString(3, bookmarksname);
                            statement.executeUpdate();
                            reloadListBookmarks();
                            con.close();
                        } catch (SQLException e) {
                            logger.error(e);
                            e.printStackTrace();
                        }

                        // TODO clear add bookmarks page and reset table
                        bookmarksName.setText("");
                        bookmarksName.resetValidation();
                        for (AddBookmarks bean : addBookmarks) {
                            bean.getSelect().setSelected(false);
                        }
                        tableEditBookmarks.scrollTo(0);
                        anchorList.toFront();
                    }
                }
            }
        } else if (event.getSource() == backId) {
            bookmarksName.setText("");
            for (AddBookmarks bean : addBookmarks) {
                bean.getSelect().setSelected(false);
            }
            bookmarksName.resetValidation();
            anchorList.toFront();
        } else if (event.getSource() == backIdEdit) {
            bookmarksNameEdit.resetValidation();
            anchorList.toFront();
        } else if (event.getSource() == resetId) {
            bookmarksName.setText("");
            for (AddBookmarks bean : addBookmarks) {
                bean.getSelect().setSelected(false);
            }
            bookmarksName.requestFocus();
            //clear combobox
            cbViewSelected.getItems().removeAll(cbViewSelected.getItems());
            cbViewSelected.getItems().clear();
            cbViewSelected.setValue(null);
            bookmarksName.resetValidation();
        }
    }

    @FXML
    private void addBookmarks() {
        checkString = "";
        labelTitle.setText("Add Bookmarks");
        bookmarksName.requestFocus();
        anchorAdd.toFront();
    }

    @FXML
    private void clearSelection(ActionEvent event) {
        cbViewSelected.getItems().removeAll(cbViewSelected.getItems());
        cbViewSelected.getItems().clear();
        cbViewSelectedEdit.getSelectionModel().clearSelection();
    }

    public void readSettings() {
        //set event handler for popup edit and delete
        EventHandler<MouseEvent> onClick = this::handleTableRowMouseClick;
        tableListBookmarks.setRowFactory(param -> {
            TableRow<ListBookmarks> row = new TableRow<>();
            row.setOnMouseClicked(onClick);
            return row;
        });

        tableListBookmarks.setPrefWidth(150);
        tableListBookmarks.setPrefWidth(150);

//      set table for adding bookmarks
        addBookmarks.clear();
        String sqlite = "SELECT foxprodb FROM settings";
        try {
            con = SQLiteConnection.Connector();
            PreparedStatement statement = con.prepareStatement(sqlite);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                location = rs.getString(1);

                File folder = new File(location);
                File[] listOfPhnFiles = folder.listFiles((dir, name) -> name.endsWith(".phn") || name.endsWith(".PHN"));

                for (int i = 0; i < listOfPhnFiles.length; i++) {
                    if (listOfPhnFiles[i].isFile()) {
                        String name = listOfPhnFiles[i].getName();
                        String sql = "SELECT * FROM dbhistory where dbname = ?";
                        try {
                            PreparedStatement prestmt = con.prepareStatement(sql);
                            if (name.indexOf(".") > 0)
                                name = name.substring(0, name.lastIndexOf("."));
                            prestmt.setString(1, name.toLowerCase());
                            {
                                ResultSet rst = prestmt.executeQuery();
                                if (rst.next()) {
                                    addBookmarks.add(new AddBookmarks(name.toLowerCase(Locale.ROOT)));
                                    editBookmarks.add(new EditBookmarks(name.toLowerCase(Locale.ROOT)));
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
            con.close();
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }

//      set value for both table
        cb1.setCellValueFactory(new PropertyValueFactory<>("select"));
        cb2.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableAddBookmarks.setEditable(true);
        tableAddBookmarks.setItems(addBookmarks);

        cb1Edit.setCellValueFactory(new PropertyValueFactory<>("select"));
        cb2Edit.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableEditBookmarks.setEditable(true);
        tableEditBookmarks.setItems(editBookmarks);

        cbList1.setCellValueFactory(new PropertyValueFactory<>("bookmarksname"));
        cbList2.setCellValueFactory(new PropertyValueFactory<>("selected"));
        cbList2.setCellFactory(column -> new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    text = new Text(item);
                    text.wrappingWidthProperty().bind(cbList2.widthProperty());
                    text.getStyleClass().add("coltext");
                    setGraphic(text);
                }
            }
        });
        tableListBookmarks.setEditable(true);
        tableListBookmarks.setItems(listBookmarks);

        //load list bookmarks for edit or delete
        reloadListBookmarks();

        //setting popup
        Label edit = new Label("Edit");
        edit.setPadding(new Insets(10));
        edit.setOnMouseClicked(actionEvent -> {
            //get selected item value for querry edit in sqlite
            ListBookmarks p = tableListBookmarks.getSelectionModel().getSelectedItem();
            String bookmarksname = p.getBookmarksname();
            String selected = p.getSelected();
            checkString =  bookmarksname;
            bookmarksNameEdit.setText(bookmarksname);

//          unselect all
            for(EditBookmarks eb : editBookmarks) {
                if (eb.getSelect().isSelected()) {
                    eb.getSelect().setSelected(false);
                } else {
                    eb.getSelect().setSelected(false);
                }
            }

            //generate checkbox selected
            String[] row = selected.split(",");
            for(EditBookmarks eb : editBookmarks) {
                for(int i=0; i<row.length; i++) {
                    if(row[i].equals(eb.getId())) {
                        eb.getSelect().setSelected(true);
                    }
                }
            }

            bookmarksNameEdit.requestFocus();
            tableEditBookmarks.scrollTo(0);
            popup.hide();
            anchorEdit.toFront();
        });

        Label delete = new Label("Delete");
        delete.setOnMouseClicked(actionEvent -> {
            //get selected item value for querry in sqlite
            ListBookmarks p = tableListBookmarks.getSelectionModel().getSelectedItem();
            String bookmarksname = p.getBookmarksname();
            String[] words = bookmarksname.split("\\|");
            String bookmarksstring = words[0];

            //get selected index for removal in sqlite database
            int selectedID = tableListBookmarks.getSelectionModel().getSelectedIndex();

            try {
                String sql = "DELETE FROM bookmarksdb where bookmarksname=?";
                con = SQLiteConnection.Connector();
                PreparedStatement statement = con.prepareStatement(sql);
                statement.setString(1, bookmarksstring);
                statement.executeUpdate();
                con.close();
                tableListBookmarks.getItems().remove(selectedID);
                refreshTableListBookmarks();
                popup.hide();
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
            }
        });
        delete.setPadding(new Insets(10));
        VBox vbox = new VBox(edit, delete);
        popup = new JFXPopup(vbox);
    }

    private void handleTableRowMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            @SuppressWarnings("unchecked")
            TableRow<Object> row = (TableRow<Object>) event.getSource();
            if (!row.isEmpty() && row.getItem() != null) {
                popup.show(row, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
                event.consume();
            }
        }
    }

    private void reloadListBookmarks() {
        //load list bookmarks for edit or delete
        listBookmarks.clear();

        String querryBookmarks = "SELECT * FROM bookmarksdb";
        try {
            con = SQLiteConnection.Connector();
            PreparedStatement statement = con.prepareStatement(querryBookmarks);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                listBookmarks.add(new ListBookmarks(rs.getString(2), rs.getString(1)));
            }
            con.close();
        } catch (SQLException throwables) {
            logger.error(throwables);
            throwables.printStackTrace();
        }
    }
    String checkString = "";

    private boolean checkBookmarksNameExists(String dbName) {

        String sql = "SELECT count(1) FROM bookmarksdb WHERE bookmarksname =?";

        try {
            con = SQLiteConnection.Connector();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) >= 1) {
                    con.close();
                    if (dbName.equals(checkString)) {
                        return false;
                    }
                    return true;
                } else {
                    con.close();
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return false;
    }

    private void refreshTableListBookmarks() {
        //reupdate the tableview again
        cbList2.setCellFactory(column -> new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    text = new Text(item);
                    text.wrappingWidthProperty().bind(cbList2.widthProperty());
                    text.getStyleClass().add("coltext");
                    setGraphic(text);
                } else {
                    setGraphic(null);
                }
            }
        });
    }
}
