import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class GameApp {
    JFrame frame;
    JButton muteButton;
    private static final Dimension MUTE_BUTTON_SIZE = new Dimension(50, 50);
    private static final String MUTE_ON_PATH = "Assets/MuteOn (1).png";
    private static final String MUTE_OFF_PATH = "Assets/MuteOff (1).png";
    private static final Dimension BUTTON_SIZE = new Dimension(280, 40);
    private static final Dimension Large_BUTTON_SIZE = new Dimension(380, 70);

    public static void main(String[] args) {
        new GameApp();
    }

    public GameApp() {
        frame = new JFrame("Gun Run - Main Menu");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        muteButton = createMuteButton();
        mainMenu();
        frame.setVisible(true);
        Sound.playBackground("Assets/Metal Slug 2 Prehistoric Site(MP3_160K).wav");
    }

    private JButton createMuteButton() {
        JButton muteBtn = new JButton();
        muteBtn.setPreferredSize(MUTE_BUTTON_SIZE);
        muteBtn.setMaximumSize(MUTE_BUTTON_SIZE);
        muteBtn.setMinimumSize(MUTE_BUTTON_SIZE);
        muteBtn.setOpaque(false);
        muteBtn.setContentAreaFilled(false);
        muteBtn.setBorderPainted(false);
        muteBtn.setFocusPainted(false);

        updateMuteButtonIcon(muteBtn);

        muteBtn.addActionListener(e -> {
            Sound.toggleMute();
            updateMuteButtonIcon(muteBtn);
        });

        return muteBtn;
    }

    private void updateMuteButtonIcon(JButton button) {
        String path = Sound.isMuted() ? MUTE_OFF_PATH : MUTE_ON_PATH;
        try {
            Image img = ImageIO.read(new File(path));
            if (img != null) {
                Image scaledImg = img.getScaledInstance(MUTE_BUTTON_SIZE.width, MUTE_BUTTON_SIZE.height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
            }
        } catch (IOException e) {
            button.setText(Sound.isMuted() ? "Unmute" : "Mute");
            button.setFont(new Font("Arial Black", Font.BOLD, 12));
            button.setToolTipText("Error loading sound icon");
        }
    }

    private JButton createStyledImageButton(String path, Dimension size) {
        JButton btn = new JButton();
        btn.setPreferredSize(size);
        btn.setMaximumSize(size);
        btn.setMinimumSize(size);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        try {
            Image img = ImageIO.read(new File(path));
            if (img != null) {
                Image scaledImg = img.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaledImg));
            } else {
                btn.setText("Error loading: " + path);
            }
        } catch (IOException e) {
            btn.setText("Error reading file: " + path);
        }
        return btn;
    }

    void mainMenu() {
        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("Assets/background1.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        final Image finalBgImage = bgImage;

        JPanel menuPanel = new JPanel(new BorderLayout()) {
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

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(muteButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(550, 150, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial Black", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(gameTitle);
        centerPanel.add(Box.createVerticalStrut(50));

        JButton startButton = createStyledImageButton("Assets/button/Start (1).png", BUTTON_SIZE);
        JButton scoreBoard = createStyledImageButton("Assets/button/Scoreboard (1).png", Large_BUTTON_SIZE);
        JButton instructions = createStyledImageButton("Assets/button/Instructions (1).png", Large_BUTTON_SIZE);

        instructions.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "MISSION BRIEFING:\n\n" +
                            "Objective: Survive the enemy attack until the timer runs out!\n\n" +
                            "🎮 PLAYER 1 CONTROLS:\n" +
                            "• Move: Left / Right Arrows\n" +
                            "• Jump: Up Arrow\n" +
                            "• Shoot: Spacebar\n\n" +
                            "🎮 PLAYER 2 CONTROLS (Multiplayer):\n" +
                            "• Move: 'A' (Left) / 'D' (Right)\n" +
                            "• Shoot: 'F' Key\n\n" +
                            "⚠️ WARNINGS:\n" +
                            "• Watch out for Zombies and Enemy Soldiers!\n" +
                            "• Dodge bombs dropped by the Helicopter!\n" +
                            "• Press 'P' to Pause anytime.\n\n" +
                            "Good Luck, Soldier!",
                    "How to Play",
                    JOptionPane.INFORMATION_MESSAGE);
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
        });

        scoreBoard.addActionListener(e -> showScoreboard());

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(scoreBoard);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(instructions);
        centerPanel.add(Box.createVerticalGlue());

        menuPanel.add(topPanel, BorderLayout.NORTH);
        menuPanel.add(centerPanel, BorderLayout.CENTER);

        frame.setContentPane(menuPanel);
        frame.revalidate();

        startButton.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            frame.dispose();
            showPlayerMode();
        });

        scoreBoard.addActionListener(e -> {
            Sound.stop();
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            Sound.playBackground("Assets/metal slug 3 carry out(M4A_128K).wav");
            showScoreboard();
        });
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

        JPanel menuPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(muteButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(550, 150, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial Black", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(gameTitle);
        centerPanel.add(Box.createVerticalStrut(50));

        JButton singlePlayer = createStyledImageButton("Assets/button/single player (1).png", Large_BUTTON_SIZE);
        JButton multiplePlayers = createStyledImageButton("Assets/button/Multi player (1).png", Large_BUTTON_SIZE);
        JButton back = createStyledImageButton("Assets/button/Back (1).png", BUTTON_SIZE);

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

        back.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            frame.dispose();
            new GameApp();
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(singlePlayer);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(multiplePlayers);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(back);
        centerPanel.add(Box.createVerticalGlue());

        menuPanel.add(topPanel, BorderLayout.NORTH);
        menuPanel.add(centerPanel, BorderLayout.CENTER);

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

        JPanel inputPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(muteButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(350, 100, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial Black", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(gameTitle);
        centerPanel.add(Box.createVerticalStrut(30));

        ActionListener nextStepAction = e -> {
            JTextField nameField1 = (JTextField) centerPanel.getClientProperty("nameField1");
            JTextField nameField2 = (JTextField) centerPanel.getClientProperty("nameField2");

            String p1 = nameField1.getText().trim();
            String p2 = (nameField2 != null) ? nameField2.getText().trim() : "";

            if (players == 1 && !p1.isEmpty()) {
                saveScore(p1, -1);
                nameFrame.dispose();
                showDifficultySelection(1);

            } else if (players == 2 && !p1.isEmpty() && !p2.isEmpty()) {
                saveScore(p1, -1);
                saveScore(p2, -1);
                nameFrame.dispose();
                showDifficultySelection(2);

            } else {
                JOptionPane.showMessageDialog(nameFrame, "Please enter all required usernames.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        JLabel label1 = new JLabel(players == 1 ? "Username:" : "Player 1 Username:");
        label1.setForeground(Color.RED);
        label1.setFont(new Font("Arial Black", Font.BOLD, 16));
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField nameField1 = new JTextField(20);
        nameField1.setMaximumSize(new Dimension(300, 30));
        nameField1.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField1.addActionListener(nextStepAction);

        centerPanel.putClientProperty("nameField1", nameField1);

        centerPanel.add(label1);
        centerPanel.add(nameField1);
        centerPanel.add(Box.createVerticalStrut(20));

        if (players == 2) {
            JLabel label2 = new JLabel("Player 2 Username:");
            label2.setForeground(Color.BLUE);
            label2.setFont(new Font("Arial Black", Font.BOLD, 16));
            label2.setAlignmentX(Component.CENTER_ALIGNMENT);
            JTextField nameField2 = new JTextField(20);
            nameField2.setMaximumSize(new Dimension(300, 30));
            nameField2.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameField2.addActionListener(nextStepAction);

            centerPanel.putClientProperty("nameField2", nameField2);

            centerPanel.add(label2);
            centerPanel.add(nameField2);
            centerPanel.add(Box.createVerticalStrut(30));
        }

        JButton nextBtn = createStyledImageButton("Assets/button/Next (1).png", BUTTON_SIZE);
        nextBtn.addActionListener(nextStepAction);
        nextBtn.setForeground(Color.WHITE);

        centerPanel.add(nextBtn);
        centerPanel.add(Box.createVerticalStrut(20));

        JButton backBtn = createStyledImageButton("Assets/button/Back (1).png", BUTTON_SIZE);
        backBtn.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            nameFrame.dispose();
            showPlayerMode();
        });
        centerPanel.add(backBtn);
        centerPanel.add(Box.createVerticalGlue());

        inputPanel.add(topPanel, BorderLayout.NORTH);
        inputPanel.add(centerPanel, BorderLayout.CENTER);

        nameFrame.add(inputPanel);
        nameFrame.setVisible(true);
        nameField1.requestFocusInWindow();
    }

    void showDifficultySelection(int players) {
        JFrame diffFrame = new JFrame("Gun Run - Select Difficulty");
        diffFrame.setSize(800, 600);
        diffFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        diffFrame.setLocationRelativeTo(null);

        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("Assets/background1.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        final Image finalBgImage = bgImage;

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(muteButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel gameTitle = new JLabel();
        try {
            Image logoImage = ImageIO.read(new File("Assets/gun_run_logo2.png"));
            logoImage = logoImage.getScaledInstance(550, 150, Image.SCALE_SMOOTH);
            gameTitle.setIcon(new ImageIcon(logoImage));
            gameTitle.setOpaque(false);
            gameTitle.setBackground(new Color(0, 0, 0, 0));
        } catch (IOException e) {
            gameTitle.setText("GUN RUN");
            gameTitle.setFont(new Font("Arial Black", Font.BOLD, 60));
            gameTitle.setForeground(Color.YELLOW);
            gameTitle.setOpaque(false);
        }
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(gameTitle);
        centerPanel.add(Box.createVerticalStrut(50));

        JButton easyBtn = createStyledImageButton("Assets/button/esay (1).png", BUTTON_SIZE);
        JButton mediumBtn = createStyledImageButton("Assets/button/Medium (1).png", BUTTON_SIZE);
        JButton hardBtn = createStyledImageButton("Assets/button/Hard (1).png", BUTTON_SIZE);
        JButton backBtn = createStyledImageButton("Assets/button/Back (1).png", BUTTON_SIZE);

        boolean isMulti = (players == 2);

        easyBtn.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            diffFrame.dispose();
            new GameGlListener("Easy", isMulti);
        });

        mediumBtn.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            diffFrame.dispose();
            new GameGlListener("Medium", isMulti);
        });

        hardBtn.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-drums-of-war-2784.wav");
            diffFrame.dispose();
            new GameGlListener("Hard", isMulti);
        });

        backBtn.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            diffFrame.dispose();
            showNameInput(players);
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(easyBtn);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(mediumBtn);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(hardBtn);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(backBtn);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        diffFrame.add(mainPanel);
        diffFrame.setVisible(true);
    }

    void showScoreboard() {
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBackground(Color.BLACK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(muteButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.BLACK);

        JLabel title = new JLabel("High Scores");
        title.setFont(new Font("Arial Black", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(20));

        Map<String, Integer> scores = loadScores();
        scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String text = entry.getKey() + " : " + entry.getValue();
                    JLabel scoreLabel = new JLabel(text);
                    scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
                    scoreLabel.setForeground(Color.WHITE);
                    scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    centerPanel.add(scoreLabel);
                    centerPanel.add(Box.createVerticalStrut(10));
                });

        JButton btnBack = createStyledImageButton("Assets/button/Back (1).png", BUTTON_SIZE);
        btnBack.addActionListener(e -> {
            Sound.playSound("Assets/mixkit-shotgun-long-pump-1666.wav");
            Sound.stop();
            frame.dispose();
            new GameApp();
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(btnBack);
        centerPanel.add(Box.createVerticalStrut(20));

        scorePanel.add(topPanel, BorderLayout.NORTH);
        scorePanel.add(centerPanel, BorderLayout.CENTER);

        frame.setContentPane(scorePanel);
        frame.revalidate();
    }

    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setBackground(new Color(20, 130, 140));
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
        int cnt = 0;
        File file = new File("scores.txt");
        if (!file.exists()) return scores;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && cnt != 10) {
                String[] parts = line.split(":");
                if (parts.length == 2) scores.put(parts[0], Integer.parseInt(parts[1]));
                cnt++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }
}