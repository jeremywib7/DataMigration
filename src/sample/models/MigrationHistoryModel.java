package sample.models;

public class MigrationHistoryModel {
    private String tanggal;
    private String dbname;
    private String jam;
    private String status;

    public MigrationHistoryModel(String tanggal, String dbname, String jam, String status) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.status = status;
        this.dbname = dbname;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }
}
