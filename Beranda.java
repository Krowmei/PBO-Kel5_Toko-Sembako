// src/Beranda.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL; // Import untuk URL

public class Beranda extends JFrame {

    public Beranda() {
        setTitle("Toko Sembako");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel utama dengan latar belakang gambar
        BackgroundPanel backgroundPanel = new BackgroundPanel("/img/BGberanda.jpg");
        backgroundPanel.setLayout(new BorderLayout()); // Gunakan BorderLayout untuk menata komponen anak
        setContentPane(backgroundPanel); // Atur backgroundPanel sebagai content pane utama

        // Panel konten tengah untuk judul dan tombol
        JPanel panelContent = new JPanel();
        panelContent.setOpaque(false); // Penting: Buat panel ini transparan agar background terlihat
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setAlignmentX(Component.CENTER_ALIGNMENT); // Pusatkan secara horizontal dalam BoxLayout

        // Label judul "Toko Sembako"
        JLabel titleLabel = new JLabel("Toko Sembako");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Pusatkan label secara horizontal
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Pusatkan teks di dalam label

        // Tombol "Yuk, belanja"
        JButton masukButton = new JButton("Yuk, belanja");
        masukButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        masukButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Pusatkan tombol secara horizontal
        masukButton.setPreferredSize(new Dimension(200, 40)); // Atur ukuran preferensi
        masukButton.setMaximumSize(new Dimension(200, 40)); // Atur ukuran maksimum (penting untuk BoxLayout)

        // Tambahkan ActionListener untuk tombol "Yuk, belanja"
        masukButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Panggil halaman utama pelanggan dan tutup frame saat ini
                // Pastikan kelas HalamanUtamaPelanggan sudah ada dan dapat diinisialisasi
                new HalamanUtamaPelanggan().setVisible(true); // Tampilkan halaman baru
                dispose(); // Tutup frame Beranda saat ini
            }
        });

        // Tambahkan komponen ke panel konten tengah dengan spasi vertikal
        panelContent.add(Box.createVerticalGlue()); // Spasi fleksibel di atas
        panelContent.add(titleLabel);
        panelContent.add(Box.createRigidArea(new Dimension(0, 30))); // Spasi tetap 30px
        panelContent.add(masukButton);
        panelContent.add(Box.createVerticalGlue()); // Spasi fleksibel di bawah

        // Tambahkan panel konten tengah ke backgroundPanel di posisi CENTER
        backgroundPanel.add(panelContent, BorderLayout.CENTER);

        // Panel untuk logo admin di pojok kanan bawah
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.setOpaque(false); // Penting: Buat panel ini transparan
        
        // Memuat ikon admin dari classpath
        ImageIcon adminIcon = null;
        try {
            URL adminIconUrl = getClass().getResource("/img/iconAdmin.jpg");
            if (adminIconUrl != null) {
                adminIcon = new ImageIcon(adminIconUrl);
            } else {
                System.err.println("ERROR: Admin icon not found at classpath: /img/iconAdmin.jpg");
                // Fallback jika ikon tidak ditemukan
                adminIcon = new ImageIcon(); // Ikon kosong
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load admin icon: " + e.getMessage());
            e.printStackTrace();
            adminIcon = new ImageIcon(); // Ikon kosong
        }

        // Buat tombol admin dengan ikon
        JButton adminButton = new JButton(adminIcon);
        adminButton.setPreferredSize(new Dimension(40, 40)); // Atur ukuran tombol
        adminButton.setContentAreaFilled(false); // Buat area konten tombol transparan
        adminButton.setBorderPainted(false); // Sembunyikan border tombol
        adminButton.setFocusPainted(false); // Sembunyikan fokus saat diklik
        adminButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Ubah kursor saat dihover

        // Tambahkan ActionListener untuk tombol admin
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Panggil form login admin dan tutup frame saat ini
                // Pastikan kelas LoginAdmin sudah ada dan dapat diinisialisasi
                new LoginAdmin().setVisible(true); // Tampilkan halaman login admin
                dispose(); // Tutup frame Beranda saat ini
            }
        });

        bottomRightPanel.add(adminButton); // Tambahkan tombol admin ke panel kanan bawah
        backgroundPanel.add(bottomRightPanel, BorderLayout.SOUTH); // Tambahkan panel kanan bawah ke backgroundPanel

        setVisible(true); // Jadikan frame terlihat
    }

    public static void main(String[] args) {
        // Jalankan GUI di Event Dispatch Thread (EDT) untuk keamanan thread Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Beranda();
            }
        });
    }

    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            setOpaque(false);

            try {
        
                URL imageUrl = getClass().getResource("/img/BGberanda.jpg");

                if (imageUrl != null) {
                    backgroundImage = new ImageIcon(getClass().getResource("/img/placeholder.png")).getImage();
                } else {
                    System.err.println("ERROR: Background image not found at classpath: " + imagePath);
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to load background image " + imagePath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(new Color(255, 204, 127));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
