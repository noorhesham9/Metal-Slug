import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class GameApp {

    public static void main(String[] args) {
        new GameApp();
    }

    public GameApp(){
        JFrame frame = new JFrame("Gun Run - Main Menu");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("Assets/background1.png"));
        } catch (Exception e) {
            System.out.println("Image not found! Check file path.");
        }
        final Image finalBgImage = bgImage;
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) {
                    g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        JButton btnStart = createStyledButton("Start");
        JButton btnSettings = createStyledButton("Scoreboard");
        JButton btnExit = createStyledButton("Instructions");
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnStart);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnSettings);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnExit);
        menuPanel.add(Box.createVerticalGlue());
        frame.add(menuPanel);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setMaximumSize(new Dimension(200, 50));
        return btn;
    }
}