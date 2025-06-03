import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HalamanKasir extends JFrame {
    private JTable tabelPesanan;
    private DefaultTableModel tableModel;
    private JButton konfirmasiButton, kembaliButton, hapusBarisButton, resetSemuaButton;
    private JFrame halamanAdmin;

    public HalamanKasir(JFrame halamanAdmin) {
        this.halamanAdmin = halamanAdmin;

        setTitle("Halaman Kasir");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        wrapperPanel.setLayout(new BorderLayout(0, 20));

        tableModel = new DefaultTableModel(new Object[]{"Kode", "Total", "Status"}, 0);

        tabelPesanan = new JTable(tableModel);
        tabelPesanan.setRowHeight(40);
        tabelPesanan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelPesanan.setForeground(Color.DARK_GRAY);
        tabelPesanan.setBackground(Color.WHITE);
        tabelPesanan.setSelectionBackground(new Color(0, 153, 76, 150));
        tabelPesanan.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabelPesanan);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 76), 2));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(255, 255, 255, 220));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        buttonPanel.setPreferredSize(new Dimension(800, 45));
        buttonPanel.setMinimumSize(new Dimension(0, 45));

        Color green = new Color(0, 153, 76);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension btnSize = new Dimension(180, 35);

        konfirmasiButton = new JButton("Konfirmasi");
        kembaliButton = new JButton("Kembali");
        hapusBarisButton = new JButton("Hapus Baris");
        resetSemuaButton = new JButton("Reset Semua");

        JButton[] buttons = {konfirmasiButton, hapusBarisButton, resetSemuaButton, kembaliButton};
        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(btnSize);
            btn.setBackground(green);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            buttonPanel.add(btn);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(buttonPanel);

        wrapperPanel.add(contentPanel, BorderLayout.CENTER);
        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);

        konfirmasiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedRow = tabelPesanan.getSelectedRow();
                if (selectedRow != -1) {
                    String kode = (String) tableModel.getValueAt(selectedRow, 0);
                    String totalStr = (String) tableModel.getValueAt(selectedRow, 1);
                    double total = Double.parseDouble(totalStr.replace("Rp", "").replace(".", "").trim());

                    new HalamanCash(kode, total, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko_sembako", "root", "");
                                PreparedStatement ps = conn.prepareStatement("UPDATE pesanan SET status='LUNAS' WHERE kode_pesanan=?");
                                ps.setString(1, kode);
                                ps.executeUpdate();
                                conn.close();
                                tableModel.setValueAt("LUNAS", selectedRow, 2);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(HalamanKasir.this, "Gagal update status.\n" + ex.getMessage());
                            }
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(HalamanKasir.this, "Pilih salah satu pesanan.");
                }
            }
        });

        hapusBarisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedRow = tabelPesanan.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(HalamanKasir.this, "Pilih baris yang ingin dihapus.");
                }
            }
        });

        resetSemuaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int confirm = JOptionPane.showConfirmDialog(
                        HalamanKasir.this,
                        "Apakah Anda yakin ingin menghapus semua data pesanan?",
                        "Konfirmasi Reset",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.setRowCount(0);
                }
            }
        });

        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                halamanAdmin.setVisible(true);
                dispose();
            }
        });

        tampilkanPesanan();

        setVisible(true);
    }

    private void tampilkanPesanan() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko_sembako", "root", "");
            String sql = "SELECT p.kode_pesanan, SUM(dp.subtotal) AS total, p.status " +
                    "FROM pesanan p " +
                    "JOIN detail_pesanan dp ON p.id = dp.pesanan_id " +
                    "WHERE p.status = 'Belum Lunas' " +
                    "GROUP BY p.kode_pesanan, p.status";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            tableModel.setRowCount(0);
            while (rs.next()) {
                String kode = rs.getString("kode_pesanan");
                double total = rs.getDouble("total");
                String status = rs.getString("status");
                tableModel.addRow(new Object[]{kode, "Rp" + String.format("%,d", (int) total).replace(',', '.'), status});
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database.\n" + e.getMessage());
        }
    }

    static class BackgroundPanel extends JPanel {
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
            }
        }
    }
}
