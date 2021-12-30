package sample.models;

public class LoginHistoryModel {

    private String user;
    private String waktu;
    private String waktuout;
    private String tanggal;

    public LoginHistoryModel(String user, String waktu, String waktuout) {
        this.user = user;
        this.waktu = waktu;
        this.waktuout = waktuout;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getWaktuout() {
        return waktuout;
    }

    public void setWaktuout(String waktuout) {
        this.waktuout = waktuout;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
