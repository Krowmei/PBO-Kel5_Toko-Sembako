import javax.swing.*;
import java.awt.event.*;

public class HalamanCash extends JFrame {
    private JLabel labelKode, labelTotal, labelUang, labelKembalian;
    private JTextField fieldKode, fieldTotal, fieldUang, fieldKembalian;
    private JButton tombolKonfirmasi, tombolBatal;

    public HalamanCash(final String kodePesanan, final double total, final Runnable onSukses) {
        setTitle("Toko Sembako");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        labelKode = new JLabel("Kode Pesanan:");
        labelTotal = new JLabel("Total:");
        labelUang = new JLabel("Uang Dibayar:");
        labelKembalian = new JLabel("Kembalian:");

        fieldKode = new JTextField(kodePesanan);
        fieldTotal = new JTextField("Rp" + (int) total);
        fieldUang = new JTextField();
        fieldKembalian = new JTextField();

        fieldKode.setEditable(false);
        fieldTotal.setEditable(false);
        fieldKembalian.setEditable(false);

        tombolKonfirmasi = new JButton("Konfirmasi");
        tombolBatal = new JButton("Batal");

        labelKode.setBounds(30, 30, 100, 25);
        fieldKode.setBounds(140, 30, 200, 25);
        labelTotal.setBounds(30, 65, 100, 25);
        fieldTotal.setBounds(140, 65, 200, 25);
        labelUang.setBounds(30, 100, 100, 25);
        fieldUang.setBounds(140, 100, 200, 25);
        labelKembalian.setBounds(30, 135, 100, 25);
        fieldKembalian.setBounds(140, 135, 200, 25);
        tombolKonfirmasi.setBounds(70, 190, 110, 30);
        tombolBatal.setBounds(200, 190, 110, 30);

        add(labelKode); add(fieldKode);
        add(labelTotal); add(fieldTotal);
        add(labelUang); add(fieldUang);
        add(labelKembalian); add(fieldKembalian);
        add(tombolKonfirmasi); add(tombolBatal);

        fieldUang.addKeyListener(new KeyAdapter() {
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
            public void actionPerformed(ActionEvent ae) {
                try {
                    double dibayar = Double.parseDouble(fieldUang.getText());
                    if (dibayar < total) {
                        JOptionPane.showMessageDialog(HalamanCash.this, "Uang tidak cukup untuk membayar.");
                    } else {
                        double kembalian = dibayar - total;
                        String struk = "======= STRUK PEMBAYARAN =======\n" +
                                "Kode Pesanan : " + kodePesanan + "\n" +
                                String.format("Total        : Rp%.0f\n", total) +
                                String.format("Dibayar      : Rp%.0f\n", dibayar) +
                                String.format("Kembalian    : Rp%.0f\n", kembalian) +
                                "Status       : LUNAS\n" +
                                "================================";
                        JOptionPane.showMessageDialog(HalamanCash.this, struk, "Struk Pembayaran", JOptionPane.INFORMATION_MESSAGE);
                        onSukses.run();
                        dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(HalamanCash.this, "Masukkan angka yang valid untuk uang dibayar.");
                }
            }
        });

        tombolBatal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });

        setVisible(true);
    }
}
