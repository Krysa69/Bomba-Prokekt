import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {
    private ArrowPanel arrowPanel;
    private List<Player> players;
    private List<JPanel> cells;
    private JTextField answerField;

    public GUI(int numOfPlayers) {
        setTitle("Word Bomb");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        players = new ArrayList<>();
        cells = new ArrayList<>();
        JPanel mainPanel = new JPanel(new GridLayout(9, 9));
        int cellWidth = 1000 / 9;
        int cellHeight = 1000 / 9;

        for (int i = 0; i < 81; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cells.add(cell);
            mainPanel.add(cell);
        }

        switch (numOfPlayers) {
            case 1:
                players.add(createPlayer(this));
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                break;
            case 2:
                players.add(createPlayer(this));
                players.add(createPlayer(this));
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 4, players.get(1), cellWidth, cellHeight);
                break;
            case 3:
                players.add(createPlayer(this));
                players.add(createPlayer(this));
                players.add(createPlayer(this));
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 8, players.get(1), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 0, players.get(2), cellWidth, cellHeight);
                break;
            case 4:
                players.add(createPlayer(this));
                players.add(createPlayer(this));
                players.add(createPlayer(this));
                players.add(createPlayer(this));
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                addPlayerToCell(cells, 4, 8, players.get(1), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 4, players.get(2), cellWidth, cellHeight);
                addPlayerToCell(cells, 4, 0, players.get(3), cellWidth, cellHeight);
                break;
            default:
                System.out.println("Error");
                break;
        }

        arrowPanel = new ArrowPanel(getRotationAngle(numOfPlayers));
        int centerCellIndex = (9 * 4) + 4;
        cells.get(centerCellIndex).add(arrowPanel, BorderLayout.CENTER);
        arrowPanel.rotateArrow(Math.PI * 3 / 2);

        JPanel container = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(mainPanel, gbc);

        answerField = new JTextField();
        JPanel answerPanel = new JPanel(new BorderLayout());
        answerPanel.add(new JLabel("Answer: "), BorderLayout.WEST);
        answerPanel.add(answerField, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(answerPanel, gbc);

        add(container);
        setVisible(true);
    }

    private Player createPlayer(JFrame parentFrame) {
        String[] playerInfo = GUI_pfp.selectProfilePicture(parentFrame);
        if (playerInfo[0] != null && playerInfo[1] != null) {
            return new Player(playerInfo[0], playerInfo[1]);
        }
        return null;
    }

    private double getRotationAngle(int numOfPlayers) {
        switch (numOfPlayers) {
            case 1:
                return 0;
            case 2:
                return Math.PI / 2;
            case 3:
                return Math.PI;
            case 4:
                return 3 * Math.PI / 2;
            default:
                return 0;
        }
    }

    private void addPlayerToCell(List<JPanel> cells, int cellRow, int cellColumn, Player player, int cellWidth, int cellHeight) {
        if (player == null) return;

        int cellIndex = cellRow * 9 + cellColumn;
        JPanel cell = cells.get(cellIndex);

        ImageIcon icon = createImageIcon("/resources/" + player.getProfilePicturePath(), cellWidth, cellHeight);
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cell.add(imageLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cell.add(nameLabel, BorderLayout.SOUTH);
    }

    private ImageIcon createImageIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public void removePlayer(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= players.size()) return;

        Player player = players.get(playerIndex);
        for (JPanel cell : cells) {
            Component[] components = cell.getComponents();
            for (Component component : components) {
                if (component instanceof JLabel) {
                    JLabel label = (JLabel) component;
                    String labelText = label.getText();
                    if (labelText != null && labelText.equals(player.getName())) {
                        cell.removeAll();
                        cell.revalidate();
                        cell.repaint();
                        break;
                    }
                }
            }
        }
        players.remove(playerIndex);
        updatePlayerPositions();
    }

    private void updatePlayerPositions() {
        int numOfPlayers = players.size();
        int cellWidth = 1000 / 9;
        int cellHeight = 1000 / 9;

        for (JPanel cell : cells) {
            cell.removeAll();
            cell.revalidate();
            cell.repaint();
        }

        switch (numOfPlayers) {
            case 1:
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                break;
            case 2:
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 4, players.get(1), cellWidth, cellHeight);
                break;
            case 3:
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 8, players.get(1), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 0, players.get(2), cellWidth, cellHeight);
                break;
            case 4:
                addPlayerToCell(cells, 0, 4, players.get(0), cellWidth, cellHeight);
                addPlayerToCell(cells, 4, 8, players.get(1), cellWidth, cellHeight);
                addPlayerToCell(cells, 8, 4, players.get(2), cellWidth, cellHeight);
                addPlayerToCell(cells, 4, 0, players.get(3), cellWidth, cellHeight);
                break;
            default:
                break;
        }


        int centerCellIndex = (9 * 4) + 4;
        cells.get(centerCellIndex).add(arrowPanel, BorderLayout.CENTER);
    }

    public static int playerNumberChoosing() {
        String[] options = {"1", "2", "3", "4"};

        int result = JOptionPane.showOptionDialog(null, "Choose number of player:", "Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (result != JOptionPane.CLOSED_OPTION) {
            return Integer.parseInt(options[result]);
        } else {
            return -1;
        }
    }

    public static void main(String[] args) {
        int numOfPlayers = playerNumberChoosing();
        if (numOfPlayers > 0) {
            GUI gui = new GUI(numOfPlayers);

            Timer timer = new Timer(5000, e -> gui.removePlayer(0));
            timer.setRepeats(false);
            timer.start();

        } else {
            System.out.println("No player selected. Exiting...");
        }
    }
}
