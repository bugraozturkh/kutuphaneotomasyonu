package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import util.DatabaseConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainMenu extends JFrame {

    // Kitap Paneli için Alanlar
    private JTextField tfKitapAdı, tfKitapYazarı, tfKitapYayınevi, tfKitapYayınYılı;
    private JTextField tfKitapAra; // Kitap arama için TextField
    private JButton btnEkle, btnSil, btnGüncelle, btnAra; // Ara butonunu ekliyoruz
    private JTable kitapListesi;
    private DefaultTableModel tableModel;
    JComboBox<String> comboKitapKategori;
    private int selectedKitapId = -1;  // Seçilen kitap ID'si

    // Öğrenci Paneli için Alanlar
    private JTextField tfOgrenciId, tfOgrenciAd, tfOgrenciTelefon, tfOgrenciKayitTarihi;
    private JTextField tfOgrenciAra; // Kitap arama için TextField
    private JTable ogrenciListesi;
    private DefaultTableModel ogrenciTableModel;
    private int selectedOgrenciId = -1;  // Seçilen öğrenci ID'si

    // Ödünç - İade Paneli için Alanlar
    private JTextField tfOduncOgrenciId, tfOduncKitapId;
    private JTable oduncListesi;
    private DefaultTableModel oduncTableModel;
    private int selectedOduncId = -1;

    public MainMenu() {
        getContentPane().setBackground(new Color(128, 128, 255));
        setBackground(new Color(255, 255, 255));
        setTitle("GELİŞİM KÜTÜPHANE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setForeground(new Color(0, 0, 0));
        tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 15));
        tabbedPane.setBackground(new Color(128, 128, 255));
        JPanel kitapPanel = createKitapPanel();
        tabbedPane.addTab("Kitap", kitapPanel);

        JPanel ogrenciPanel = createOgrenciPanel();
        tabbedPane.addTab("Öğrenci", ogrenciPanel);

        // 3. sekme başlığını değiştirme
        JPanel oduncPanel = createOduncPanel();
        tabbedPane.addTab("Ödünç - İade İşlemleri", oduncPanel);  // Başlık değiştirildi

        getContentPane().add(tabbedPane);
    }

    private JPanel createKitapPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Sol Panel: Kullanıcıdan kitap bilgilerini alacağız
        JPanel solPanel = new JPanel();
        solPanel.setBackground(new Color(128, 128, 255));
        solPanel.setLayout(new GridLayout(6, 2));

        JLabel label = new JLabel("Kitap Adı:");
        label.setFont(new Font("Tahoma", Font.BOLD, 19));
        solPanel.add(label);
        tfKitapAdı = new JTextField();
        tfKitapAdı.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfKitapAdı);

        JLabel label_1 = new JLabel("Kitap Yazarı:");
        label_1.setFont(new Font("Tahoma", Font.BOLD, 18));
        solPanel.add(label_1);
        tfKitapYazarı = new JTextField();
        tfKitapYazarı.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfKitapYazarı);

        JLabel label_2 = new JLabel("Kitap Yayınevi:");
        label_2.setFont(new Font("Tahoma", Font.BOLD, 18));
        solPanel.add(label_2);
        tfKitapYayınevi = new JTextField();
        tfKitapYayınevi.setFont(new Font("Tahoma", Font.BOLD, 15));
        tfKitapYayınevi.setBackground(new Color(255, 255, 255));
        solPanel.add(tfKitapYayınevi);

        JLabel label_3 = new JLabel("Kitap Yayın Yılı:");
        label_3.setFont(new Font("Tahoma", Font.BOLD, 18));
        solPanel.add(label_3);
        tfKitapYayınYılı = new JTextField();
        tfKitapYayınYılı.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfKitapYayınYılı);
        
        JLabel label_4 = new JLabel("Kitap Kategorisi:");
        label_4.setFont(new Font("Tahoma", Font.BOLD, 18));
        solPanel.add(label_4);

        comboKitapKategori = new JComboBox<>(new String[] {
            "Roman", "Edebiyat", "Bilim", "Tarih", "Felsefe", "Çocuk", "Diğer"
        });
        comboKitapKategori.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(comboKitapKategori);

        // Sağ Panel: Kitaplar listelenecek
        JPanel sagPanel = new JPanel();
        sagPanel.setBackground(new Color(128, 128, 255));
        sagPanel.setVisible(false);  // Başlangıçta gizli

        // JTable ve TableModel oluşturulması
        String[] columnNames = {"Kitap Id", "Kitap Adı", "Yazar", "Yayınevi", "Yayın Yılı", "Durum","Kategori"};
        tableModel = new DefaultTableModel(columnNames, 0);
        kitapListesi = new JTable(tableModel);
        kitapListesi.getSelectionModel().addListSelectionListener(e -> loadSelectedKitap());

        sagPanel.setLayout(null);
        JScrollPane scrollPane = new JScrollPane(kitapListesi);
        scrollPane.setBounds(0, 0, 479, 454);
        scrollPane.setToolTipText("");
        sagPanel.add(scrollPane);

        // Ekle, Sil, Güncelle Butonları
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(128, 128, 255));
        buttonPanel.setLayout(new FlowLayout());

        btnEkle = new JButton("Ekle");
        btnEkle.setBackground(new Color(255, 255, 255));
        btnEkle.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnEkle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addKitap();
            }
        });

        btnSil = new JButton("Sil");
        btnSil.setBackground(new Color(255, 255, 255));
        btnSil.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteKitap();
            }
        });

        btnGüncelle = new JButton("Güncelle");
        btnGüncelle.setBackground(new Color(255, 255, 255));
        btnGüncelle.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnGüncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateKitap();
            }
        });

        // Kitapları Listele Butonu
        JButton btnListele = new JButton("Kitapları Listele");
        btnListele.setBackground(new Color(255, 255, 255));
        btnListele.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnListele.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kitapları listele işlemi
                listKitaplar();
                // Sağ panelde kitapları listelemek için tablonun görünür olduğunu kontrol et
                sagPanel.setVisible(true);  // Sağ panelin görünür olması gerektiğini belirtiyoruz
            }
        });

        // Kitap Ara Alanı ve Butonu
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(128, 128, 255));
        searchPanel.setLayout(new FlowLayout());

        JLabel lblSearch = new JLabel("Kitap Ara:");
        lblSearch.setFont(new Font("Tahoma", Font.BOLD, 15));
        searchPanel.add(lblSearch);

        tfKitapAra = new JTextField(15);
        tfKitapAra.setFont(new Font("Tahoma", Font.BOLD, 15));
        searchPanel.add(tfKitapAra);

        btnAra = new JButton("Ara");
        btnAra.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnAra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchKitap();
            }
        });
        searchPanel.add(btnAra);

        buttonPanel.add(btnEkle);
        buttonPanel.add(btnSil);
        buttonPanel.add(btnGüncelle);
        buttonPanel.add(btnListele); // Kitapları Listele butonunu ekliyoruz

        panel.add(solPanel, BorderLayout.WEST);
        panel.add(sagPanel, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH); // Ara alanını üst panel olarak ekliyoruz
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Kitap arama fonksiyonu
    private void searchKitap() {
        String searchQuery = tfKitapAra.getText().trim();

        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen arama için bir kelime girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT kitapId, kitapAd, kitapYazar, kitapYayinevi, kitapYayinyili, durum " +
                           "FROM tableKitaplar WHERE kitapAd LIKE ? OR kitapYazar LIKE ? OR kitapYayinevi LIKE ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "%" + searchQuery + "%");
                stmt.setString(2, "%" + searchQuery + "%");
                stmt.setString(3, "%" + searchQuery + "%");

                ResultSet rs = stmt.executeQuery();

                // Tabloyu temizle
                tableModel.setRowCount(0);

                // Arama sonuçlarını ekle
                while (rs.next()) {
                    Object[] row = new Object[6]; // 5 normal sütun + 1 durum sütunu
                    row[0] = rs.getInt("kitapId");
                    row[1] = rs.getString("kitapAd");
                    row[2] = rs.getString("kitapYazar");
                    row[3] = rs.getString("kitapYayinevi");
                    row[4] = rs.getDate("kitapYayinyili");
                    row[5] = rs.getString("durum");  // Durum bilgisini alıyoruz
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Diğer fonksiyonlar (addKitap, listKitaplar, deleteKitap, updateKitap, loadSelectedKitap) aynen kaldı



 // Kitap ekleme işlemi veritabanına
    private void addKitap() {
    	
    	String kitapAdi = tfKitapAdı.getText();
        String kitapYazari = tfKitapYazarı.getText();
        String kitapYayınevi = tfKitapYayınevi.getText();
        String kitapYayinYili = tfKitapYayınYılı.getText();
        String kitapKategori = (String) comboKitapKategori.getSelectedItem(); // Kategori seçimini alıyoruz

        // Alanları kontrol et
        if (kitapAdi.isEmpty() || kitapYazari.isEmpty() || kitapYayınevi.isEmpty() || kitapYayinYili.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Yıl bilgisini al
        int yayinYili = 0;
        try {
            yayinYili = Integer.parseInt(kitapYayinYili);  // Yıl bilgisini sayıya dönüştür
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçerli bir yıl girin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Veritabanına bağlanma ve kitap ekleme
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO tableKitaplar (kitapAd, kitapYazar, kitapYayinevi, kitapYayinyili, durum, kitapKategori) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, kitapAdi);
                stmt.setString(2, kitapYazari);
                stmt.setString(3, kitapYayınevi);

                // Yıl bilgisini tarih formatında ayarlıyoruz.
                String tarihFormat = yayinYili + "-01-01"; // Yalnızca yılı ekliyoruz, gün ve ay '01' olarak alınıyor.
                java.sql.Date sqlDate = java.sql.Date.valueOf(tarihFormat);
                stmt.setDate(4, sqlDate); // Veritabanına ekliyoruz.

                stmt.setString(5, "Boş");  // Başlangıçta kitap durumu "Boş"
                stmt.setString(6, kitapKategori); // Kitap kategorisini ekliyoruz
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kitap başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Giriş alanlarını temizle
        tfKitapAdı.setText("");
        tfKitapYazarı.setText("");
        tfKitapYayınevi.setText("");
        tfKitapYayınYılı.setText("");

        // Kitapları yeniden listele
        listKitaplar();  // Bu fonksiyonu çağırarak listeyi güncellediğimizden emin olalım
    }

 // Veritabanından kitapları al ve tabloya ekle
    public void listKitaplar() {
    	tableModel.setRowCount(0);  // Bu satır, tabloyu tamamen temizler
    	try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tableKitaplar";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("kitapId");
                    String kitapAd = rs.getString("kitapAd");
                    String kitapYazar = rs.getString("kitapYazar");
                    String kitapYayinevi = rs.getString("kitapYayinevi");
                    String kitapYayinYili = rs.getString("kitapYayinYili");
                    String kitapDurum = rs.getString("durum");
                    String kitapKategori = rs.getString("kitapKategori"); // Kategori bilgisi
                    tableModel.addRow(new Object[] {id, kitapAd, kitapYazar, kitapYayinevi, kitapYayinYili, kitapDurum, kitapKategori});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteKitap() {
        int selectedRow = kitapListesi.getSelectedRow(); // JTable'dan seçili satır
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek kitabı seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // JTable'dan kitap ID'sini al
        int kitapId = (int) kitapListesi.getValueAt(selectedRow, 0); // 0, kitap ID'sinin bulunduğu sütun indeksi

        // Silme işlemini çağır
        deleteKitap(kitapId);
    }

    // Kitap silme işlemi
    private void deleteKitap(int kitapId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Önce ödünç kayıtlarını sil
            String deleteOduncQuery = "DELETE FROM tableOduncler WHERE kitapId = ?";
            try (PreparedStatement oduncStmt = connection.prepareStatement(deleteOduncQuery)) {
                oduncStmt.setInt(1, kitapId);
                oduncStmt.executeUpdate();
            }

            // Ardından kitabı sil
            String deleteKitapQuery = "DELETE FROM tableKitaplar WHERE kitapId = ?";
            try (PreparedStatement kitapStmt = connection.prepareStatement(deleteKitapQuery)) {
                kitapStmt.setInt(1, kitapId);
                kitapStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Kitap başarıyla silindi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kitap silinirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadSelectedKitap() {
        int selectedRow = kitapListesi.getSelectedRow();
        if (selectedRow != -1) {
            selectedKitapId = (int) tableModel.getValueAt(selectedRow, 0);
            tfKitapAdı.setText((String) tableModel.getValueAt(selectedRow, 1));
            tfKitapYazarı.setText((String) tableModel.getValueAt(selectedRow, 2));
            tfKitapYayınevi.setText((String) tableModel.getValueAt(selectedRow, 3));
            tfKitapYayınYılı.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));

            // Kitap kategorisini alıp ComboBox'ta göster
            String kitapKategori = (String) tableModel.getValueAt(selectedRow, 6);  // Kategori sütunu
            comboKitapKategori.setSelectedItem(kitapKategori);  // ComboBox'ta seçili öğeyi ayarla
        }
    }


    private void updateKitap() {
        if (selectedKitapId == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String kitapAdi = tfKitapAdı.getText();
        String kitapYazari = tfKitapYazarı.getText();
        String kitapYayınevi = tfKitapYayınevi.getText();
        String kitapYayinYili = tfKitapYayınYılı.getText();
        String kitapKategori = (String) comboKitapKategori.getSelectedItem(); // Kategoriyi alıyoruz

        if (kitapAdi.isEmpty() || kitapYazari.isEmpty() || kitapYayınevi.isEmpty() || kitapYayinYili.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        // Veritabanına bağlanma ve kitap güncelleme
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE tableKitaplar SET kitapAd = ?, kitapYazar = ?, kitapYayinevi = ?, kitapYayinyili = ?, kitapKategori = ? WHERE kitapId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, kitapAdi);
                stmt.setString(2, kitapYazari);
                stmt.setString(3, kitapYayınevi);
                stmt.setString(4, kitapYayinYili);
               

                stmt.setString(5, kitapKategori);  // Kategori bilgisi
                stmt.setInt(6, selectedKitapId);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kitap başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kitap güncellenirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Kitapları yeniden listele
        listKitaplar();
        // Seçili kitap bilgisini temizle
        selectedKitapId = -1;
    }

    
    private JPanel createOgrenciPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(128, 128, 255));
        panel.setLayout(new BorderLayout());

        // Sol Panel: Kullanıcıdan öğrenci bilgilerini alacağız
        JPanel solPanel = new JPanel();
        solPanel.setBackground(new Color(128, 128, 255));
        solPanel.setLayout(new GridLayout(5, 2));  // 5 satır, 2 sütun (Öğrenci bilgileri alanı)

        JLabel label = new JLabel("Öğrenci Numarası:");
        label.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(label);  // 'Öğrenci ID' yerine 'Öğrenci Numarası'
        tfOgrenciId = new JTextField();
        tfOgrenciId.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfOgrenciId);

        JLabel label_1 = new JLabel("Öğrenci Adı:");
        label_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(label_1);
        tfOgrenciAd = new JTextField();
        tfOgrenciAd.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfOgrenciAd);

        JLabel label_2 = new JLabel("Telefon:");
        label_2.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(label_2);
        tfOgrenciTelefon = new JTextField();
        tfOgrenciTelefon.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfOgrenciTelefon);

        JLabel label_3 = new JLabel("Kayıt Tarihi:");
        label_3.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(label_3);
        tfOgrenciKayitTarihi = new JTextField();
        tfOgrenciKayitTarihi.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfOgrenciKayitTarihi);

        // Sağ Panel: Öğrenciler listelenecek
        JPanel sagPanel = new JPanel();
        sagPanel.setForeground(new Color(128, 128, 255));
        sagPanel.setBackground(new Color(128, 128, 255));
        sagPanel.setVisible(false);  // Başlangıçta gizli

        String[] columnNames = {"Öğrenci Numarası", "Adı", "Telefon", "Kayıt Tarihi"};
        ogrenciTableModel = new DefaultTableModel(columnNames, 0);
        
        ogrenciListesi = new JTable(ogrenciTableModel);
        ogrenciListesi.setForeground(new Color(0, 0, 0));
        ogrenciListesi.setBackground(new Color(255, 255, 255));
        ogrenciListesi.getSelectionModel().addListSelectionListener(e -> loadSelectedOgrenci());
        JScrollPane scrollPane = new JScrollPane(ogrenciListesi);
        scrollPane.setBounds(0, 37, 499, 454);

        // Öğrenci Ara Alanı ve Butonunu Üstte Ekle
        JPanel aramaPaneli = new JPanel();
        aramaPaneli.setBounds(0, 0, 499, 37);
        aramaPaneli.setBackground(new Color(128, 128, 255));
        aramaPaneli.setLayout(null);

        JLabel label_4 = new JLabel("Öğrenci Ara:");
        label_4.setBounds(36, 9, 94, 19);
        label_4.setFont(new Font("Tahoma", Font.BOLD, 15));
        aramaPaneli.add(label_4);

        tfOgrenciAra = new JTextField(15);  // Daha büyük bir alan verdik
        tfOgrenciAra.setBounds(135, 6, 201, 25);
        tfOgrenciAra.setFont(new Font("Tahoma", Font.BOLD, 15));
        aramaPaneli.add(tfOgrenciAra);

        JButton btnAraOgrenci = new JButton("Ara");
        btnAraOgrenci.setBounds(341, 5, 84, 27);
        btnAraOgrenci.setBackground(new Color(255, 255, 255));
        btnAraOgrenci.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnAraOgrenci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOgrenci(); // Öğrenci arama fonksiyonunu çağırıyoruz
            }
        });
        sagPanel.setLayout(null);
        aramaPaneli.add(btnAraOgrenci);

        // Öğrenci Listesi ve Arama Panelini Ekle
        sagPanel.add(aramaPaneli);
        sagPanel.add(scrollPane);

        // Ekle, Sil, Güncelle Butonları
        JPanel buttonPanel = new JPanel();
        buttonPanel.setForeground(new Color(128, 128, 255));
        buttonPanel.setBackground(new Color(128, 128, 255));
        buttonPanel.setLayout(new FlowLayout());

        JButton btnEkleOgrenci = new JButton("Ekle");
        btnEkleOgrenci.setBackground(new Color(255, 255, 255));
        btnEkleOgrenci.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnEkleOgrenci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOgrenci();
            }
        });

        JButton btnSilOgrenci = new JButton("Sil");
        btnSilOgrenci.setBackground(new Color(255, 255, 255));
        btnSilOgrenci.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnSilOgrenci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOgrenci();
            }
        });

        JButton btnGüncelleOgrenci = new JButton("Güncelle");
        btnGüncelleOgrenci.setBackground(new Color(255, 255, 255));
        btnGüncelleOgrenci.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnGüncelleOgrenci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOgrenci();
            }
        });
        
        JButton btnListeleOgrenci = new JButton("Öğrencileri Listele");
        btnListeleOgrenci.setBackground(new Color(255, 255, 255));
        btnListeleOgrenci.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnListeleOgrenci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listOgrenciler();
                sagPanel.setVisible(true);
            }
        });

        buttonPanel.add(btnEkleOgrenci);
        buttonPanel.add(btnSilOgrenci);
        buttonPanel.add(btnGüncelleOgrenci);
        buttonPanel.add(btnListeleOgrenci);

        panel.add(solPanel, BorderLayout.WEST);
        panel.add(sagPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Öğrenci arama fonksiyonu
    private void searchOgrenci() {
        String searchQuery = tfOgrenciAra.getText().trim();

        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen arama için bir kelime girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ogrenciId, ogrenciAd, ogrenciTelefon, ogrenciKayitTarihi FROM tableOgrenciler WHERE ogrenciAd LIKE ? OR ogrenciTelefon LIKE ? OR ogrenciId LIKE ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "%" + searchQuery + "%");
                stmt.setString(2, "%" + searchQuery + "%");
                stmt.setString(3, "%" + searchQuery + "%");

                ResultSet rs = stmt.executeQuery();

                // Tabloyu temizle
                ogrenciTableModel.setRowCount(0);

                // Arama sonuçlarını ekle
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getInt("ogrenciId");
                    row[1] = rs.getString("ogrenciAd");
                    row[2] = rs.getString("ogrenciTelefon");
                    row[3] = rs.getDate("ogrenciKayitTarihi");
                    ogrenciTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }



    // Öğrenci ekleme işlemi
    private void addOgrenci() {
        String ogrenciId = tfOgrenciId.getText();
        String ogrenciAd = tfOgrenciAd.getText();
        String ogrenciTelefon = tfOgrenciTelefon.getText();
        String ogrenciKayitTarihi = tfOgrenciKayitTarihi.getText();

        if (ogrenciId.isEmpty() || ogrenciAd.isEmpty() || ogrenciTelefon.isEmpty() || ogrenciKayitTarihi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Veritabanına bağlanma ve öğrenci ekleme
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO tableOgrenciler (ogrenciId, ogrenciAd, ogrenciTelefon, ogrenciKayitTarihi) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, ogrenciId);
                stmt.setString(2, ogrenciAd);
                stmt.setString(3, ogrenciTelefon);
                stmt.setString(4, ogrenciKayitTarihi);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Öğrenci başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Öğrenci bilgilerini temizle
        tfOgrenciId.setText("");
        tfOgrenciAd.setText("");
        tfOgrenciTelefon.setText("");
        tfOgrenciKayitTarihi.setText("");

        // Öğrencileri yeniden listele
        listOgrenciler();
    }

    private void deleteOgrenci() {
        int selectedRow = ogrenciListesi.getSelectedRow();
        if (selectedRow != -1) {
            int ogrenciId = (int) ogrenciTableModel.getValueAt(selectedRow, 0);

            try (Connection connection = DatabaseConnection.getConnection()) {
                // 1. Öğrencinin ödünç aldığı kitapları sil
                String deleteOduncQuery = "DELETE FROM tableOduncler WHERE ogrenciId = ?";
                try (PreparedStatement stmt2 = connection.prepareStatement(deleteOduncQuery)) {
                    stmt2.setInt(1, ogrenciId);
                    stmt2.executeUpdate();
                }

                // 2. Öğrenciyi sil
                String deleteOgrenciQuery = "DELETE FROM tableOgrenciler WHERE ogrenciId = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deleteOgrenciQuery)) {
                    stmt.setInt(1, ogrenciId);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Öğrenci ve ödünç kitapları başarıyla silindi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                ogrenciTableModel.removeRow(selectedRow);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Öğrenci ve ödünç kitapları silinirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen silmek için bir öğrenci seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


 // Öğrenci güncelleme işlemi
    private void updateOgrenci() {
        if (selectedOgrenciId == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir öğrenci seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ogrenciIdStr = tfOgrenciId.getText();  // tfOgrenciId TextField'inden öğrenci numarasını al
        int ogrenciId = -1;  // Başlangıçta -1 değerini verelim, eğer geçerli bir ID girilmezse hata vereceğiz.

        try {
            // Öğrenci numarasını (ID) int'e dönüştür
            ogrenciId = Integer.parseInt(ogrenciIdStr);
        } catch (NumberFormatException e) {
            // Eğer geçerli bir integer girilmezse, hata mesajı ver.
            JOptionPane.showMessageDialog(this, "Geçerli bir öğrenci numarası girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ogrenciAd = tfOgrenciAd.getText();
        String ogrenciTelefon = tfOgrenciTelefon.getText();
        String ogrenciKayitTarihi = tfOgrenciKayitTarihi.getText();

        if (ogrenciAd.isEmpty() || ogrenciTelefon.isEmpty() || ogrenciKayitTarihi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Veritabanına bağlanma ve öğrenci güncelleme
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE tableOgrenciler SET ogrenciAd = ?, ogrenciTelefon = ?, ogrenciKayitTarihi = ? WHERE ogrenciId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, ogrenciAd);
                stmt.setString(2, ogrenciTelefon);
                stmt.setString(3, ogrenciKayitTarihi);
                stmt.setInt(4, selectedOgrenciId);  // Burada selectedOgrenciId'yi kullanıyoruz, ID'yi değiştirmek istiyorsanız bunu güncelleyin.

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Öğrenci başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Öğrenci güncellenirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Öğrencileri yeniden listele
        listOgrenciler();
        selectedOgrenciId = -1;
    }


    // Öğrenci listesini veritabanından alıp tabloya yazdırma
 // Öğrenciler listesini güncelleme
    private void listOgrenciler() {
        // Veritabanına bağlanma ve öğrencileri alma
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ogrenciId, ogrenciAd, ogrenciTelefon, ogrenciKayitTarihi FROM tableOgrenciler";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Tabloyu temizle
                ogrenciTableModel.setRowCount(0);

                // Öğrencileri JTable'ye ekle
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getInt("ogrenciId");
                    row[1] = rs.getString("ogrenciAd");
                    row[2] = rs.getString("ogrenciTelefon");
                    row[3] = rs.getDate("ogrenciKayitTarihi");
                    ogrenciTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanından öğrenciler alınırken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Seçilen öğrenciyi alıp sol paneldeki alanlara yükleme
    private void loadSelectedOgrenci() {
        int selectedRow = ogrenciListesi.getSelectedRow();  // Seçilen satırın indeksini alıyoruz.
        
        // Eğer bir satır seçildiyse
        if (selectedRow != -1) {
            // 'ogrenciId' int olarak alınacak, çünkü veritabanındaki veri tipi int
            selectedOgrenciId = (Integer) ogrenciTableModel.getValueAt(selectedRow, 0);  // ogrenciId'yi int olarak alıyoruz
            
            // Sol paneldeki form alanlarına aktarma
            tfOgrenciId.setText(String.valueOf(selectedOgrenciId));  // Integer'ı String'e dönüştürüp yazdırıyoruz
            tfOgrenciAd.setText((String) ogrenciTableModel.getValueAt(selectedRow, 1));  // ogrenciAd varchar, doğrudan String olarak alınabilir
            tfOgrenciTelefon.setText((String) ogrenciTableModel.getValueAt(selectedRow, 2));  // ogrenciTelefon varchar, doğrudan String olarak alınabilir
            
            // ogrenciKayitTarihi tarih, String yerine Date olarak alınmalı
            Object kayitTarihiObj = ogrenciTableModel.getValueAt(selectedRow, 3);  // ogrenciKayitTarihi (Date) verisini alıyoruz
            
            if (kayitTarihiObj != null) {
                // Tarihi formatlamak için SimpleDateFormat kullanıyoruz
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  // Tarih formatı: yyyy-MM-dd (Örnek)
                String formattedDate = dateFormat.format(kayitTarihiObj);  // Tarihi formatlıyoruz
                tfOgrenciKayitTarihi.setText(formattedDate);  // Formatlanmış tarihi form alanına aktarıyoruz
            } else {
                tfOgrenciKayitTarihi.setText("");  // Eğer tarih null ise, boş bırakıyoruz
            }
        }
    }
    
    private JPanel createOduncPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Sol Panel: Kullanıcıdan öğrenciId ve kitapId bilgilerini alacağız
        JPanel solPanel = new JPanel();
        solPanel.setBackground(new Color(128, 128, 255));
        solPanel.setLayout(new GridLayout(4, 2));

        JLabel label = new JLabel("Öğrenci ID:");
        label.setBackground(new Color(255, 255, 255));
        label.setFont(new Font("Tahoma", Font.BOLD, 18));
        solPanel.add(label);
                tfOduncOgrenciId = new JTextField();
                tfOduncOgrenciId.setFont(new Font("Tahoma", Font.BOLD, 15));
                solPanel.add(tfOduncOgrenciId);
        
                JLabel label_1 = new JLabel("Kitap ID:");
                label_1.setFont(new Font("Tahoma", Font.BOLD, 18));
                solPanel.add(label_1);

        // Sağ Panel: Ödünç işlemleri ve ödünç listeleme
        JPanel sagPanel = new JPanel();
        sagPanel.setBackground(new Color(128, 128, 255));

        // Tablolar için başlıklar
        String[] oduncColumnNames = {"Öğrenci ID", "Kitap ID", "Ödünç Tarihi", "İade Tarihi"};
        oduncTableModel = new DefaultTableModel(oduncColumnNames, 0);
        sagPanel.setLayout(null);
        oduncListesi = new JTable(oduncTableModel);
        JScrollPane scrollPane = new JScrollPane(oduncListesi);
        scrollPane.setBounds(0, 0, 673, 496);
        sagPanel.add(scrollPane);

        // Alt Panel: Ödünç işlemleri butonları
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(128, 128, 255));
        buttonPanel.setLayout(new FlowLayout());

        JButton btnOduncAl = new JButton("Ödünç Al");
        btnOduncAl.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnOduncAl.setBackground(new Color(255, 255, 255));
        btnOduncAl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oduncAl();
            }
        });

        JButton btnIadeEt = new JButton("İade Et");
        btnIadeEt.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnIadeEt.setBackground(new Color(255, 255, 255));
        btnIadeEt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iadeEt();
            }
        });

        JButton btnListeleOduncler = new JButton("Ödünçleri Listele");
        btnListeleOduncler.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnListeleOduncler.setBackground(new Color(255, 255, 255));
        btnListeleOduncler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listOduncler();
            }
        });

        buttonPanel.add(btnOduncAl);
        buttonPanel.add(btnIadeEt);
        buttonPanel.add(btnListeleOduncler);

        // Buton panelini sol panelin altına ekleyelim
        panel.add(solPanel, BorderLayout.WEST);
        tfOduncKitapId = new JTextField();
        tfOduncKitapId.setFont(new Font("Tahoma", Font.BOLD, 15));
        solPanel.add(tfOduncKitapId);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(sagPanel, BorderLayout.CENTER);

        return panel;
    }

    private void oduncAl() {
        String ogrenciIdStr = tfOduncOgrenciId.getText();
        String kitapIdStr = tfOduncKitapId.getText();

        if (ogrenciIdStr.isEmpty() || kitapIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kitabın durumunu kontrol et (Boş mu Dolu mu)
        try (Connection connection = DatabaseConnection.getConnection()) {
            String durumQuery = "SELECT durum FROM tableKitaplar WHERE kitapId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(durumQuery)) {
                stmt.setInt(1, Integer.parseInt(kitapIdStr));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String durum = rs.getString("durum");

                    if ("Dolu".equals(durum)) {
                        JOptionPane.showMessageDialog(this, "Bu kitap zaten ödünç alınmış.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Veritabanına bağlanma ve ödünç alma işlemi
            String query = "INSERT INTO tableOduncler (ogrenciId, kitapId, oduncTarihi) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(ogrenciIdStr));
                stmt.setInt(2, Integer.parseInt(kitapIdStr));

                // Ödünç alınma tarihini sistemden alıyoruz
                Date currentDate = new Date();
                java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
                stmt.setDate(3, sqlDate);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kitap başarıyla ödünç alındı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                // Kitabın durumunu "Dolu" olarak güncelle
                String updateDurumQuery = "UPDATE tableKitaplar SET durum = 'Dolu' WHERE kitapId = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateDurumQuery)) {
                    updateStmt.setInt(1, Integer.parseInt(kitapIdStr));
                    updateStmt.executeUpdate();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ödünç alma sırasında hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Ödünçleri yeniden listele
        listOduncler();
    }


    private void iadeEt() {
        String ogrenciIdStr = tfOduncOgrenciId.getText();
        String kitapIdStr = tfOduncKitapId.getText();

        if (ogrenciIdStr.isEmpty() || kitapIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kitabın durumunu kontrol et (Dolu mu Boş mu)
        try (Connection connection = DatabaseConnection.getConnection()) {
            String durumQuery = "SELECT durum FROM tableKitaplar WHERE kitapId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(durumQuery)) {
                stmt.setInt(1, Integer.parseInt(kitapIdStr));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String durum = rs.getString("durum");

                    if ("Boş".equals(durum)) {
                        JOptionPane.showMessageDialog(this, "Bu kitap zaten iade edilmiş veya ödünç alınmamış.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Veritabanına bağlanma ve iade işlemi
            String query = "UPDATE tableOduncler SET iadeTarihi = ? WHERE ogrenciId = ? AND kitapId = ? AND iadeTarihi IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(2, Integer.parseInt(ogrenciIdStr));
                stmt.setInt(3, Integer.parseInt(kitapIdStr));

                // İade tarihini sistemden alıyoruz
                Date currentDate = new Date();
                java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
                stmt.setDate(1, sqlDate);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Kitap başarıyla iade edildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                    // Kitabın durumunu "Boş" olarak güncelle
                    String updateDurumQuery = "UPDATE tableKitaplar SET durum = 'Boş' WHERE kitapId = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateDurumQuery)) {
                        updateStmt.setInt(1, Integer.parseInt(kitapIdStr));
                        updateStmt.executeUpdate();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Bu kitap zaten iade edilmiş.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "İade işlemi sırasında hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Ödünçleri yeniden listele
        listOduncler();
    }


    private void listOduncler() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT oduncId, ogrenciId, kitapId, oduncTarihi, iadeTarihi FROM tableOduncler";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Tabloyu temizle
                oduncTableModel.setRowCount(0);

                // Ödünçleri tabloya ekle
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getInt("ogrenciId");
                    row[1] = rs.getInt("kitapId");
                    row[2] = rs.getDate("oduncTarihi");
                    row[3] = rs.getDate("iadeTarihi");
                    oduncTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ödünç işlemleri alınırken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }







    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu().setVisible(true);
            }
        });
    }
}