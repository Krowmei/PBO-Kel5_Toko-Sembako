import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class HalamanPemesanan extends JFrame {
    private JComboBox<String> barangComboBox;
    private JTextField hargaField, jumlahField;
    private JButton tambahButton, cetakStrukButton, resetButton, hapusButton, kembaliButton, bayarCashButton;
    private JTable tabelBelanja;
    private DefaultTableModel tableModel;
    private JTextArea strukArea;

    private Map<String, Double> daftarBarang = new LinkedHashMap<String, Double>();
    private Map<String, Integer> stokBarang = new HashMap<String, Integer>();
    private JFrame HalamanUtama;

    public HalamanPemesanan(JFrame halamanUtama) {
        this.HalamanUtama = halamanUtama;
        setTitle("Halaman Pemesanan");
        setSize(630, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JPanel wrapperPanel = new JPanel(null);
        wrapperPanel.setOpaque(true);
        wrapperPanel.setBackground(new Color(255, 248, 240));
        wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 76), 3),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        wrapperPanel.setBounds(20, 20, 580, 580);
        backgroundPanel.add(wrapperPanel);

        loadDataBarang();

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

        JLabel labelBarang = new JLabel("Pilih Barang:");
        barangComboBox = new JComboBox<String>(daftarBarang.keySet().toArray(new String[0]));
        JLabel labelHarga = new JLabel("Harga:");
        hargaField = new JTextField();
        hargaField.setEditable(false);
        JLabel labelJumlah = new JLabel("Jumlah:");
        jumlahField = new JTextField();
        tambahButton = new JButton("Tambah ke Keranjang");

        JComponent[] labels = new JComponent[]{labelBarang, labelHarga, labelJumlah};
        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(labelFont);
        }
        tambahButton.setFont(buttonFont);
        tambahButton.setBackground(new Color(0, 153, 76));
        tambahButton.setForeground(Color.WHITE);

        labelBarang.setBounds(20, 10, 100, 25);
        barangComboBox.setBounds(140, 10, 150, 25);
        labelHarga.setBounds(20, 45, 100, 25);
        hargaField.setBounds(140, 45, 150, 25);
        labelJumlah.setBounds(20, 80, 100, 25);
        jumlahField.setBounds(140, 80, 150, 25);
        tambahButton.setBounds(140, 115, 180, 30);

        tableModel = new DefaultTableModel(new Object[]{"Barang", "Harga", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        tabelBelanja = new JTable(tableModel);
        tabelBelanja.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelBelanja.setRowHeight(25);
        tabelBelanja.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(tabelBelanja);
        scrollPane.setBounds(20, 160, 530, 150);

        cetakStrukButton = new JButton("Cetak Struk");
        resetButton = new JButton("Reset");
        hapusButton = new JButton("Hapus Baris");
        kembaliButton = new JButton("Kembali");
        bayarCashButton = new JButton("Bayar Cash");

        JButton[] tombol = {cetakStrukButton, resetButton, hapusButton, kembaliButton, bayarCashButton};
        for (int i = 0; i < tombol.length; i++) {
            tombol[i].setFont(buttonFont);
            tombol[i].setBackground(new Color(0, 153, 76));
            tombol[i].setForeground(Color.WHITE);
        }

        cetakStrukButton.setBounds(20, 320, 120, 30);
        resetButton.setBounds(150, 320, 100, 30);
        hapusButton.setBounds(260, 320, 120, 30);
        kembaliButton.setBounds(390, 320, 120, 30);

        strukArea = new JTextArea();
        strukArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        strukArea.setBackground(new Color(255, 255, 240));
        strukArea.setEditable(false);
        JScrollPane strukScroll = new JScrollPane(strukArea);
        strukScroll.setBounds(20, 360, 530, 160);

        bayarCashButton.setBounds(160, 530, 240, 35);

        wrapperPanel.add(labelBarang);
        wrapperPanel.add(barangComboBox);
        wrapperPanel.add(labelHarga);
        wrapperPanel.add(hargaField);
        wrapperPanel.add(labelJumlah);
        wrapperPanel.add(jumlahField);
        wrapperPanel.add(tambahButton);
        wrapperPanel.add(scrollPane);
        wrapperPanel.add(cetakStrukButton);
        wrapperPanel.add(resetButton);
        wrapperPanel.add(hapusButton);
        wrapperPanel.add(kembaliButton);
        wrapperPanel.add(bayarCashButton);
        wrapperPanel.add(strukScroll);

        barangComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barang = (String) barangComboBox.getSelectedItem();
                hargaField.setText(String.valueOf(daftarBarang.get(barang)));
            }
        });

        tambahButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barang = (String) barangComboBox.getSelectedItem();
                double harga = daftarBarang.get(barang);
                int jumlah;
                try {
                    jumlah = Integer.parseInt(jumlahField.getText());
                    if (jumlah <= 0 || jumlah > stokBarang.get(barang)) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(HalamanPemesanan.this, "Jumlah tidak valid atau stok tidak cukup.");
                    return;
                }
                double subtotal = harga * jumlah;
                tableModel.addRow(new Object[]{barang, harga, jumlah, subtotal});
                jumlahField.setText("");
            }
        });

        cetakStrukButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tampilkanStruk("PREVIEW", "LUNAS", null);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                strukArea.setText("");
                jumlahField.setText("");
            }
        });

        hapusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelBelanja.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(HalamanPemesanan.this, "Pilih baris yang ingin dihapus");
                }
            }
        });

        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (HalamanUtama != null) {
                    HalamanUtama.setVisible(true);
                }
                dispose();
            }
        });

        bayarCashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prosesPembayaran("Cash");
            }
        });

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getColumn() == 2 && e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    try {
                        double harga = Double.parseDouble(tableModel.getValueAt(row, 1).toString());
                        int jumlah = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
                        double subtotal = harga * jumlah;
                        tableModel.setValueAt(subtotal, row, 3);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(HalamanPemesanan.this, "Input jumlah tidak valid");
                    }
                }
            }
        });

        barangComboBox.setSelectedIndex(0);
        hargaField.setText(String.valueOf(daftarBarang.get(barangComboBox.getSelectedItem())));
        setVisible(true);
    }

    private void loadDataBarang() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko_sembako", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nama, harga, stok FROM barang");
            while (rs.next()) {
                String nama = rs.getString("nama");
                double harga = rs.getDouble("harga");
                int stok = rs.getInt("stok");
                daftarBarang.put(nama, harga);
                stokBarang.put(nama, stok);
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data barang: " + e.getMessage());
        }
    }

    private void prosesPembayaran(String metode) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }

        double total = hitungTotal();
        String kode = "PES" + (int)(Math.random() * 1000000);
        String status = metode.equals("Cash") ? "Belum Lunas" : "LUNAS";

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Total: Rp" + total + "\nMetode: " + metode + "\nLanjutkan?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko_sembako", "root", "");
                PreparedStatement psPesanan = conn.prepareStatement(
                    "INSERT INTO pesanan (kode_pesanan, status, metode_pembayaran) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );

                psPesanan.setString(1, kode);
                psPesanan.setString(2, status);
                psPesanan.setString(3, metode);
                psPesanan.executeUpdate();

                ResultSet generatedKeys = psPesanan.getGeneratedKeys();
                int pesananId = 0;
                if (generatedKeys.next()) {
                    pesananId = generatedKeys.getInt(1);
                }

                PreparedStatement psDetail = conn.prepareStatement(
                    "INSERT INTO detail_pesanan (pesanan_id, nama_barang, harga, jumlah, subtotal) VALUES (?, ?, ?, ?, ?)"
                );

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String barang = tableModel.getValueAt(i, 0).toString();
                    double harga = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
                    int jumlah = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                    double subtotal = Double.parseDouble(tableModel.getValueAt(i, 3).toString());

                    psDetail.setInt(1, pesananId);
                    psDetail.setString(2, barang);
                    psDetail.setDouble(3, harga);
                    psDetail.setInt(4, jumlah);
                    psDetail.setDouble(5, subtotal);
                    psDetail.addBatch();

                    PreparedStatement updateStok = conn.prepareStatement("UPDATE barang SET stok = stok - ? WHERE nama = ?");
                    updateStok.setInt(1, jumlah);
                    updateStok.setString(2, barang);
                    updateStok.executeUpdate();
                }

                psDetail.executeBatch();
                conn.close();

                tampilkanStruk(kode, status, total);
                JOptionPane.showMessageDialog(this, "Pesanan disimpan.\nKode: " + kode);
                tableModel.setRowCount(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal simpan pesanan: " + e.getMessage());
            }
        }
    }

    private double hitungTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += Double.parseDouble(tableModel.getValueAt(i, 3).toString());
        }
        return total;
    }

    private void tampilkanStruk(String kode, String status, Double totalOverride) {
        StringBuilder struk = new StringBuilder();
        double total = 0;
        struk.append("======= STRUK BELANJA =======\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String barang = tableModel.getValueAt(i, 0).toString();
            int jumlah = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            double subtotal = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
            struk.append(String.format("%s x%d = Rp%.0f\n", barang, jumlah, subtotal));
            total += subtotal;
        }
        if (totalOverride != null) total = totalOverride;
        struk.append("------------------------------\n");
        struk.append(String.format("TOTAL : Rp%.0f\n", total));
        if (!"PREVIEW".equals(kode)) {
            struk.append(String.format("KODE  : %s\n", kode));
            struk.append(String.format("STATUS: %s\n", status));
        }
        struk.append("==============================");
        strukArea.setText(struk.toString());
    }

    private static class BackgroundPanel extends JPanel {
        private Image bgImage;

        public BackgroundPanel(String path) {
            try {
                bgImage = new ImageIcon(path).getImage();
            } catch (Exception e) {
                bgImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HalamanPemesanan(null);
            }
        });
    }
}
