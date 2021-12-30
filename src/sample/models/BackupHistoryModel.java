package sample.models;

public class BackupHistoryModel {
    private String tanggal;
    private String jam;
    private String folder;
    private String status;

    public BackupHistoryModel(String tanggal, String jam, String folder, String status) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.folder = folder;
        this.status = status;
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
