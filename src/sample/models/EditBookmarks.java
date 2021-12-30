package sample.models;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.SimpleStringProperty;

public class EditBookmarks {

    private JFXCheckBox select;
    private final SimpleStringProperty id;

    public EditBookmarks(String id) {
        this.select = new JFXCheckBox();
        this.id = new SimpleStringProperty(id);
    }

    public JFXCheckBox getSelect() {
        return select;
    }

    public void setSelect(JFXCheckBox select) {
        this.select = select;
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }
}
