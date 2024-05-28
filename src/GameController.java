import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameController {
    List<Player> activePlayers;
    List<Dice> possibleThrows;
    int roundCount = 0;
    Scanner sc = new Scanner(System.in);
    Random rd = new Random();

    public GameController() {
        activePlayers = new ArrayList<>();
        possibleThrows = new ArrayList<>();
        addThrows();
    }

    public Dice diceThrow() {
        int random = rd.nextInt(3);
        return possibleThrows.get(random);
    }

    public void addThrows() {
        possibleThrows.add(Dice.FIRST);
        possibleThrows.add(Dice.MIDDLE);
        possibleThrows.add(Dice.LAST);
    }

    public static void emptyUsedWordsFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("UsedWords.txt"))) {
            bw.write("");
        } catch (IOException e) {
            throw new RuntimeException("Error emptying UsedWords.txt: " + e.getMessage());
        }
    }

    public String selectRandomSyllable() {
        File syllableFile = new File("Syllables.txt");
        if (!syllableFile.exists()) {
            JOptionPane.showMessageDialog(null, "Syllables.txt file not found. Please ensure the file is in the correct location.");
            return null;
        }
        List<String> syllables = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(syllableFile))) {
            String syllable;
            while ((syllable = br.readLine()) != null) {
                syllables.add(syllable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        return syllables.isEmpty() ? null : syllables.get(random.nextInt(syllables.size()));
    }

    public boolean isWordInFile(String searchedWord, Dice dice, String syllable) {
        try (BufferedReader br = new BufferedReader(new FileReader("UsedWords.txt"));
             BufferedReader bru = new BufferedReader(new FileReader("Words.txt"));
             BufferedWriter bw = new BufferedWriter(new FileWriter("UsedWords.txt", true))) {

            String line1;
            String line2;
            boolean foundInUsedWords = false;
            boolean foundInWords = false;

            while ((line1 = br.readLine()) != null) {
                if (line1.contains(searchedWord)) {
                    System.out.println("This word was already used");
                    foundInUsedWords = true;
                    break;
                }
            }

            while ((line2 = bru.readLine()) != null) {
                if (line2.contains(searchedWord)) {
                    foundInWords = true;
                    break;
                }
            }

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
                    bw.write(searchedWord);
                    bw.newLine();
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Error reading or writing file: " + e.getMessage());
        }
    }
}
