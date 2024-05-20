import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GUI {
    private JFrame frame;
    private int numPlayers;
    private JPanel arrowPanel;
    private JPanel profilePanel;
    private JLabel castleNameLabel;
    private JLabel profileImageLabel;
    private JTextField textField;
    private double currentRotationAngle = 0.0;

    public GUI(int numPlayers) {
        this.numPlayers = numPlayers;
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        arrowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                Path2D.Double arrowShape = new Path2D.Double();
                arrowShape.moveTo(centerX, centerY - 50); // Top
                arrowShape.lineTo(centerX - 20, centerY + 20); // Bottom left
                arrowShape.lineTo(centerX + 20, centerY + 20); // Bottom right
                arrowShape.closePath();

                g2d.rotate(Math.toRadians(currentRotationAngle), centerX, centerY);
                g2d.setColor(Color.RED);
                g2d.fill(arrowShape);
            }
        };
        frame.getContentPane().add(arrowPanel, BorderLayout.CENTER);

        textField = new JTextField(10);
        textField.setHorizontalAlignment(JTextField.CENTER);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);

        profilePanel = new JPanel(new BorderLayout());
        castleNameLabel = new JLabel("Castle Name");
        castleNameLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(castleNameLabel, BorderLayout.NORTH);

        profileImageLabel = new JLabel("Profile Image");
        //profileImageLabel.setHorizontalAlignment(JLabel.CENTER);
        profileImageLabel.setVerticalAlignment(JLabel.EAST);
        profilePanel.add(profileImageLabel, BorderLayout.CENTER);

        frame.getContentPane().add(profilePanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void rotateArrow(double angle) {
        currentRotationAngle += angle;
        arrowPanel.repaint();
    }

    public static void main(String[] args) {
        int numPlayers = 4;
        GUI window = new GUI(numPlayers);

        while (true) {
            double rotationAngle = 360.0 / numPlayers;
            window.rotateArrow(rotationAngle);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
