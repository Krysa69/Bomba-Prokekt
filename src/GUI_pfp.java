import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class GUI_pfp {

    public static void SelectionOfProfilePicture() {

        JFrame frame = new JFrame("Výběr profilového obrázku");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        String[] imageNames = {
                "sl.jpg","scaredTyger.jpg","gojo.jpg","lebron.jpg",
                "novby.jpg","sam.jpg","kocka.jpg","mellstroy.jpg",
                "cata.jpg","johan.jpg","majkl.jpg","asta.jpg",
                "sukuna.png","travisFish.png","majsner.jpg","bejr.jpg",

        };


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));


        for (String imageName : imageNames) {

            URL imageUrl = GUI_pfp.class.getResource("/resources/" + imageName);
            if (imageUrl == null) {
                System.err.println("Obrázek " + imageName + " nebyl nalezen!");
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
                    JOptionPane.showMessageDialog(frame, "Vybrali jste: " + imageName);
                }
            });
            panel.add(button);
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}
