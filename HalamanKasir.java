import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class HalamanKasir extends JFrame {
    private JTable tabelPesanan;
    private DefaultTableModel tableModel;
    private JButton konfirmasiButton, kembaliButton, hapusBarisButton, resetSemuaButton;
    private JFrame halamanAdmin;

    public HalamanKasir(JFrame halamanAdmin) {
        this.halamanAdmin = halamanAdmin;

        setTitle("Toko Sembako");
        setSize(650, 450);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new java.awt.Color(255, 204, 127));

        tableModel = new DefaultTableModel(new Object[]{"Kode", "Total", "Status"}, 0);
        tabelPesanan = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelPesanan);
        scrollPane.setBounds(30, 30, 570, 200);

        konfirmasiButton = new JButton("Konfirmasi Pembayaran");
        konfirmasiButton.setBounds(30, 250, 250, 35);
        kembaliButton = new JButton("Kembali");
        kembaliButton.setBounds(30, 300, 120, 35);
        hapusBarisButton = new JButton("Hapus Baris");
        hapusBarisButton.setBounds(310, 250, 140, 35);
        resetSemuaButton = new JButton("Reset Semua");
        resetSemuaButton.setBounds(470, 250, 130, 35);

        add(scrollPane);
        add(konfirmasiButton);
        add(kembaliButton);
        add(hapusBarisButton);
        add(resetSemuaButton);

        tampilkanPesanan();

        konfirmasiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelPesanan.getSelectedRow();
                if (selectedRow != -1) {
                    String kode = (String) tableModel.getValueAt(selectedRow, 0);
                    String totalStr = (String) tableModel.getValueAt(selectedRow, 1);
                    double total = Double.parseDouble(totalStr.replace("Rp", "").replace(".", "").trim());

                    new HalamanCash(kode, total, new Runnable() {
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
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelPesanan.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(HalamanKasir.this, "Pilih baris yang ingin dihapus.");
                }
            }
        });

        resetSemuaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
            public void actionPerformed(ActionEvent e) {
                halamanAdmin.setVisible(true);
                dispose();
            }
        });

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
        tableModel.setRowCount(0); // bersihkan isi tabel
        while (rs.next()) {
            String kode = rs.getString("kode_pesanan");
            double total = rs.getDouble("total");
            String status = rs.getString("status");
            tableModel.addRow(new Object[]{kode, "Rp" + (int) total, status});
        }
        conn.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database.\n" + e.getMessage());
    }
}

}
