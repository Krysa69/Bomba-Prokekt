import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * The GUI_pfp class provides a static method to display a dialog for selecting a profile picture.
 * The dialog allows the user to enter their name and select a profile picture from a set of predefined images.
 */
public class GUI_pfp {

    /**
     * Displays a modal dialog for selecting a profile picture.
     * The dialog prompts the user to enter their name and choose a profile picture from a grid of images.
     */
    public static String[] selectProfilePicture(JFrame parentFrame) {
        // Initialize the dialog
        JDialog dialog = new JDialog(parentFrame, "Select Profile Picture", true);
        dialog.setPreferredSize(new Dimension(600, 600));
        dialog.setLayout(new BorderLayout());

        // Define the list of image names
        String[] imageNames = {
                "sl.jpg", "scaredTyger.jpg", "gojo.jpg", "lebron.jpg",
                "novby.jpg", "sam.jpg", "kocka.jpg", "mellstroy.jpg",
                "cata.jpg", "johan.jpg", "majkl.jpg", "asta.jpg",
                "sukuna.png", "travisFish.png", "majsner.jpg", "bejr.jpg",
        };

        // Initialize the result array
        String[] result = {null, null};

        // Create and set up the input panel for the user to enter their name
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Write your name:");
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30));
        JButton continueButton = new JButton("Continue");

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(nameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(continueButton, gbc);

        dialog.add(inputPanel, BorderLayout.CENTER);

        // Add action listener to the continue button
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = nameField.getText();
                if (userName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter your name.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Remove the input panel and display the image selection grid
                dialog.remove(inputPanel);

                JLabel userNameLabel = new JLabel("Username: " + userName);
                userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dialog.add(userNameLabel, BorderLayout.NORTH);

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(4, 4));

                // Create buttons for each profile picture
                for (String imageName : imageNames) {
                    URL imageUrl = GUI_pfp.class.getResource("/resources/" + imageName);
                    if (imageUrl == null) {
                        System.err.println("Image " + imageName + " not found!");
                        continue;
                    }
                    ImageIcon icon = new ImageIcon(imageUrl);
                    Image image = icon.getImage();
                    Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);

                    JButton button = new JButton(scaledIcon);
                    button.setPreferredSize(new Dimension(150, 150));
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            result[0] = imageName;
                            result[1] = userName;
                            dialog.dispose();
                        }
                    });
                    panel.add(button);
                }

                // Add the panel to the dialog and update its size and layout
                dialog.add(panel, BorderLayout.CENTER);
                dialog.setSize(new Dimension(600, 600));
                dialog.revalidate();
                dialog.repaint();
            }
        });

        // Set dialog properties and display it
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);

        // Return the result array containing the selected image name and user name
        return result;
    }
}