import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class GUI_pfp {

    public static String[] selectProfilePicture(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Select Profile Picture", true);
        dialog.setPreferredSize(new Dimension(600, 600));
        dialog.setLayout(new BorderLayout());

        String[] imageNames = {
                "sl.jpg", "scaredTyger.jpg", "gojo.jpg", "lebron.jpg",
                "novby.jpg", "sam.jpg", "kocka.jpg", "mellstroy.jpg",
                "cata.jpg", "johan.jpg", "majkl.jpg", "asta.jpg",
                "sukuna.png", "travisFish.png", "majsner.jpg", "bejr.jpg",
        };

        String[] result = {null, null};

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

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = nameField.getText();
                if (userName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter your name.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                dialog.remove(inputPanel);

                JLabel userNameLabel = new JLabel("Username: " + userName);
                userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dialog.add(userNameLabel, BorderLayout.NORTH);

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(4, 4));

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

                dialog.add(panel, BorderLayout.CENTER);
                dialog.setSize(new Dimension(600, 600));
                dialog.revalidate();
                dialog.repaint();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);

        return result;
    }
}
