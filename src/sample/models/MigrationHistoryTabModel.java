package sample.models;

public class MigrationHistoryTabModel {
    private String tanggal;
    private String dbname;
    private String jam;
    private String total;
    private String status;

    public MigrationHistoryTabModel(String tanggal, String jam, String dbname, String total, String status) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.status = status;
        this.total = total;
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
