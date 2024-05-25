public class Player {
    private String profilePicturePath;
    private String name;

    public Player(String profilePicturePath, String name) {
        this.profilePicturePath = profilePicturePath;
        this.name = name;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public String getName() {
        return name;
    }
}
