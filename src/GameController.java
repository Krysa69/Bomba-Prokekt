import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The GameController class manages the logic of the word game.
 * It handles dice throws, word selection, and file operations.
 */
public class GameController {
    /**
     * List of active players
     */
    private List<Player> activePlayers;
    /**
     * List of possible outcomes of dice throws
     */
    private List<Dice> possibleThrows;
    private Scanner sc = new Scanner(System.in);
    private Random rd = new Random();

    /**
     * Constructs a GameController object.
     * Initializes the activePlayers and possibleThrows lists,
     * and adds predefined dice outcomes.
     */
    public GameController() {
        activePlayers = new ArrayList<>();
        possibleThrows = new ArrayList<>();
        addThrows();
    }

    /**
     * Simulates a dice throw and returns the outcome.
     *
     * @return The outcome of the dice throw
     */
    public Dice diceThrow() {
        /**
         * Generate a random number between 0 and 2 (inclusive)
         */
        int random = rd.nextInt(3);
        /**
         * Return the outcome of the dice throw from the possibleThrows list
         */
        return possibleThrows.get(random);
    }

    /**
     * Adds predefined dice outcomes to the possibleThrows list.
     */
    public void addThrows() {
        possibleThrows.add(Dice.FIRST);
        possibleThrows.add(Dice.MIDDLE);
        possibleThrows.add(Dice.LAST);
    }

    /**
     * Empties the contents of the "UsedWords.txt" file.
     * Throws a RuntimeException if an error occurs.
     */
    public static void emptyUsedWordsFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/files/UsedWords.txt"))) {
            /**
             * Write an empty string to the UsedWords.txt file
             */
            bw.write("");
        } catch (IOException e) {
            throw new RuntimeException("Error emptying UsedWords.txt: " + e.getMessage());
        }
    }

    /**
     * Selects a random syllable from the "Syllables.txt" file.
     * Returns A random syllable or null if file not found or empty
     */
    public String selectRandomSyllable() {
        /**
         * Read the Syllables.txt file from the resources folder
         */
        InputStream inputStream = getClass().getResourceAsStream("/files/Syllables.txt");
        if (inputStream == null) {
            JOptionPane.showMessageDialog(null, "Syllables.txt file not found. Please ensure the file is in the correct location.");
            return null;
        }
        List<String> syllables = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String syllable;
            /**
             * Read each line of the file and add it to the list of syllables
             */
            while ((syllable = br.readLine()) != null) {
                syllables.add(syllable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * Select a random syllable from the list and return it
         */

        Random random = new Random();
        return syllables.isEmpty() ? null : syllables.get(random.nextInt(syllables.size()));
    }

    /**
     * Checks if a word is present in the "UsedWords.txt" and "Words.txt" files
     * and writes it to "UsedWords.txt" if valid.
     * <p>
     * Returns true if the word is valid and written to file, false otherwise
     */
    public boolean isWordInFile(String searchedWord, Dice dice, String syllable) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/files/UsedWords.txt")));
             BufferedReader bru = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/files/Words.txt")));
             BufferedWriter bw = new BufferedWriter(new FileWriter("src/files/UsedWords.txt", true))) {

            String line1;
            String line2;
            boolean foundInUsedWords = false;
            boolean foundInWords = false;

            /**
             * Check if the searched word is present in the UsedWords.txt file
             */
            while ((line1 = br.readLine()) != null) {
                if (line1.contains(searchedWord)) {
                    System.out.println("This word was already used");
                    foundInUsedWords = true;
                    break;
                }
            }

            /**
             * Check if the searched word is present in the Words.txt file
             */
            while ((line2 = bru.readLine()) != null) {
                if (line2.contains(searchedWord)) {
                    foundInWords = true;
                    break;
                }
            }
            /**
             * Check if the word's position is valid based on the dice position and syllable
             */
            if (!foundInUsedWords && foundInWords) {
                boolean isPositionValid = false;

                switch (dice) {
                    case FIRST:
                        isPositionValid = foundInWords && line2.startsWith(syllable);
                        break;
                    case MIDDLE:
                        isPositionValid = foundInWords && line2.contains(syllable);
                        break;
                    case LAST:
                        isPositionValid = foundInWords && line2.endsWith(syllable);
                        break;
                }

                if (isPositionValid) {
                    /**
                     * Write the word to UsedWords.txt if it meets the criteria and return true
                      */
                    bw.write(searchedWord);
                    bw.newLine();
                    return true;
                }
            }
            /**
             * Return false if the word is not valid or cannot be written to file
              */
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Error reading or writing file: " + e.getMessage());
        }
    }
}
