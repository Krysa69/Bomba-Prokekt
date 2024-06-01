import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) {
        // Prompt the user to select the number of players
        int numOfPlayers = GUI.playerNumberChoosing();
        if (numOfPlayers > 0) {
            // Initialize the GUI with the selected number of players
            GUI gui = new GUI(numOfPlayers);

            // Main game loop
            while (gui.players.size() > 1) {
                for (int i = 0; i < gui.players.size(); i++) {
                    Player currentPlayer = gui.players.get(i);

                    // Rotate the arrow to point to the current player
                    gui.arrowPanel.rotateArrow(getRotationAngleForPlayer(i, gui.players.size()));

                    // Update the dice result and syllable labels
                    gui.diceResultLabel.setText("Dice: " + gui.currentDice);
                    gui.syllableLabel.setText("Syllable: " + gui.currentSyllable);

                    // Clear and focus the writing area
                    gui.writingArea.setText("");
                    gui.writingArea.requestFocus();

                    // Set up a countdown latch and timer for the player's turn
                    CountDownLatch latch = new CountDownLatch(1);
                    Timer timer = new Timer(10000, e -> {
                        latch.countDown();
                        gui.handleConfirmButtonClick();
                    });
                    timer.setRepeats(false);
                    timer.start();

                    // Wait for the player's input or the timer to expire
                    synchronized (gui) {
                        try {
                            gui.wait(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Stop the timer
                    timer.stop();
                    String userInput = gui.writingArea.getText().trim();

                    // Check the player's input
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
                            // Update the dice result and syllable for the next turn
                            gui.currentDice = gui.gameController.diceThrow();
                            gui.currentSyllable = gui.gameController.selectRandomSyllable();
                            gui.diceResultLabel.setText("Dice: " + gui.currentDice);
                            gui.syllableLabel.setText("Syllable: " + gui.currentSyllable);
                        }
                    }

                    // Check if there is only one player left
                    if (gui.players.size() == 1) {
                        break;
                    }
                }
            }

            // Announce the winner
            JOptionPane.showMessageDialog(gui, "Game over! The winner is " + gui.players.get(0).getName());
        } else {
            System.out.println("No player selected. Exiting...");
        }

        // Empty the used words file
        GameController.emptyUsedWordsFile();
    }

    /**
     * Calculates the rotation angle for the arrow based on the current player index and the total number of players.
     */
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
