import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

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
        Sound.playBackground("Assets/Metal Slug 2 Prehistoric Site(MP3_160K).wav");
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
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(550, 150, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(gameTitle);
        menuPanel.add(Box.createVerticalStrut(50));

        JButton startButton = createStyledButton("Start");
        JButton scoreBoard = createStyledButton("Scoreboard");
        JButton Instructions = createStyledButton("Instructions");
        JButton Mute = createStyledButton(Sound.isMuted() ? "Unmute" : "Mute");

        Instructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Game Instructions:\n\n" +
                                "1. Use Arrow Keys to Move.\n" +
                                "2. Press 'Space' to Shoot.\n" +
                                "3. Avoid enemies and obstacles.\n" +
                                "4. Survive as long as possible!\n\n" +
                                "Good Luck, Soldier!",
                        "How to Play",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        scoreBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sound.stop();
                Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
                Sound.playBackground("Assets/metal slug 3 carry out(M4A_128K).wav");
                showScoreboard();
            }
        });

        startButton.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            frame.dispose();
            showPlayerMode();
        });

        Mute.addActionListener(e -> {
            Sound.toggleMute();
            Mute.setText(Sound.isMuted() ? "Unmute" : "Mute");
        });

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(startButton);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(scoreBoard);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(Instructions);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(Mute);
        menuPanel.add(Box.createVerticalGlue());

        frame.setContentPane(menuPanel);
        frame.revalidate();
    }

    void showPlayerMode() {
        JFrame frame = new JFrame("Gun Run - Select Player Mode");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

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
                if (finalBgImage != null) g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(550, 150, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(gameTitle);
        menuPanel.add(Box.createVerticalStrut(50));

        JButton singlePlayer = createStyledButton("Single Player");
        JButton multiplePlayers = createStyledButton("Multi Players");
        JButton Back = createStyledButton("Back");

        singlePlayer.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            frame.dispose();
            showNameInput(1);
        });

        multiplePlayers.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            frame.dispose();
            showNameInput(2);
        });

        Back.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            frame.dispose();
            new GameApp();
        });

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(singlePlayer);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(multiplePlayers);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(Back);
        menuPanel.add(Box.createVerticalGlue());

        frame.add(menuPanel);
        frame.setVisible(true);
    }

    void showNameInput(int players) {
        JFrame nameFrame = new JFrame("Gun Run - Enter Names");
        nameFrame.setSize(800, 600);
        nameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameFrame.setLocationRelativeTo(null);

        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("Assets/background1.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        final Image finalBgImage = bgImage;

        JPanel inputPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(true);

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(350, 100, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(Box.createVerticalStrut(30));
        inputPanel.add(gameTitle);
        inputPanel.add(Box.createVerticalStrut(30));

        ActionListener startGameAction = e -> {
            JTextField nameField1 = (JTextField) inputPanel.getClientProperty("nameField1");
            JTextField nameField2 = (JTextField) inputPanel.getClientProperty("nameField2");

            String p1 = nameField1.getText().trim();
            String p2 = (nameField2 != null) ? nameField2.getText().trim() : "";

            if (players == 1 && !p1.isEmpty()) {
                saveScore(p1, -1);
                nameFrame.dispose();
                new MetalSlugListener();

            } else if (players == 2 && !p1.isEmpty() && !p2.isEmpty()) {
                saveScore(p1, -1);
                saveScore(p2, -1);
                nameFrame.dispose();
                new MetalSlugListener();

            } else {
                JOptionPane.showMessageDialog(nameFrame, "Please enter all required usernames.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        JLabel label1 = new JLabel(players == 1 ? "Username:" : "Player 1 Username:");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("Arial", Font.BOLD, 16));
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField nameField1 = new JTextField(20);
        nameField1.setMaximumSize(new Dimension(300, 30));
        nameField1.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField1.addActionListener(startGameAction);

        inputPanel.putClientProperty("nameField1", nameField1);

        inputPanel.add(label1);
        inputPanel.add(nameField1);
        inputPanel.add(Box.createVerticalStrut(20));

        if (players == 2) {
            JLabel label2 = new JLabel("Player 2 Username:");
            label2.setForeground(Color.WHITE);
            label2.setFont(new Font("Arial", Font.BOLD, 16));
            label2.setAlignmentX(Component.CENTER_ALIGNMENT);
            JTextField nameField2 = new JTextField(20);
            nameField2.setMaximumSize(new Dimension(300, 30));
            nameField2.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameField2.addActionListener(startGameAction);

            inputPanel.putClientProperty("nameField2", nameField2);

            inputPanel.add(label2);
            inputPanel.add(nameField2);
            inputPanel.add(Box.createVerticalStrut(30));
        }

        JButton startBtn = createStyledButton("Enter");
        startBtn.addActionListener(startGameAction);
        startBtn.setForeground(Color.BLACK);

        inputPanel.add(startBtn);
        inputPanel.add(Box.createVerticalStrut(20));

        JButton backBtn = createStyledButton("Back");
        backBtn.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            nameFrame.dispose();
            showPlayerMode();
        });
        inputPanel.add(backBtn);
        inputPanel.add(Box.createVerticalGlue());

        nameFrame.add(inputPanel);
        nameFrame.setVisible(true);
        nameField1.requestFocusInWindow();
    }

    void showScoreboard() {
        JFrame sbFrame = new JFrame("Scoreboard");
        sbFrame.setSize(800, 600);
        sbFrame.setLocationRelativeTo(null);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.BLACK);

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
        btnBack.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            Sound.stop();
            Sound.playBackground("Assets/Metal Slug 2 Prehistoric Site(MP3_160K).wav");
            sbFrame.dispose();
            new GameApp();
        });

        scorePanel.add(Box.createVerticalGlue());
        scorePanel.add(btnBack);
        scorePanel.add(Box.createVerticalStrut(20));

        sbFrame.setContentPane(scorePanel);
        sbFrame.setVisible(true);
        frame.dispose();
    }

    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setMaximumSize(new Dimension(250, 60));
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    void saveScore(String playerName, int newScore) {
        Map<String, Integer> scores = loadScores();
        if (scores.containsKey(playerName)) {
            int currentScore = scores.get(playerName);
            if (newScore > currentScore) scores.put(playerName, newScore);
        } else {
            scores.put(playerName, newScore);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt"))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
                if (parts.length == 2) scores.put(parts[0], Integer.parseInt(parts[1]));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }
}