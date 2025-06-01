import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class HalamanAdmin extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfNama, tfHarga, tfStok;
    private JLabel lblGambarPath;
    private String selectedImagePath = "/img/";
    private Connection conn;

    public HalamanAdmin() {
        setTitle("Toko Sembako");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Color backgroundColor = new Color(255, 204, 127);
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundColor);
        backgroundPanel.setLayout(new BorderLayout()); 
        setContentPane(backgroundPanel);

        connectDB();

        model = new DefaultTableModel(new Object[]{"ID", "Nama", "Harga", "Stok", "Gambar"}, 0);
        table = new JTable(model) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 4) {
                    return new GambarRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        table.setRowHeight(60);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        tfNama = new JTextField();
        tfHarga = new JTextField();
        tfStok = new JTextField();
        JButton btnPilihGambar = new JButton("Pilih Gambar");
        lblGambarPath = new JLabel("Belum ada gambar dipilih");

        inputPanel.add(new JLabel("Nama Barang:"));
        inputPanel.add(tfNama);
        inputPanel.add(new JLabel("Harga:"));
        inputPanel.add(tfHarga);
        inputPanel.add(new JLabel("Stok:"));
        inputPanel.add(tfStok);
        inputPanel.add(btnPilihGambar);
        inputPanel.add(lblGambarPath);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnKembali = new JButton("Kembali");

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUbah);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnKembali);

        btnPilihGambar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(HalamanAdmin.this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedImagePath = selectedFile.getAbsolutePath();
                    lblGambarPath.setText(selectedFile.getName());
                }
            }
        });

        btnTambah.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tambahBarang();
            }
        });

        btnUbah.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ubahBarang();
            }
        });

        btnHapus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hapusBarang();
            }
        });

        btnKembali.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HalamanUtamaAdmin();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                if (i >= 0) {
                    tfNama.setText(model.getValueAt(i, 1).toString());
                    tfHarga.setText(model.getValueAt(i, 2).toString());
                    tfStok.setText(model.getValueAt(i, 3).toString());
                    lblGambarPath.setText(model.getValueAt(i, 4).toString());
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        inputPanel.setBackground(new Color(255, 204, 127));
        buttonPanel.setBackground(new Color(255, 204, 127));
        mainPanel.setBackground(new Color(255, 204, 127));

        add(mainPanel);
        loadData();
        setVisible(true);
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/toko_sembako", "root", "");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal koneksi ke database: " + ex.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM barang")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        rs.getString("gambar")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void tambahBarang() {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO barang (nama, harga, stok, gambar) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, tfNama.getText());
            ps.setDouble(2, Double.parseDouble(tfHarga.getText()));
            ps.setInt(3, Integer.parseInt(tfStok.getText()));
            ps.setString(4, lblGambarPath.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
            clearForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal tambah data: " + ex.getMessage());
        }
    }

    private void ubahBarang() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = ((Integer) model.getValueAt(row, 0)).intValue();
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE barang SET nama=?, harga=?, stok=?, gambar=? WHERE id=?")) {
            ps.setString(1, tfNama.getText());
            ps.setDouble(2, Double.parseDouble(tfHarga.getText()));
            ps.setInt(3, Integer.parseInt(tfStok.getText()));
            ps.setString(4, lblGambarPath.getText());
            ps.setInt(5, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            clearForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal ubah data: " + ex.getMessage());
        }
    }

    private void hapusBarang() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = ((Integer) model.getValueAt(row, 0)).intValue();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM barang WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            clearForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        tfNama.setText("");
        tfHarga.setText("");
        tfStok.setText("");
        lblGambarPath.setText("Belum ada gambar dipilih");
        selectedImagePath = "";
        table.clearSelection();
    }

    class GambarRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = new JLabel();
        if (value != null && !value.toString().isEmpty()) {
            // Path absolut sesuai struktur folder Anda
            String imgPath = System.getProperty("user.dir") + File.separator + "tokoSembako" + File.separator + "img" + File.separator + value.toString();
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(imgPath);
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else {
                label.setText("Tidak ada gambar");
            }
        }
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HalamanAdmin();
            }
        });
    }
}