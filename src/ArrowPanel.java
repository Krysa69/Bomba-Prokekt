import javax.swing.*;
import java.awt.*;

class ArrowPanel extends JPanel {
    private double angle;

    public ArrowPanel(double initialAngle) {
        this.angle = initialAngle;
        setPreferredSize(new Dimension(200, 200));
    }

    public void rotateArrow(double newAngle) {
        this.angle = newAngle;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int arrowLength = Math.min(getWidth(), getHeight()) / 2;
        int arrowWidth = arrowLength / 4;

        g2d.translate(centerX, centerY);
        g2d.rotate(angle);

        g2d.setColor(Color.RED);
        int[] xPoints = {0, -arrowWidth, arrowWidth};
        int[] yPoints = {-arrowLength, arrowLength / 2, arrowLength / 2};
        g2d.fillPolygon(xPoints, yPoints, 3);

        g2d.rotate(-angle);
        g2d.translate(-centerX, -centerY);
    }
}
