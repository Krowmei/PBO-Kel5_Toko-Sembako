import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginAdmin extends JFrame {

    public LoginAdmin() {
        setTitle("Toko Sembako");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(255, 204, 127)); // Warna background panel login
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelUsername = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(labelUsername, gbc);

        final JTextField fieldUsername = new JTextField(20);
        gbc.gridx = 1;
        panel.add(fieldUsername, gbc);

        JLabel labelPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(labelPassword, gbc);

        final JPasswordField fieldPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(fieldPassword, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        JButton backButton = new JButton("Kembali ke Beranda");
        gbc.gridy = 3;
        panel.add(backButton, gbc);

        final JLabel statusLabel = new JLabel("");
        gbc.gridy = 4;
        panel.add(statusLabel, gbc);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginAdmin());
    }
}
