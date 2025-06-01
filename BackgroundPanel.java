import javax.swing.*;
import java.awt.*;


public class BackgroundPanel extends JPanel {
    private Color fillColor; 
    public BackgroundPanel(Color color) {
        this.fillColor = color; 
        setOpaque(true); 
                         
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass implementation for standard rendering.

        if (fillColor != null) {
            g.setColor(new Color(255, 204, 127)); // Atur warna pengisi
            g.fillRect(0, 0, getWidth(), getHeight()); // Isi seluruh area panel dengan warna
        }
    }
}
