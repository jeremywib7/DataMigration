package sample.models;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.SimpleStringProperty;

public class ListBookmarks {

    private final String bookmarksname;
    private final String selected;

    public ListBookmarks(String bookmarksname, String selected) {
        this.bookmarksname = bookmarksname;
        this.selected = selected;
    }

    public String getBookmarksname() {
        return bookmarksname;
    }

    public String getSelected() {
        return selected;
    }
}
