import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HalamanUtamaAdmin extends JFrame {
    public HalamanUtamaAdmin() {
        setTitle("Toko Sembako");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(255, 204, 127));

        JButton btnCRUDBarang = new JButton("Kelola Data Barang");
        JButton btnHalamanKasir = new JButton("Konfirmasi Pembayaran (Kasir)");
        JButton btnLogout = new JButton("Logout");

        btnCRUDBarang.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HalamanAdmin();
            }
        });

        btnHalamanKasir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HalamanKasir(HalamanUtamaAdmin.this);
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset session admin
                Session.adminId = -1;
                Session.adminUsername = null;

                dispose();
                new LoginAdmin();
            }
        });

        panel.add(btnCRUDBarang);
        panel.add(btnHalamanKasir);
        panel.add(btnLogout);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HalamanUtamaAdmin();
            }
        });
    }
}

// Class Session disatukan di bawah ini (tidak public)
class Session {
    public static int adminId = -1;
    public static String adminUsername = null;
}
