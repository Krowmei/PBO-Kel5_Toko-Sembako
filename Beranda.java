import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Beranda extends JFrame {

    public Beranda() {
        setTitle("Beranda"); 
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel backgroundPanel = new BackgroundPanel("img/BGberanda.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel panelContent = new JPanel();
        panelContent.setBackground(Color.WHITE);
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panelContent.setOpaque(true);
        panelContent.setMaximumSize(new Dimension(400, 300));

        JLabel titleLabel = new JLabel("Toko Sembako");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton masukButton = new JButton("Yuk, belanja");
        masukButton.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        masukButton.setPreferredSize(new Dimension(250, 50));
        masukButton.setMaximumSize(new Dimension(250, 50));
        masukButton.setBackground(new Color(0, 153, 76));
        masukButton.setForeground(Color.WHITE);
        masukButton.setFocusPainted(false);
        masukButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        masukButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        masukButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new HalamanUtamaPelanggan();
                dispose();
            }
        });

        panelContent.add(titleLabel);
        panelContent.add(Box.createRigidArea(new Dimension(0, 30)));
        panelContent.add(masukButton);

        wrapper.add(panelContent);
        backgroundPanel.add(wrapper, BorderLayout.CENTER);

        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomRightPanel.setOpaque(false);

        ImageIcon adminIcon = new ImageIcon("img/iconAdmin.jpg");
        JButton adminButton = new JButton(adminIcon);
        adminButton.setPreferredSize(new Dimension(50, 50));
        adminButton.setContentAreaFilled(false);
        adminButton.setBorderPainted(false);
        adminButton.setFocusPainted(false);
        adminButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        adminButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                adminButton.setContentAreaFilled(true);
                adminButton.setBackground(new Color(200, 200, 200, 100));
            }

            public void mouseExited(MouseEvent e) {
                adminButton.setContentAreaFilled(false);
            }
        });

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginAdmin();
                dispose();
            }
        });

        bottomRightPanel.add(adminButton);
        backgroundPanel.add(bottomRightPanel, BorderLayout.SOUTH);
        setVisible(true);
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
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Beranda();
            }
        });
    }
}
