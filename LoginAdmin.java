import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginAdmin extends JFrame {

    public LoginAdmin() {
        setTitle("Halaman Login Admin");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel loginWrapper = new JPanel(new GridBagLayout());
        loginWrapper.setBackground(new Color(255, 248, 240));
        loginWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 76), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelTitle = new JLabel("LOGIN ADMIN");
        labelTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginWrapper.add(labelTitle, gbc);

        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginWrapper.add(labelUsername, gbc);

        final JTextField fieldUsername = new JTextField(20);
        gbc.gridx = 1;
        loginWrapper.add(fieldUsername, gbc);

        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginWrapper.add(labelPassword, gbc);

        final JPasswordField fieldPassword = new JPasswordField(20);
        gbc.gridx = 1;
        loginWrapper.add(fieldPassword, gbc);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Kembali ke Beranda");

        final JLabel statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.RED);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        Color buttonColor = new Color(0, 153, 76);

        JButton[] buttons = { loginButton, backButton };
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(buttonFont);
            buttons[i].setBackground(buttonColor);
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFocusPainted(false);
        }

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginWrapper.add(loginButton, gbc);

        gbc.gridy = 4;
        loginWrapper.add(backButton, gbc);

        gbc.gridy = 5;
        loginWrapper.add(statusLabel, gbc);

        backgroundPanel.add(loginWrapper);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = fieldUsername.getText();
                String password = new String(fieldPassword.getPassword());

                try {
                    Connection conn = Koneksi.getConnection();
                    String query = "SELECT * FROM admin WHERE username=? AND password=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(LoginAdmin.this, "Login berhasil!");
                        new HalamanUtamaAdmin();
                        dispose();
                    } else {
                        statusLabel.setText("Username atau password salah.");
                    }

                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Terjadi kesalahan saat login.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Beranda();
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
                new LoginAdmin();
            }
        });
    }
}
