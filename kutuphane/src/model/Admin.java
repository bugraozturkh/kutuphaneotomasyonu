package model;

public class Admin {
    private int adminId;
    private String adminKullaniciAdi;
    private String adminSifre;

    // Getter ve Setter metodları
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminKullaniciAdi() {
        return adminKullaniciAdi;
    }

    public void setAdminKullaniciAdi(String adminKullaniciAdi) {
        this.adminKullaniciAdi = adminKullaniciAdi;
    }

    public String getAdminSifre() {
        return adminSifre;
    }

    public void setAdminSifre(String adminSifre) {
        this.adminSifre = adminSifre;
    }
}
