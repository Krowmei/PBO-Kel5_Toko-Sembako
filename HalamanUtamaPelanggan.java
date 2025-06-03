import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HalamanUtamaPelanggan extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    public HalamanUtamaPelanggan() {
        setTitle("Halaman Pelanggan");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        connectDB();

        model = new DefaultTableModel(new Object[]{"Gambar", "Nama", "Harga", "Stok"}, 0) {
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return String.class;
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(70);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(new Color(40, 40, 40));
        table.setBackground(new Color(255, 255, 255));
        table.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(true);
        tableWrapper.setBackground(new Color(255, 248, 240));
        tableWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 76), 3),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        tableWrapper.setMaximumSize(new Dimension(850, 400));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(tableWrapper);
        centerPanel.add(Box.createVerticalGlue());

        JButton btnPesan = new JButton("Pesan Sekarang");
        JButton btnKembali = new JButton("Kembali ke Beranda");

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension btnSize = new Dimension(180, 35);

        for (JButton btn : new JButton[]{btnPesan, btnKembali}) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(btnSize);
            btn.setBackground(new Color(0, 153, 76));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }

        btnPesan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HalamanPemesanan(HalamanUtamaPelanggan.this);
            }
        });

        btnKembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Beranda();
            }
        });

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        panelBawah.setOpaque(true);
        panelBawah.setBackground(new Color(255, 255, 255, 220));
        panelBawah.add(btnKembali);
        panelBawah.add(btnPesan);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(panelBawah, BorderLayout.SOUTH);

        loadDataBarang();

        setVisible(true);
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/toko_sembako", "root", "");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal koneksi ke database: " + ex.getMessage());
        }
    }

    private void loadDataBarang() {
        model.setRowCount(0);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM barang")) {

            while (rs.next()) {
                String nama = rs.getString("nama");
                String harga = String.format("%.2f", rs.getDouble("harga"));
                String stok = String.valueOf(rs.getInt("stok"));
                String gambar = rs.getString("gambar");

                ImageIcon icon = new ImageIcon("img/" + gambar);
                Image image = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                model.addRow(new Object[]{new ImageIcon(image), nama, harga, stok});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data barang: " + ex.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.out.println("Gagal memuat gambar: " + imagePath);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HalamanUtamaPelanggan();
            }
        });
    }
}
