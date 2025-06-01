
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.net.URL;


public class HalamanPemesanan extends JFrame {
    private JComboBox<String> barangComboBox;
    private JTextField hargaField, jumlahField;
    private JButton tambahButton, cetakStrukButton, resetButton, hapusButton, kembaliButton, bayarCashButton, bayarCashlessButton;
    private JTable tabelBelanja;
    private DefaultTableModel tableModel;
    private JTextArea strukArea;

    private Map<String, Double> daftarBarang = new LinkedHashMap<>();
    private Map<String, Integer> stokBarang = new HashMap<>();
    private JFrame HalamanUtama; 

    public HalamanPemesanan(JFrame halamanUtama) {
        this.HalamanUtama = halamanUtama;
        setTitle("Toko Sembako");
        setSize(620, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        

        Color backgroundColor = new Color(245, 250, 255); 
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundColor);
        backgroundPanel.setLayout(null); 
        setContentPane(backgroundPanel);
        

        loadDataBarang();

        JLabel labelBarang = new JLabel("Pilih Barang:");
        barangComboBox = new JComboBox<>(daftarBarang.keySet().toArray(new String[0]));
        JLabel labelHarga = new JLabel("Harga:");
        hargaField = new JTextField();
        hargaField.setEditable(false);
        JLabel labelJumlah = new JLabel("Jumlah:");
        jumlahField = new JTextField();
        tambahButton = new JButton("Tambah ke Keranjang");

        labelBarang.setBounds(30, 20, 100, 25);
        barangComboBox.setBounds(150, 20, 150, 25);
        labelHarga.setBounds(30, 55, 100, 25);
        hargaField.setBounds(150, 55, 150, 25);
        labelJumlah.setBounds(30, 90, 100, 25);
        jumlahField.setBounds(150, 90, 150, 25);
        tambahButton.setBounds(150, 125, 180, 30);

        tableModel = new DefaultTableModel(new Object[]{"Barang", "Harga", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        tabelBelanja = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelBelanja);
        scrollPane.setBounds(30, 170, 540, 150);

        cetakStrukButton = new JButton("Cetak Struk");
        resetButton = new JButton("Reset");
        hapusButton = new JButton("Hapus Baris");
        kembaliButton = new JButton("Kembali");
        bayarCashButton = new JButton("Bayar Cash");
        bayarCashlessButton = new JButton("Bayar Cashless");

        cetakStrukButton.setBounds(30, 330, 120, 30);
        resetButton.setBounds(160, 330, 100, 30);
        hapusButton.setBounds(270, 330, 120, 30);
        kembaliButton.setBounds(410, 330, 120, 30);
        bayarCashButton.setBounds(170, 580, 260, 35);

        strukArea = new JTextArea();
        strukArea.setEditable(false);
        JScrollPane strukScroll = new JScrollPane(strukArea);
        strukScroll.setBounds(30, 370, 540, 200);
        backgroundPanel.add(labelBarang); backgroundPanel.add(barangComboBox);
        backgroundPanel.add(labelHarga); backgroundPanel.add(hargaField);
        backgroundPanel.add(labelJumlah); backgroundPanel.add(jumlahField);
        backgroundPanel.add(tambahButton); backgroundPanel.add(scrollPane);
        backgroundPanel.add(cetakStrukButton); backgroundPanel.add(resetButton);
        backgroundPanel.add(hapusButton); backgroundPanel.add(kembaliButton);
        backgroundPanel.add(bayarCashButton);
        backgroundPanel.add(strukScroll);

        barangComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barang = (String) barangComboBox.getSelectedItem();
                if (barang != null && daftarBarang.containsKey(barang)) {
                    hargaField.setText(String.valueOf(daftarBarang.get(barang)));
                } else {
                    hargaField.setText("");
                }
            }
        });

        tambahButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barang = (String) barangComboBox.getSelectedItem();
                if (barang == null || !daftarBarang.containsKey(barang)) {
                    JOptionPane.showMessageDialog(HalamanPemesanan.this, "Pilih barang terlebih dahulu.");
                    return;
                }
                
                double harga = daftarBarang.get(barang);
                int jumlah;
                try {
                    jumlah = Integer.parseInt(jumlahField.getText());
                    if (jumlah <= 0) {
                        JOptionPane.showMessageDialog(HalamanPemesanan.this, "Jumlah harus lebih dari 0.");
                        return;
                    }
                    if (jumlah > stokBarang.get(barang)) {
                        JOptionPane.showMessageDialog(HalamanPemesanan.this, "Stok " + barang + " tidak cukup. Stok tersedia: " + stokBarang.get(barang));
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(HalamanPemesanan.this, "Jumlah tidak valid.");
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
                tampilkanStruk("PREVIEW", "BELUM DIBAYAR", null);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                strukArea.setText("");
                jumlahField.setText("");
                if (barangComboBox.getItemCount() > 0) {
                    barangComboBox.setSelectedIndex(0);
                }
            }
        });

        hapusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelBelanja.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(HalamanPemesanan.this, "Pilih baris yang ingin dihapus.");
                }
            }
        });

        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HalamanUtama.setVisible(true);
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
                    if (row >= 0 && row < tableModel.getRowCount()) {
                        try {
                            String barangNama = tableModel.getValueAt(row, 0).toString();
                            double harga = Double.parseDouble(tableModel.getValueAt(row, 1).toString());
                            int jumlahBaru = Integer.parseInt(tableModel.getValueAt(row, 2).toString());

                            if (jumlahBaru <= 0) {
                                JOptionPane.showMessageDialog(HalamanPemesanan.this, "Jumlah tidak valid. Akan dihapus dari keranjang.");
                                tableModel.removeRow(row);
                                return;
                            }
                            
                            int stokTersedia = stokBarang.getOrDefault(barangNama, 0);
                            if (jumlahBaru > stokTersedia) {
                                JOptionPane.showMessageDialog(HalamanPemesanan.this, "Stok " + barangNama + " tidak cukup. Stok tersedia: " + stokTersedia + ". Jumlah akan disesuaikan.");
                                tableModel.setValueAt(stokTersedia, row, 2);
                                jumlahBaru = stokTersedia;
                            }

                            double subtotal = harga * jumlahBaru;
                            tableModel.setValueAt(subtotal, row, 3);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(HalamanPemesanan.this, "Input jumlah tidak valid.");
                            tableModel.setValueAt(0, row, 2);
                        } catch (Exception ex) {
                            System.err.println("Error updating table row: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        if (barangComboBox.getItemCount() > 0) {
            barangComboBox.setSelectedIndex(0);
            hargaField.setText(String.valueOf(daftarBarang.get(barangComboBox.getSelectedItem())));
        } else {
            hargaField.setText("0.0");
        }
        
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
                
                // Simpan ke tabel pesanan (tanpa detail barang)
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
                
                // Simpan ke tabel detail_pesanan
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
                    
                    // Kurangi stok barang
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
            struk.append(String.format("KODE  : %s\n", kode));
            struk.append(String.format("STATUS: %s\n", status));
        }
        struk.append("==============================");
        strukArea.setText(struk.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame dummyParent = new JFrame();
                dummyParent.setVisible(false);
                new HalamanPemesanan(dummyParent);
            }
        });
    }
}
