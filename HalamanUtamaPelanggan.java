// src/HalamanUtamaPelanggan.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.net.URL; // Diperlukan untuk Class.getResource()

// Import BackgroundPanel yang sudah direvisi (dengan fill color)

public class HalamanUtamaPelanggan extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    public HalamanUtamaPelanggan() {
        setTitle("Toko Sembako");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Color backgroundColor = new Color(255, 204, 127);
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundColor);
        backgroundPanel.setLayout(new BorderLayout()); 
        setContentPane(backgroundPanel);
        
        // Koneksi database
        connectDB();

        // Model tabel
        model = new DefaultTableModel(new Object[]{"Gambar", "Nama", "Harga", "Stok"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(80);
        
        // JScrollPane untuk tabel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false); // Penting: Buat scrollPane transparan
        scrollPane.getViewport().setOpaque(false); // Sangat penting: Buat viewport tabel transparan
                                                  // agar warna backgroundPanel terlihat di balik tabel

        // Tombol Pesan
        JButton btnPesan = new JButton("Pesan Sekarang");
        btnPesan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPesan.setPreferredSize(new Dimension(180, 35));
        btnPesan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                // Pastikan HalamanPemesanan memiliki konstruktor yang menerima JFrame sebagai argumen
                new HalamanPemesanan(HalamanUtamaPelanggan.this).setVisible(true); // Tampilkan halaman baru
            }
        });

        // Tombol Kembali
        JButton btnKembali = new JButton("Kembali ke Beranda");
        btnKembali.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnKembali.setPreferredSize(new Dimension(180, 35));
        btnKembali.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Beranda().setVisible(true); // Tampilkan Beranda
            }
        });

        // Panel bawah untuk tombol
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.setOpaque(false); // Penting: Buat panel ini transparan
        panelBawah.add(btnKembali);
        panelBawah.add(btnPesan);

        // --- Tambahkan komponen GUI ke backgroundPanel ---
        // Ganti add() yang sebelumnya langsung ke JFrame, menjadi add() ke backgroundPanel
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(panelBawah, BorderLayout.SOUTH);

        // Load data dari DB
        loadDataBarang();
        setVisible(true);
    }

    private void connectDB() {
        try {
           
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/toko_sembako", "root", "");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal koneksi ke database: " + ex.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Cetak stack trace untuk debugging
        }
    }

    private void loadDataBarang() {
        model.setRowCount(0); // Bersihkan baris yang ada di tabel
        if (conn == null) { // Pastikan koneksi tidak null
            JOptionPane.showMessageDialog(this, "Koneksi database belum terjalin.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nama, harga, stok, gambar FROM barang")) { // Ambil juga kolom gambar
            while (rs.next()) {
                String nama = rs.getString("nama");
                String harga = String.valueOf(rs.getDouble("harga"));
                String stok = String.valueOf(rs.getInt("stok"));
                String gambarFileName = rs.getString("gambar"); // Nama file gambar dari DB

                ImageIcon icon = null;
                try {
                    // Menggunakan Class.getResource() dengan path absolut dari root classpath
                    URL imageUrl = getClass().getResource("/img/" + gambarFileName);
                    if (imageUrl != null) {
                        icon = new ImageIcon(imageUrl);
                    } else {
                        System.err.println("Gambar tidak ditemukan: /img/" + gambarFileName);
                        icon = new ImageIcon(); // Ikon kosong sebagai fallback
                    }
                } catch (Exception e) {
                    System.err.println("Terjadi kesalahan saat memuat gambar " + gambarFileName + ": " + e.getMessage());
                    icon = new ImageIcon(); // Ikon kosong jika ada error lain
                }

                // Penskalaan gambar
                Image image = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                model.addRow(new Object[]{new ImageIcon(image), nama, harga, stok});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data barang: " + ex.getMessage(), "Error Load Data", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Cetak stack trace untuk debugging
        } finally {
            // Pastikan koneksi ditutup setelah selesai menggunakan
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HalamanUtamaPelanggan();
            }
        });
    }
}
