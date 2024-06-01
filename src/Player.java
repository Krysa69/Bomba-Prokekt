/**
 * The Player class represents a player in the Word Bomb game.
 * Each player has a profile picture and a name.
 */
public class Player {
    private String profilePicturePath;
    private String name;

    /**
     * Constructs a new Player with the specified profile picture path and name.
     */
    public Player(String profilePicturePath, String name) {
        this.profilePicturePath = profilePicturePath;
        this.name = name;
    }

    /**
     * Returns the path to the profile picture of the player.
     */
    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    /**
     * Returns the name of the player.
     */
    public String getName() {
        return name;
    }
}
