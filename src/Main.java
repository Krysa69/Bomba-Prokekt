import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        int numOfPlayers = GUI.playerNumberChoosing();
        if (numOfPlayers > 0) {
            GUI gui = new GUI(numOfPlayers);

            // Main game loop
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
                        gui.handleConfirmButtonClick(); // Handle timeout
                    });
                    timer.setRepeats(false);
                    timer.start();

                    synchronized (gui) {
                        try {
                            gui.wait(10000); // Wait for either user input or timeout
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    timer.stop();
                    String userInput = gui.writingArea.getText().trim();

                    if (userInput.isEmpty()) {
                        JOptionPane.showMessageDialog(gui, "No answer! Player " + currentPlayer.getName() + " is out.");
                        gui.removePlayer(i);
                        i--; // Adjust index due to player removal
                    } else {
                        boolean isValidWord = gui.gameController.isWordInFile(userInput, gui.currentDice, gui.currentSyllable);
                        if (!isValidWord) {
                            JOptionPane.showMessageDialog(gui, "Incorrect word! Player " + currentPlayer.getName() + " is out.");
                            gui.removePlayer(i);
                            i--; // Adjust index due to player removal
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
