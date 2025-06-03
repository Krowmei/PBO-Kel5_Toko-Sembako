import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StrukPembayaran extends JDialog {

    public StrukPembayaran(JFrame parent, String kodePesanan, double total, double dibayar, double kembalian) {
        super(parent, "Struk Pembayaran", true);
        setSize(350, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 248, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 76), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel title = new JLabel("STRUK PEMBAYARAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel labelKode = new JLabel("Kode Pesanan:");
        labelKode.setFont(labelFont);
        panel.add(labelKode, gbc);

        gbc.gridx = 1;
        JLabel valueKode = new JLabel(kodePesanan);
        valueKode.setFont(valueFont);
        panel.add(valueKode, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel labelTotal = new JLabel("Total:");
        labelTotal.setFont(labelFont);
        panel.add(labelTotal, gbc);

        gbc.gridx = 1;
        JLabel valueTotal = new JLabel(String.format("Rp%,.0f", total));
        valueTotal.setFont(valueFont);
        panel.add(valueTotal, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel labelDibayar = new JLabel("Dibayar:");
        labelDibayar.setFont(labelFont);
        panel.add(labelDibayar, gbc);

        gbc.gridx = 1;
        JLabel valueDibayar = new JLabel(String.format("Rp%,.0f", dibayar));
        valueDibayar.setFont(valueFont);
        panel.add(valueDibayar, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel labelKembalian = new JLabel("Kembalian:");
        labelKembalian.setFont(labelFont);
        panel.add(labelKembalian, gbc);

        gbc.gridx = 1;
        JLabel valueKembalian = new JLabel(String.format("Rp%,.0f", kembalian));
        valueKembalian.setFont(valueFont);
        panel.add(valueKembalian, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel labelStatus = new JLabel("Status:");
        labelStatus.setFont(labelFont);
        panel.add(labelStatus, gbc);

        gbc.gridx = 1;
        JLabel valueStatus = new JLabel("LUNAS");
        valueStatus.setFont(valueFont);
        valueStatus.setForeground(new Color(0, 153, 76));
        panel.add(valueStatus, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton okButton = new JButton("OK");
        okButton.setBackground(new Color(0, 153, 76));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setPreferredSize(new Dimension(120, 35));
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panel.add(okButton, gbc);

        setContentPane(panel);
    }
}
