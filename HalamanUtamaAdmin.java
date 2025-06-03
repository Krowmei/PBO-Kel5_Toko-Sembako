import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HalamanUtamaAdmin extends JFrame {

    public HalamanUtamaAdmin() {
        setTitle("Halaman Utama Admin");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(new Color(255, 248, 240));
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 76), 3),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);
        Color buttonColor = new Color(0, 153, 76);
        Dimension buttonSize = new Dimension(250, 50);

        JButton btnCRUDBarang = new JButton("Kelola Data Barang");
        JButton btnHalamanKasir = new JButton("Konfirmasi Pembayaran");
        JButton btnLogout = new JButton("Logout");

        JButton[] buttons = { btnCRUDBarang, btnHalamanKasir, btnLogout };
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(buttonFont);
            buttons[i].setBackground(buttonColor);
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(buttonSize);

            gbc.gridy = i;
            gbc.gridx = 0;
            menuPanel.add(buttons[i], gbc);
        }

        backgroundPanel.add(menuPanel);

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
                Session.adminId = -1;
                Session.adminUsername = null;
                dispose();
                new LoginAdmin();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HalamanUtamaAdmin();
            }
        });
    }
}

class Session {
    public static int adminId = -1;
    public static String adminUsername = null;
}
