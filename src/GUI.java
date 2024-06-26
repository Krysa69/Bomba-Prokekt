import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The GUI class represents the main user interface for the Word Bomb game.
 * It initializes the game components, manages player interactions, and handles the game flow.
 */
public class GUI extends JFrame {
    ArrowPanel arrowPanel;
    List<Player> players;
    List<JPanel> cells;
    JTextField answerField;
    JTextArea writingArea;
    JButton confirmButton;
    JLabel diceResultLabel;
    JLabel syllableLabel;
    GameController gameController;
    Dice currentDice;
    String currentSyllable;
    static final int ARROW_PANEL_INDEX = (9 * 4) + 4;
    static final int BUTTON_PANEL_INDEX = (9 * 5) + 4;

    /**
     * Constructs a new GUI instance with the specified number of players.
     */
    public GUI(int numOfPlayers) {
        // Set up the main window properties
        setTitle("Word Bomb");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the player list, cell list, and game controller
        players = new ArrayList<>();
        cells = new ArrayList<>();
        gameController = new GameController();

        // Determine the initial dice roll and syllable
        currentDice = gameController.diceThrow();
        currentSyllable = gameController.selectRandomSyllable();

        // Create the main panel with a 9x9 grid layout
        JPanel mainPanel = new JPanel(new GridLayout(9, 9));
        int cellWidth = 1000 / 9;
        int cellHeight = 1000 / 9;

        // Create and arrange 81 cells in the main panel
        for (int i = 0; i < 81; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cells.add(cell);
            mainPanel.add(cell);
        }

        // Create player instances based on the number of players
        for (int i = 0; i < numOfPlayers; i++) {
            players.add(createPlayer(this));
        }

        // Configure and place the arrow panel at the designated cell index
        arrowPanel = new ArrowPanel(getRotationAngle(numOfPlayers));
        cells.get(ARROW_PANEL_INDEX).add(arrowPanel, BorderLayout.CENTER);

        // Set up the text area for writing input
        writingArea = new JTextArea();
        writingArea.setLineWrap(true);
        writingArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(writingArea);

        // Configure and set up the confirm button
        confirmButton = new JButton("Confirm");
        confirmButton.setPreferredSize(new Dimension(cellWidth, 30));
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConfirmButtonClick();
            }
        });

        diceResultLabel = new JLabel("Dice: " + currentDice);
        syllableLabel = new JLabel("Syllable: " + currentSyllable);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(diceResultLabel);
        infoPanel.add(syllableLabel);
        infoPanel.add(confirmButton);

        cells.get(BUTTON_PANEL_INDEX).setLayout(new BorderLayout());
        cells.get(BUTTON_PANEL_INDEX).add(scrollPane, BorderLayout.CENTER);
        cells.get(BUTTON_PANEL_INDEX).add(infoPanel, BorderLayout.SOUTH);

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
        updatePlayerPositions();
    }

    /**
     * Creates a new Player instance based on user input.
     * Returns A new Player instance with the selected profile picture and name, or null if no selection was made.
     */
    private Player createPlayer(JFrame parentFrame) {
        String[] playerInfo = GUI_pfp.selectProfilePicture(parentFrame);
        if (playerInfo[0] != null && playerInfo[1] != null) {
            return new Player(playerInfo[0], playerInfo[1]);
        }
        return null;
    }

    /**
     * Determines the rotation angle for the arrow panel based on the number of players.
     * Returns The rotation angle for the arrow panel.
     */
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

    /**
     * Adds a player to a specific cell on the game board.
     */
    private void addPlayerToCell(List<JPanel> cells, int cellRow, int cellColumn, Player player, int cellWidth, int cellHeight) {
        if (player == null) return;
        int cellIndex = cellRow * 9 + cellColumn;
        JPanel cell = cells.get(cellIndex);
        cell.removeAll();
        ImageIcon icon = createImageIcon("/resources/" + player.getProfilePicturePath(), cellWidth, cellHeight);
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cell.add(imageLabel, BorderLayout.CENTER);
        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cell.add(nameLabel, BorderLayout.SOUTH);
        cell.revalidate();
        cell.repaint();
    }

    /**
     * Creates an ImageIcon from the specified path, scaled to the given dimensions.
     */
    private ImageIcon createImageIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Handles the action when the confirm button is clicked.
     * Notifies any waiting threads that the button has been clicked.
     */
    public void handleConfirmButtonClick() {
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * Removes a player from the game and updates the game board accordingly.
     */
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
        changeDiceAndSyllable();
    }

    /**
     * Updates the positions of all players on the game board.
     */
    private void updatePlayerPositions() {
        int cellWidth = 1000 / 9;
        int cellHeight = 1000 / 9;
        clearPlayerCells();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int[] cellCoordinates = getPlayerCellCoordinates(i, players.size());
            addPlayerToCell(cells, cellCoordinates[0], cellCoordinates[1], player, cellWidth, cellHeight);
        }
        restoreNonPlayerComponents();
    }

    /**
     * Clears the content of all cells except the arrow panel and button panel.
     */
    private void clearPlayerCells() {
        for (int i = 0; i < cells.size(); i++) {
            if (i != ARROW_PANEL_INDEX && i != BUTTON_PANEL_INDEX) {
                cells.get(i).removeAll();
                cells.get(i).revalidate();
                cells.get(i).repaint();
            }
        }
    }

    /**
     * Restores the components in the arrow panel and button panel after updating player positions.
     */
    private void restoreNonPlayerComponents() {
        cells.get(ARROW_PANEL_INDEX).removeAll();
        cells.get(ARROW_PANEL_INDEX).add(arrowPanel, BorderLayout.CENTER);
        cells.get(ARROW_PANEL_INDEX).revalidate();
        cells.get(ARROW_PANEL_INDEX).repaint();

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(diceResultLabel);
        infoPanel.add(syllableLabel);
        infoPanel.add(confirmButton);

        cells.get(BUTTON_PANEL_INDEX).removeAll();
        cells.get(BUTTON_PANEL_INDEX).add(new JScrollPane(writingArea), BorderLayout.CENTER);
        cells.get(BUTTON_PANEL_INDEX).add(infoPanel, BorderLayout.SOUTH);
        cells.get(BUTTON_PANEL_INDEX).revalidate();
        cells.get(BUTTON_PANEL_INDEX).repaint();
    }

    /**
     * Changes the current dice result and syllable to new random values.
     */
    private void changeDiceAndSyllable() {
        currentDice = gameController.diceThrow();
        currentSyllable = gameController.selectRandomSyllable();
        diceResultLabel.setText("Dice: " + currentDice);
        syllableLabel.setText("Syllable: " + currentSyllable);
    }

    /**
     * Returns the coordinates of the cell for the specified player based on the number of players.
     */
    private int[] getPlayerCellCoordinates(int playerIndex, int numOfPlayers) {
        switch (numOfPlayers) {
            case 1:
                return new int[]{0, 4};
            case 2:
                if (playerIndex == 0) return new int[]{0, 4};
                else return new int[]{8, 4};
            case 3:
                if (playerIndex == 0) return new int[]{0, 4};
                else if (playerIndex == 1) return new int[]{8, 8};
                else return new int[]{8, 0};
            case 4:
                if (playerIndex == 0) return new int[]{0, 4};
                else if (playerIndex == 1) return new int[]{4, 8};
                else if (playerIndex == 2) return new int[]{8, 4};
                else return new int[]{4, 0};
            default:
                return new int[]{0, 0};
        }
    }

    /**
     * Displays a dialog for selecting the number of players.
     */
    public static int playerNumberChoosing() {
        String[] options = {"1", "2", "3", "4"};
        int result = JOptionPane.showOptionDialog(null, "Choose number of player:", "Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (result != JOptionPane.CLOSED_OPTION) {
            return Integer.parseInt(options[result]);
        } else {
            return -1;
        }
    }
}
