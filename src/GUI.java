import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GUI extends JFrame {
    private ArrowPanel arrowPanel;
    private List<Player> players;
    private List<JPanel> cells;
    private JTextField answerField;
    private JTextArea writingArea;
    private JButton confirmButton;
    private JLabel diceResultLabel;
    private JLabel syllableLabel;
    private GameController gameController;
    private Dice currentDice;
    private String currentSyllable;
    private static final int ARROW_PANEL_INDEX = (9 * 4) + 4;
    private static final int BUTTON_PANEL_INDEX = (9 * 5) + 4;

    public GUI(int numOfPlayers) {
        setTitle("Word Bomb");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        players = new ArrayList<>();
        cells = new ArrayList<>();
        gameController = new GameController();


        currentDice = gameController.diceThrow();
        currentSyllable = gameController.selectRandomSyllable();

        JPanel mainPanel = new JPanel(new GridLayout(9, 9));
        int cellWidth = 1000 / 9;
        int cellHeight = 1000 / 9;

        for (int i = 0; i < 81; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cells.add(cell);
            mainPanel.add(cell);
        }


        for (int i = 0; i < numOfPlayers; i++) {
            players.add(createPlayer(this));
        }

        arrowPanel = new ArrowPanel(getRotationAngle(numOfPlayers));
        cells.get(ARROW_PANEL_INDEX).add(arrowPanel, BorderLayout.CENTER);

        writingArea = new JTextArea();
        writingArea.setLineWrap(true);
        writingArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(writingArea);

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

    private ImageIcon createImageIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void handleConfirmButtonClick() {
        synchronized (this) {
            this.notify();
        }
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
        changeDiceAndSyllable();
    }

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

    private void clearPlayerCells() {
        for (int i = 0; i < cells.size(); i++) {
            if (i != ARROW_PANEL_INDEX && i != BUTTON_PANEL_INDEX) {
                cells.get(i).removeAll();
                cells.get(i).revalidate();
                cells.get(i).repaint();
            }
        }
    }

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

    private void changeDiceAndSyllable() {
        currentDice = gameController.diceThrow();
        currentSyllable = gameController.selectRandomSyllable();
        diceResultLabel.setText("Dice: " + currentDice);
        syllableLabel.setText("Syllable: " + currentSyllable);
    }

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
            gui.gameController.emptyUsedWordsFile();
            while (gui.players.size() > 1) {
                for (int i = 0; i < gui.players.size(); i++) {
                    Player currentPlayer = gui.players.get(i);
                    gui.arrowPanel.rotateArrow(getRotationAngleForPlayer(i, gui.players.size()));
                    gui.diceResultLabel.setText("Dice: " + gui.currentDice);
                    gui.syllableLabel.setText("Syllable: " + gui.currentSyllable);

                    gui.writingArea.setText("");
                    gui.writingArea.requestFocus();

                    CountDownLatch latch = new CountDownLatch(1);
                    Timer timer = new Timer(10000, e -> {
                        latch.countDown();
                        gui.handleConfirmButtonClick();
                    });
                    timer.setRepeats(false);
                    timer.start();

                    synchronized (gui) {
                        try {
                            gui.wait(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    timer.stop();
                    String userInput = gui.writingArea.getText().trim();

                    if (userInput.isEmpty()) {
                        JOptionPane.showMessageDialog(gui, "No answer! Player " + currentPlayer.getName() + " is out.");
                        gui.removePlayer(i);
                        i--;
                    } else {
                        boolean isValidWord = gui.gameController.isWordInFile(userInput, gui.currentDice, gui.currentSyllable);
                        if (!isValidWord) {
                            JOptionPane.showMessageDialog(gui, "Incorrect word! Player " + currentPlayer.getName() + " is out.");
                            gui.removePlayer(i);
                            i--;
                        } else {
                            gui.currentDice = gui.gameController.diceThrow();
                            gui.currentSyllable = gui.gameController.selectRandomSyllable();
                            gui.diceResultLabel.setText("Dice: " + gui.currentDice);
                            gui.syllableLabel.setText("Syllable: " + gui.currentSyllable);
                        }
                    }

                    if (gui.players.size() == 1) {
                        break;
                    }
                }
            }

            JOptionPane.showMessageDialog(gui, "Game over! The winner is " + gui.players.get(0).getName());
        } else {
            System.out.println("No player selected. Exiting...");
        }
    }

    private static double getRotationAngleForPlayer(int playerIndex, int numOfPlayers) {
        switch (numOfPlayers) {
            case 1:
                return 0;
            case 2:
                return playerIndex == 0 ? 0 : Math.PI;
            case 3:
                return playerIndex * (2 * Math.PI / 3);
            case 4:
                return playerIndex * (Math.PI / 2);
            default:
                return 0;
        }
    }
}