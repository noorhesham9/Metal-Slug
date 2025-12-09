import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class GameApp {

    JFrame frame;

    public static void main(String[] args) {
        new GameApp();
    }

    public GameApp() {
        frame = new JFrame("Gun Run - Main Menu");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        mainMenu();

        frame.setVisible(true);
    }

    void mainMenu() {
        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("Assets/background1.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

        btnSettings.addActionListener(e -> showScoreboard());

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnStart);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnSettings);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnExit);
        menuPanel.add(Box.createVerticalGlue());

        frame.setContentPane(menuPanel);
        frame.revalidate();
    }

    void showScoreboard() {
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("High Scores");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.add(Box.createVerticalStrut(20));
        scorePanel.add(title);
        scorePanel.add(Box.createVerticalStrut(20));
        Map<String, Integer> scores = loadScores();
        scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String text = entry.getKey() + " : " + entry.getValue();
                    JLabel scoreLabel = new JLabel(text);
                    scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
                    scoreLabel.setForeground(Color.WHITE);
                    scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    scorePanel.add(scoreLabel);
                    scorePanel.add(Box.createVerticalStrut(10));
                });


        JButton btnBack = createStyledButton("Back");
        btnBack.addActionListener(e -> mainMenu());

        scorePanel.add(Box.createVerticalGlue());
        scorePanel.add(btnBack);
        scorePanel.add(Box.createVerticalStrut(20));

        frame.setContentPane(scorePanel);
        frame.revalidate();
    }

    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setFocusable(false);
        return btn;
    }

    void saveScore(String playerName, int newScore) {
        Map<String, Integer> scores = loadScores();


        if (scores.containsKey(playerName)) {
            int currentScore = scores.get(playerName);
            if (newScore > currentScore) {
                scores.put(playerName, newScore);
            }
        } else {
            scores.put(playerName, newScore);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt"))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    Map<String, Integer> loadScores() {
        Map<String, Integer> scores = new HashMap<>();
        File file = new File("scores.txt");
        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    scores.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }
}