import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class HalamanAdmin extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfNama, tfHarga, tfStok;
    private JLabel lblGambarPath;
    private Connection conn;

    public HalamanAdmin() {
        setTitle("Halaman Admin");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectDB();

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        model = new DefaultTableModel(new Object[]{"ID", "Nama", "Harga", "Stok", "Gambar"}, 0);

        table = new JTable(model) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 4) return new GambarRenderer();
                return super.getCellRenderer(row, column);
            }
        };

        table.setRowHeight(70);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);
        table.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        tfNama = new JTextField();
        tfHarga = new JTextField();
        tfStok = new JTextField();
        JButton btnPilihGambar = new JButton("Pilih Gambar");
        lblGambarPath = new JLabel("Belum ada gambar dipilih");

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 13);
        for(Component c : new Component[]{tfNama, tfHarga, tfStok, btnPilihGambar}) {
            c.setFont(inputFont);
        }

        Color green = new Color(0, 153, 76);
        btnPilihGambar.setBackground(green);
        btnPilihGambar.setForeground(Color.WHITE);
        btnPilihGambar.setFocusPainted(false);
        btnPilihGambar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("Nama Barang:"));
        inputPanel.add(tfNama);
        inputPanel.add(new JLabel("Harga:"));
        inputPanel.add(tfHarga);
        inputPanel.add(new JLabel("Stok:"));
        inputPanel.add(tfStok);
        inputPanel.add(btnPilihGambar);
        inputPanel.add(lblGambarPath);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(new Color(255, 255, 255, 230));
        contentWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(green, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        contentWrapper.add(inputPanel);
        contentWrapper.add(Box.createVerticalStrut(10));
        contentWrapper.add(scrollPane);
        contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        contentWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        wrapperPanel.add(contentWrapper, BorderLayout.CENTER);

        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(255, 255, 255, 220));

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnKembali = new JButton("Kembali");

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension btnSize = new Dimension(140, 35);

        for (JButton btn : new JButton[]{btnTambah, btnUbah, btnHapus, btnKembali}) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(btnSize);
            btn.setBackground(green);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            buttonPanel.add(btn);
        }

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnPilihGambar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(HalamanAdmin.this) == JFileChooser.APPROVE_OPTION) {
                    lblGambarPath.setText(fc.getSelectedFile().getName());
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
        table.clearSelection();
    }

    class GambarRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = new JLabel();
            if (value != null && !value.toString().isEmpty()) {
                ImageIcon icon = new ImageIcon("img/" + value.toString());
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
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
        }
    }
}
