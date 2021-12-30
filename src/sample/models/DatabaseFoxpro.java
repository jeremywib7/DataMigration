package sample.models;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.SimpleStringProperty;

public class DatabaseFoxpro {
    private JFXCheckBox select;
    private final SimpleStringProperty id;
    private final SimpleStringProperty namadatabase;
    private final SimpleStringProperty migrasiterakhir;
    private final SimpleStringProperty dataterakhir;
    private final SimpleStringProperty ekstensi;


    public DatabaseFoxpro(String id, String namadatabase, String migrasiterakhir, String dataterakhir, String ekstensi) {
        this.select = new JFXCheckBox();
        this.id = new SimpleStringProperty(id);
        this.namadatabase = new SimpleStringProperty(namadatabase);
        this.migrasiterakhir = new SimpleStringProperty(migrasiterakhir);
        this.dataterakhir = new SimpleStringProperty(dataterakhir);
        this.ekstensi = new SimpleStringProperty(ekstensi);
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

    public String getNamadatabase() {
        return namadatabase.get();
    }

    public SimpleStringProperty namadatabaseProperty() {
        return namadatabase;
    }

    public void setNamadatabase(String namadatabase) {
        this.namadatabase.set(namadatabase);
    }

    public String getMigrasiterakhir() {
        return migrasiterakhir.get();
    }

    public SimpleStringProperty migrasiterakhirProperty() {
        return migrasiterakhir;
    }

    public void setMigrasiterakhir(String migrasiterakhir) {
        this.migrasiterakhir.set(migrasiterakhir);
    }

    public String getDataterakhir() {
        return dataterakhir.get();
    }

    public SimpleStringProperty dataterakhirProperty() {
        return dataterakhir;
    }

    public void setDataterakhir(String dataterakhir) {
        this.dataterakhir.set(dataterakhir);
    }

    public String getEkstensi() {
        return ekstensi.get();
    }

    public SimpleStringProperty ekstensiProperty() {
        return ekstensi;
    }

    public void setEkstensi(String ekstensi) {
        this.ekstensi.set(ekstensi);
    }
}
