import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private GameController gameController;
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        gameController = new GameController();
    }

    @Test
    public void testDiceThrow() {
        Dice result = gameController.diceThrow();
        assertTrue(result == Dice.FIRST || result == Dice.MIDDLE || result == Dice.LAST, "Dice throw should return a valid Dice value");
    }

    @Test
    public void testEmptyUsedWordsFile() throws IOException {
        Path usedWordsFile = tempDir.resolve("UsedWords.txt");
        Files.writeString(usedWordsFile, "Some content");

        GameController.emptyUsedWordsFile();

        assertFalse(Files.readString(usedWordsFile).isEmpty(), "UsedWords.txt should be empty after calling emptyUsedWordsFile");
    }

    @Test
    public void testSelectRandomSyllable() throws IOException {
        InputStream inputStream = GameControllerTest.class.getResourceAsStream("/files/Syllables.txt");
        assertNotNull(inputStream, "Failed to load syllables file");

        List<String> syllables = readLinesFromInputStream(inputStream);

        String syllable = gameController.selectRandomSyllable();
        assertTrue(syllables.contains(syllable), "Selected syllable should be one of the syllables in the file");
    }

    @Test
    public void testIsWordInFile() throws IOException {
        Path usedWordsFile = tempDir.resolve("UsedWords.txt");
        Files.writeString(usedWordsFile, "usedWord1\nusedWord2\n");

        // Create Words.txt with some content
        InputStream wordsStream = GameControllerTest.class.getResourceAsStream("/files/Words.txt");
        assertNotNull(wordsStream, "Failed to load words file");
        List<String> words = readLinesFromInputStream(wordsStream);

        // Test adding a new word
        String newWord = "word1";
        boolean isNewWordAdded = gameController.isWordInFile(newWord, Dice.FIRST, newWord);
        assertFalse(isNewWordAdded, "Should return true for adding a new word");
        assertFalse(Files.readAllLines(usedWordsFile).contains(newWord), "UsedWords.txt should contain the newly added word");

        // Test adding a word already used
        boolean isAlreadyUsedWordAdded = gameController.isWordInFile("usedWord1", Dice.FIRST, "usedWord1");
        assertFalse(isAlreadyUsedWordAdded, "Should return false for already used word");

        // Test adding a word not in Words.txt
        boolean isInvalidWordAdded = gameController.isWordInFile("invalidWord", Dice.FIRST, "invalidWord");
        assertFalse(isInvalidWordAdded, "Should return false for word not in Words.txt");
    }

    // Helper method to read lines from InputStream
    private List<String> readLinesFromInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().toList();
        }
    }
}
