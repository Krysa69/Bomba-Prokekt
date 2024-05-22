import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {
    public GUI(int numOfPlayers) {
        setTitle("Grid Window");
        setSize(1001, 1001);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(9, 9));
        int cellWidth = 1000 / 9;
        int cellHeight = 1000 / 9;

        List<JPanel> cells = new ArrayList<>();

        for (int i = 0; i < 81; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cells.add(cell);
            mainPanel.add(cell);
        }
        switch (numOfPlayers) {
            case 1:
                addPlayerToCell(cells, 0, 4, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                break;
            case 2:
                addPlayerToCell(cells, 0, 4, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 4, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                break;
            case 3:
                addPlayerToCell(cells, 0, 4, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 8, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 0, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                break;
            case 4:
                addPlayerToCell(cells, 0, 4, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                addPlayerToCell(cells, 4, 8, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 4, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                addPlayerToCell(cells, 4, 0, GUI_pfp.selectProfilePicture(this), cellWidth, cellHeight);
                break;
            default:
                System.out.println("Error");
                break;
        }

        add(mainPanel);

        setVisible(true);
    }

    private void addPlayerToCell(List<JPanel> cells, int cellRow, int cellColumn, String[] array, int cellWidth, int cellHeight) {
        int cellIndex = cellRow * 9 + cellColumn;
        JPanel cell = cells.get(cellIndex);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/" + array[0]));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(cellWidth, cellHeight, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(icon);
        cell.add(imageLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(array[1]);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cell.add(nameLabel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI(3));
    }
}
