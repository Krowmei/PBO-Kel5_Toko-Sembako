import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HalamanCash extends JFrame {

    private JLabel labelKode, labelTotal, labelUang, labelKembalian;
    private JTextField fieldKode, fieldTotal, fieldUang, fieldKembalian;
    private JButton tombolKonfirmasi, tombolBatal;

    public HalamanCash(final String kodePesanan, final double total, final Runnable onSukses) {
        setTitle("Halaman Cash");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(new Color(255, 248, 240));
        wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 76), 3),
            BorderFactory.createEmptyBorder(20, 20, 40, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        labelKode = new JLabel("Kode Pesanan:");
        labelKode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        wrapperPanel.add(labelKode, gbc);

        fieldKode = new JTextField(kodePesanan, 20);
        fieldKode.setEditable(false);
        gbc.gridx = 1;
        wrapperPanel.add(fieldKode, gbc);

        labelTotal = new JLabel("Total:");
        labelTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        wrapperPanel.add(labelTotal, gbc);

        fieldTotal = new JTextField("Rp" + (int) total, 20);
        fieldTotal.setEditable(false);
        gbc.gridx = 1;
        wrapperPanel.add(fieldTotal, gbc);

        labelUang = new JLabel("Uang Dibayar:");
        labelUang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        wrapperPanel.add(labelUang, gbc);

        fieldUang = new JTextField(20);
        gbc.gridx = 1;
        wrapperPanel.add(fieldUang, gbc);

        labelKembalian = new JLabel("Kembalian:");
        labelKembalian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        wrapperPanel.add(labelKembalian, gbc);

        fieldKembalian = new JTextField(20);
        fieldKembalian.setEditable(false);
        gbc.gridx = 1;
        wrapperPanel.add(fieldKembalian, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(255, 248, 240));

        tombolKonfirmasi = new JButton("Konfirmasi");
        tombolBatal = new JButton("Batal");

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        Color buttonColor = new Color(0, 153, 76);

        JButton[] buttons = { tombolKonfirmasi, tombolBatal };
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(buttonFont);
            buttons[i].setBackground(buttonColor);
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(140, 35));
        }

        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(0, 10, 0, 10);
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        buttonPanel.add(tombolKonfirmasi, gbcBtn);

        gbcBtn.gridx = 1;
        buttonPanel.add(tombolBatal, gbcBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapperPanel.add(buttonPanel, gbc);

        backgroundPanel.add(wrapperPanel);

        fieldUang.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double uang = Double.parseDouble(fieldUang.getText());
                    double kembalian = uang - total;
                    fieldKembalian.setText("Rp" + (kembalian >= 0 ? (int) kembalian : 0));
                } catch (NumberFormatException ex) {
                    fieldKembalian.setText("Rp0");
                }
            }
        });

        tombolKonfirmasi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double dibayar = Double.parseDouble(fieldUang.getText());
                    if (dibayar < total) {
                        JOptionPane.showMessageDialog(HalamanCash.this, "Uang tidak cukup untuk membayar.");
                    } else {
                        double kembalian = dibayar - total;
                        StrukPembayaran strukDialog = new StrukPembayaran(
                            HalamanCash.this, kodePesanan, total, dibayar, kembalian);
                        strukDialog.setVisible(true);
                        onSukses.run();
                        dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(HalamanCash.this, "Masukkan angka yang valid untuk uang dibayar.");
                }
            }
        });

        tombolBatal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
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
}
