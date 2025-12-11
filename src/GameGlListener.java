import javax.media.opengl.*;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameGlListener implements GLEventListener, KeyListener, MouseListener {

    class Bullet {
        float x, y;
        boolean facingRight;
        boolean active;
        float width = 5, height = 3;
        boolean isPlayer2;

        public Bullet(float x, float y, boolean facingRight, boolean isPlayer2) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.active = true;
            this.isPlayer2 = isPlayer2;
        }
    }

    class Enemy {
        float x, y;
        int type;
        boolean facingRight;
        boolean active;
        long lastShotTime;
        float width = 10, height = 10;
        int state;
        long deathStartTime;
        final float ATTACK_RANGE = 45.0f;

        public Enemy(float x, float y, int type, boolean facingRight) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.facingRight = facingRight;
            this.active = true;
            this.state = 0;
            this.lastShotTime = System.currentTimeMillis();
        }
    }

    class Helicopter {
        float x;
        float y = 110.0f;
        float speed = 0.4f;
        boolean active = false;
        float targetX;
        boolean isDropping = false;
        long lastDropTime = 0;
        final long COOLDOWN = 20000;
        final float DROP_Y = 80.0f;
        final float HELI_HEIGHT = 15.0f;
        final float TOP_BUFFER = 10.0f;

        public void activate(float playerX) {
            this.active = true;
            this.x = playerX;
            this.y = 110.0f;
            this.targetX = playerX;
            this.isDropping = true;
            this.lastDropTime = System.currentTimeMillis();
        }
    }

    class EnemyBullet {
        float x, y;
        boolean facingRight;
        boolean active;
        float width = 5, height = 3;
        int type;

        public EnemyBullet(float x, float y, boolean facingRight, int type) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.active = true;
            this.type = type;
        }
    }

    class Bomb {
        float x, y;
        boolean active;
        float width = 5, height = 5;
        float speed = 0.5f;

        public Bomb(float x, float y) {
            this.x = x;
            this.y = y;
            this.active = true;
        }
    }

    class Explosion {
        float x, y;
        boolean active;
        long startTime;
        final long DURATION = 300;
        float width = 20, height = 20;

        public Explosion(float x, float y) {
            this.x = x - (width / 2);
            this.y = y;
            this.active = true;
            this.startTime = System.currentTimeMillis();
        }
    }

    float backgroundScrollX = 0.0f;
    final float PARALLAX_SPEED = 0.1f;
    Helicopter helicopter = new Helicopter();
    long lastHeliSpawnTime = 0;
    ArrayList<Bomb> helicopterBombs = new ArrayList<>();
    Texture helicopterTexture;
    Texture bombTexture;

    ArrayList<Explosion> explosions = new ArrayList<>();
    Texture[] explosionTextures = new Texture[1];
    JFrame myFrame;
    boolean isPaused = false;
    boolean isMultiplayer = false;
    int timerSeconds = 0;
    int score = 0;
    long lastTime;
    boolean isGameOver = false;
    boolean isGameRunning = true;
    boolean isWin = false;
    String difficultyLevel;

    TextRenderer timerRenderer;
    GLCanvas glCanvas;
    FPSAnimator animator;
    TextRenderer textRenderer;
    TextRenderer menuRenderer;
    TextRenderer ScoreRenderer;

    Texture backgroundTexture;
    Texture pauseButtonTexture;
    Texture scoreBoardTexture;
    Texture timerBoardTexture;
    Texture gamePausedTexture;
    Texture continueTexture;
    Texture exitTexture;
    Texture winTexture;
    Texture loseTexture;
    Texture Enter;
    Texture To;
    Texture Exit;
    Texture Restart;
    Texture bulletTexture;
    Texture muteOnTexture;
    Texture muteOffTexture;

    ArrayList<Texture> enemy_zombi_walk = new ArrayList<>();
    Texture enemy_zombi_death;
    Texture enemy_zombi_attack;

    ArrayList<Texture> enemy1_Scintest_walk = new ArrayList<>();
    Texture enemy1_Scintest_death;
    Texture enemy1_Scintest_attack;

    private final long ENEMY_SHOOT_INTERVAL = 1500;
    private float playerSpeed = 0.5f;

    Texture[] numbersTextures = new Texture[10];
    Texture[] healthImages = new Texture[6];

    int playerHealth = 100;
    ArrayList<Texture> idleTextures = new ArrayList<>();
    ArrayList<Texture> walkingTextures = new ArrayList<>();
    ArrayList<Texture> shootingTextures = new ArrayList<>();
    ArrayList<Texture> jumpTextures = new ArrayList<>();
    float playerX = 10;
    float playerY = 15;
    float groundLevel = 15;
    float playerWidth = 10;
    float playerHeight = 15;
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean isWalking = false;
    boolean isJumping = false;
    float verticalVelocity = 0;
    float gravity = 0.15f;
    float jumpStrength = 3.2f;
    boolean isShooting = false;
    boolean facingRight = true;
    long lastFrameTime = 0;
    int currentFrameIndex = 0;
    long shootingStartTime = 0;

    ArrayList<Texture> player2Textures = new ArrayList<>();
    float player2X = 30;
    float player2Y = 15;
    int player2Health = 100;
    boolean p2IsAlive = true;
    boolean p2LeftPressed = false;
    boolean p2RightPressed = false;
    boolean p2FacingRight = false;
    boolean p2IsWalking = false;
    boolean p2IsShooting = false;
    long p2LastFrameTime = 0;
    int p2FrameIndex = 0;
    long p2ShootingStartTime = 0;
    float player2Width = 10;
    float player2Height = 15;

    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    long lastSpawnTime = 0;
    long spawnInterval = 2000;
    Random random = new Random();
    private long helicopterCooldown;

    Rectangle continueBtnBounds = new Rectangle(35, 50, 30, 10);
    Rectangle exitBtnBounds = new Rectangle(35, 35, 30, 10);
    Rectangle gamePausedBounds = new Rectangle(25, 70, 50, 15);
    Rectangle pauseGameBtnBounds = new Rectangle(82, 88, 16, 8);
    Rectangle scoreBoardBounds = new Rectangle(2, 88, 20, 8);
    Rectangle timerBoardBounds = new Rectangle(40, 88, 20, 8);
    Rectangle muteBtnBounds = new Rectangle(5, 80, 10, 10);

    public GameGlListener(String difficulty, boolean isMultiplayer) {
        this.difficultyLevel = difficulty;
        this.isMultiplayer = isMultiplayer;

        if (difficulty.equals("Easy")) {
            timerSeconds = 30;
            spawnInterval = 3000;
            this.helicopterCooldown = 0;
        } else if (difficulty.equals("Medium")) {
            timerSeconds = 45;
            spawnInterval = 1800;
            this.helicopterCooldown = 7000;
        } else if (difficulty.equals("Hard")) {
            timerSeconds = 80;
            spawnInterval = 800;
            this.helicopterCooldown = 3500;
        }
        GLCapabilities capabilities = new GLCapabilities();
        glCanvas = new GLCanvas(capabilities);
        glCanvas.addGLEventListener(this);
        glCanvas.addKeyListener(this);
        glCanvas.addMouseListener(this);

        myFrame = new JFrame("Metal Slug - Game Mode (" + difficulty + ")");
        myFrame.setLayout(new BorderLayout());
        myFrame.add(glCanvas, BorderLayout.CENTER);
        myFrame.setSize(800, 600);
        myFrame.setLocationRelativeTo(null);
        myFrame.setResizable(false);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);

        animator = new FPSAnimator(glCanvas, 60);
        animator.start();

        lastTime = System.currentTimeMillis();
        glCanvas.requestFocus();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        try {
            backgroundTexture = TextureIO.newTexture(new File("Assets/Background.png"), true);
            pauseButtonTexture = TextureIO.newTexture(new File("Assets/Pauseboard (1).png"), true);
            scoreBoardTexture = TextureIO.newTexture(new File("Assets/scoreboard (1).png"), true);
            timerBoardTexture = TextureIO.newTexture(new File("Assets/timerboard (1).png"), true);
            gamePausedTexture = TextureIO.newTexture(new File("Assets/gamepausedboard (1).png"), true);
            continueTexture = TextureIO.newTexture(new File("Assets/continue (1).png"), true);
            exitTexture = TextureIO.newTexture(new File("Assets/exitboard (1).png"), true);
            winTexture = TextureIO.newTexture(new File("Assets/youwin.png"), true);
            loseTexture = TextureIO.newTexture(new File("Assets/youlose.png"), true);
           // Enter = TextureIO.newTexture(new File("Assets/word enter.png"), true);
            To = TextureIO.newTexture(new File("Assets/word to.png"), true);
            Exit = TextureIO.newTexture(new File("Assets/exit.png"), true);

            File restartFile = new File("Assets/button/Restart (1).png");
            if (restartFile.exists()) {
                Restart = TextureIO.newTexture(restartFile, true);
            } else {
                System.err.println("Restart button asset not found!");
            }

            muteOnTexture = TextureIO.newTexture(new File("Assets/MuteOff (1).png"), true);
            muteOffTexture = TextureIO.newTexture(new File("Assets/MuteOn (1).png"), true);

            for (int i = 0; i < 10; i++) {
                numbersTextures[i] = TextureIO.newTexture(new File("Assets/numbers board (" + i + ").png"), true);
            }
            for (int i = 0; i < 5; i++) {
                healthImages[i] = TextureIO.newTexture(new File("Assets/helthbar/" + i + ".png"), true);
            }


            int i = 1;
            while (true) {
                File f = new File("Assets/enemy/Enemy" + i + " (1).png");
                if (!f.exists()) break;
                enemy_zombi_walk.add(TextureIO.newTexture(f, true));
                i++;
            }
            enemy_zombi_attack = TextureIO.newTexture(new File("Assets/enemy/ScientistBullet.png"), true);
            enemy_zombi_death = TextureIO.newTexture(new File("Assets/enemy/Enemy1Dead.png"), true);
            i = 1;
            while (true) {
                File f = new File("Assets/enemy2/Enemy2 " + i + " (1).png");
                if (!f.exists()) break;
                enemy1_Scintest_walk.add(TextureIO.newTexture(f, true));
                i++;
            }
            enemy1_Scintest_attack = TextureIO.newTexture(new File("Assets/enemy2/MechaRobotAttack.png"), true);
            enemy1_Scintest_death = TextureIO.newTexture(new File("Assets/enemy2/Enemy2Dead.png"), true);

            File w1 = new File("Assets/playerWalking/15.png");
            File w2 = new File("Assets/playerWalking/13.png");
            File w3 = new File("Assets/playerWalking/14.png");
            if (w1.exists()) walkingTextures.add(TextureIO.newTexture(w1, true));
            if (w2.exists()) walkingTextures.add(TextureIO.newTexture(w2, true));
            if (w3.exists()) walkingTextures.add(TextureIO.newTexture(w3, true));

            i = 1;
            while (true) {
                File f = new File("Assets/playerIdle/" + i + ".png");
                if (!f.exists()) break;
                idleTextures.add(TextureIO.newTexture(f, true));
                i++;
            }
            i = 1;
            while (true) {
                File f = new File("Assets/playerShooting/" + i + ".png");
                if (!f.exists()) break;
                shootingTextures.add(TextureIO.newTexture(f, true));
                i++;
            }
            i = 1;
            while (true) {
                File f = new File("Assets/PlayerJumpUp/" + i + ".png");
                if (!f.exists()) break;
                jumpTextures.add(TextureIO.newTexture(f, true));
                i++;
            }

            File bFile = new File("Assets/bullet.png");
            if (bFile.exists()) bulletTexture = TextureIO.newTexture(bFile, true);

            File expFile = new File("Assets/EnemyAirship/AirshipDeath.png");
            if (expFile.exists()) explosionTextures[0] = TextureIO.newTexture(expFile, true);

            File heliFile = new File("Assets/EnemyHelicopter/EnemyHelicopter.png");
            if (heliFile.exists()) helicopterTexture = TextureIO.newTexture(heliFile, true);

            File bombFile = new File("Assets/EnemyHelicopter/HelicopterBullet.png");
            if (bombFile.exists()) bombTexture = TextureIO.newTexture(bombFile, true);

            for (int k = 1; k <= 10; k++) {
                File p2File = new File("Assets/enemy2/Enemy2 " + k + " (1).png");
                if (p2File.exists()) {
                    player2Textures.add(TextureIO.newTexture(p2File, true));
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        timerRenderer = new TextRenderer(new Font("Stencil", Font.BOLD, 40));
        menuRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 28));
        ScoreRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 40));
        textRenderer = new TextRenderer(new Font("Stencil", Font.BOLD, 30));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        drawBackground(gl);

        if (isGameRunning) {
            drawGame(gl);
            drawHUD(gl, drawable);
            checkGameStatus();
            drawHealthBar(gl);
            if (isMultiplayer) drawPlayer2HealthBar(gl);
        } else {
            renderEndScreen(gl, drawable.getWidth(), drawable.getHeight());
        }
        if (isPaused) {
            drawPauseMenu(gl, drawable);
        } else {
            updateTimer();
        }
    }

    private void drawGame(GL gl) {

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastFrameTime;
        final int ANIMATION_SPEED = 100;

        if (elapsed >= ANIMATION_SPEED) {
            currentFrameIndex = (currentFrameIndex + 1);
            lastFrameTime = currentTime;
        }

        if (!isPaused && !isGameOver) {
            gl.glDisable(GL.GL_DEPTH_TEST);

            if (playerHealth > 0) {
                if (leftPressed) {
                    playerX -= playerSpeed;
                    facingRight = false;
                    backgroundScrollX += PARALLAX_SPEED;
                }
                if (rightPressed) {
                    playerX += playerSpeed;
                    facingRight = true;
                    backgroundScrollX -= PARALLAX_SPEED;
                }
                if (isJumping) {
                    playerY += verticalVelocity;
                    verticalVelocity -= gravity;
                    if (playerY <= groundLevel) {
                        playerY = groundLevel;
                        isJumping = false;
                        verticalVelocity = 0;
                    }
                }
            }

            if (isMultiplayer && player2Health > 0) {
                if (p2LeftPressed) {
                    player2X -= playerSpeed;
                    p2FacingRight = false;
                    p2IsWalking = true;
                }
                if (p2RightPressed) {
                    player2X += playerSpeed;
                    p2FacingRight = true;
                    p2IsWalking = true;
                }
            }

            if (backgroundScrollX > 100) backgroundScrollX -= 100;
            if (backgroundScrollX < -100) backgroundScrollX += 100;

            final float MAX_X = 100f - playerWidth;
            if (playerX < 0) playerX = 0;
            if (playerX > MAX_X) playerX = MAX_X;
            if (player2X < 0) player2X = 0;
            if (player2X > MAX_X) player2X = MAX_X;

            spawnHelecopter();
            updateHelicopter(gl);
            updateEnemies(gl);
            updateBullets(gl);
            updateEnemyBullets(gl);
            updateBombs(gl);
            updateExplosions(gl);
            checkCollisions();
            spawnEnemies();

            if (playerHealth > 0) animateSprite(gl);
            if (isMultiplayer && player2Health > 0) drawPlayer2(gl);

            drawBullets(gl);
            drawEnemies(gl);
            drawEnemyBullets(gl);
            drawHelicopter(gl);
            drawBombs(gl);
            drawExplosions(gl);
        }
    }

    private void updateHelicopter(GL gl) {
        if (helicopter.active) {
            Sound.stop();

            if (helicopter.isDropping) {
                if (helicopter.y > helicopter.DROP_Y) {
                    Sound.playSound("Assets/Sounds/mixkit-arcade-game-explosion-2759.wav");
                    helicopter.y -= helicopter.speed;
                } else {
                    if (helicopter.y <= helicopter.DROP_Y && helicopter.y > helicopter.DROP_Y - 1.0f) {
                        helicopterBombs.add(new Bomb(helicopter.x + (helicopter.HELI_HEIGHT / 2), helicopter.y));
                        helicopter.y -= 2.0f;

                    }
                    helicopter.isDropping = false;
                }


            } else {
                helicopter.y += helicopter.speed * 2.0;
                if (helicopter.y > 115) {
                    helicopter.active = false;
                }
            }
        }
    }

    private void updateBombs(GL gl) {
        for (Bomb bomb : helicopterBombs) {
            if (bomb.active) {
                bomb.y -= bomb.speed;
                if (bomb.y < groundLevel) {
                    bomb.y = groundLevel;
                    Sound.playSound("Assets/Sounds/Explosion.wav");

                    bomb.active = false;
                    spawnExplosion(bomb.x, groundLevel);
                }
            }
        }
        helicopterBombs.removeIf(b -> !b.active);
    }

    private void drawHelicopter(GL gl) {
        if (!helicopter.active || helicopterTexture == null) return;
        float w = 20;
        float h = 15;
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor3f(1, 1, 1);
        helicopterTexture.enable();
        helicopterTexture.bind();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(helicopter.x, helicopter.y + h);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(helicopter.x + w, helicopter.y + h);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(helicopter.x + w, helicopter.y);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(helicopter.x, helicopter.y);
        gl.glEnd();
        helicopterTexture.disable();
        gl.glDisable(GL.GL_BLEND);

    }

    private void drawBombs(GL gl) {
        if (bombTexture == null) return;
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        bombTexture.enable();
        bombTexture.bind();

        gl.glColor3f(1, 1, 1);
        for (Bomb b : helicopterBombs) {
            if (!b.active) continue;
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(b.x, b.y + b.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(b.x + b.width, b.y + b.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(b.x + b.width, b.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(b.x, b.y);
            gl.glEnd();
        }
        bombTexture.disable();
        gl.glDisable(GL.GL_BLEND);
    }

    private void updateExplosions(GL gl) {
        explosions.removeIf(exp -> System.currentTimeMillis() - exp.startTime > exp.DURATION);
    }

    private void drawExplosions(GL gl) {
        if (explosionTextures[0] == null) return;
        gl.glEnable(GL.GL_BLEND);
        explosionTextures[0].enable();
        explosionTextures[0].bind();
        gl.glColor3f(1, 1, 1);
        for (Explosion exp : explosions) {
            if (exp.active) {

                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(exp.x, exp.y + exp.height);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(exp.x + exp.width, exp.y + exp.height);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(exp.x + exp.width, exp.y);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(exp.x, exp.y);
                gl.glEnd();
            }
        }
        explosionTextures[0].disable();
        gl.glDisable(GL.GL_BLEND);
    }

    private void spawnHelecopter() {

        long currentTime = System.currentTimeMillis();
        if (helicopter.active) return;
        if (difficultyLevel.equals("Easy")) return;
        if (currentTime - lastHeliSpawnTime >= helicopterCooldown) {
            helicopter.activate(playerX);
            lastHeliSpawnTime = currentTime;
        }
    }

    private void spawnEnemies() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > spawnInterval) {
            float spawnX = random.nextBoolean() ? -10 : 110;
            boolean facing = spawnX < 0;
            int type = random.nextInt(2);
            enemies.add(new Enemy(spawnX, groundLevel, type, facing));
            lastSpawnTime = currentTime;
        }
    }


    private void updateEnemies(GL gl) {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (!e.active || e.state == 2) continue;

            float target = playerX;

            if (e.x < target) e.facingRight = false;
            else e.facingRight = true;

            e.state = 0;
            float speed = (e.type == 0) ? 0.08f : 0.2f;

            if (e.x < target) e.x += speed;
            else e.x -= speed;

            if (currentTime - e.lastShotTime >= ENEMY_SHOOT_INTERVAL) {
                boolean reversedDirection = !e.facingRight;

                float startX = reversedDirection ? e.x + e.width : e.x - 5;
                float startY = e.y + (e.height / 2.0f);

                enemyBullets.add(new EnemyBullet(startX, startY, reversedDirection, e.type));

                e.lastShotTime = currentTime;
            }
        }
    }


    private void updateEnemyBullets(GL gl) {

        for (int i = 0; i < enemyBullets.size(); i++) {
            EnemyBullet b = enemyBullets.get(i);

            if (b.active) {

                if (b.facingRight) {
                    b.x += 1.5f;
                } else {
                    b.x -= 1.5f;
                }
                if (b.x < -10 || b.x > 110) b.active = false;
            }
        }


        enemyBullets.removeIf(b -> !b.active);
    }

    private void checkCollisions() {
        for (Bullet b : bullets) {
            if (!b.active) continue;
            for (Enemy e : enemies) {
                if (!e.active || e.state == 2) continue;
                if (b.x < e.x + e.width && b.x + b.width > e.x && b.y < e.y + e.height && b.y + b.height > e.y) {
                    b.active = false;
                    e.state = 2;
                    Sound.playSound("Assets/Sounds/Zombie.wav");

                    e.deathStartTime = System.currentTimeMillis();
                    score += 10;
                    break;
                }
            }
        }

        for (EnemyBullet eb : enemyBullets) {
            if (!eb.active) continue;
            if (playerHealth > 0 && eb.x < playerX + playerWidth && eb.x + eb.width > playerX && eb.y < playerY + playerHeight && eb.y + eb.height > playerY) {
                eb.active = false;
                playerHealth -= 10;


                score -= 5;
                if (score < 0) score = 0;
                continue;
            }

            if (!isMultiplayer && playerHealth <= 0) {
                Sound.playSound("Assets/Sounds/Death.wav");
                System.out.println("aaaaaaaaaaaaaaaaahhhh");
                System.out.println("aaaaaaaaaaaaaaaaahhhh");

            }


            if (isMultiplayer && player2Health > 0) {
                if (eb.x < player2X + player2Width && eb.x + eb.width > player2X && eb.y < player2Y + player2Height && eb.y + eb.height > player2Y) {
                    eb.active = false;
                    player2Health -= 10;

                    break;
                }
            }

            if (player2Health <= 0) {

                Sound.playSound("Assets/Sounds/Death.wav");
                System.out.println("Player 2 Died! Sound played once.");
            }


        }

        for (Enemy e : enemies) {
            if (e.active && e.state != 2) {
                if (playerHealth > 0 && e.x < playerX + playerWidth && e.x + e.width > playerX && e.y < playerY + playerHeight && e.y + e.height > playerY) {
                    e.active = false;
                    Sound.playSound("Assets/Sounds/Explosion.wav");
                    spawnExplosion(playerX + playerWidth / 2, playerY);
                    playerHealth -= 30;
                    score -= 20;
                    if (score < 0) score = 0;
                }
                if (isMultiplayer && player2Health > 0 && e.x < player2X + player2Width && e.x + e.width > player2X && e.y < player2Y + player2Height && e.y + e.height > player2Y) {
                    e.active = false;
                    Sound.playSound("Assets/Sounds/Explosion.wav");
                    spawnExplosion(player2X + player2Width / 2, player2Y);
                    player2Health -= 30;
                }
            }
        }

        for (Bomb bomb : helicopterBombs) {
            if (bomb.active) {

                if (playerHealth > 0 && bomb.x < playerX + playerWidth && bomb.x + bomb.width > playerX && bomb.y < playerY + playerHeight && bomb.y + bomb.height > playerY) {
                    bomb.active = false;
                    Sound.playSound("Assets/Sounds/Explosion.wav");
                    spawnExplosion(bomb.x, bomb.y);
                    playerHealth -= 30;
                    score -= 10;
                    if (score < 0) score = 0;
                }
                if (isMultiplayer && player2Health > 0 && bomb.x < player2X + player2Width && bomb.x + bomb.width > player2X && bomb.y < player2Y + player2Height && bomb.y + bomb.height > player2Y) {
                    bomb.active = false;
                    Sound.playSound("Assets/Sounds/Explosion.wav");
                    spawnExplosion(bomb.x, bomb.y);
                    player2Health -= 30;
                }
            }
        }
    }

    private void spawnExplosion(float x, float y) {
        explosions.add(new Explosion(x, y));
    }


    private void drawEnemies(GL gl) {
        long currentTime = System.currentTimeMillis();

        long elapsed = currentTime - lastFrameTime;
        final int ANIMATION_SPEED = 100;
        if (elapsed >= ANIMATION_SPEED) {
            currentFrameIndex = (currentFrameIndex + 1);
            lastFrameTime = currentTime;
        }


        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor3f(1, 1, 1);

        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (!e.active) continue;
            float currentScale;

            if (e.type == 1) {
                currentScale = 1.5f;
            } else {
                currentScale = 2.0f;
            }
            Texture currentTex = null;
            ArrayList<Texture> walkTextures = null;
            Texture deathTexture = null;
            Texture attackTexture = null;

            if (e.type == 0) {
                walkTextures = enemy_zombi_walk;
                deathTexture = enemy_zombi_death;
                attackTexture = enemy_zombi_attack;
            } else if (e.type == 1) {
                walkTextures = enemy1_Scintest_walk;
                deathTexture = enemy1_Scintest_death;
                attackTexture = enemy1_Scintest_attack;
            }

            if (e.state == 2) {
                currentTex = deathTexture;
                if (currentTime - e.deathStartTime > 500) {
                    e.active = false;
                }
            } else if (e.state == 1) {
                currentTex = attackTexture;
            } else {
                if (walkTextures != null && !walkTextures.isEmpty()) {
                    int frame = currentFrameIndex % walkTextures.size();
                    currentTex = walkTextures.get(frame);
                }
            }

            if (currentTex == null) continue;

            float w = e.width * currentScale;
            float h = e.height * currentScale;
            float drawX = e.x;
            float drawY = e.y;

            if (e.type == 0 && e.state == 2) {
                drawY -= 5.0f;
            }

            currentTex.enable();
            currentTex.bind();

            gl.glBegin(GL.GL_QUADS);
            if (e.facingRight) {
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(drawX, drawY + h);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(drawX + w, drawY + h);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(drawX + w, drawY);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(drawX, drawY);
            } else {
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(drawX, drawY + h);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(drawX + w, drawY + h);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(drawX + w, drawY);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(drawX, drawY);
            }
            gl.glEnd();
            currentTex.disable();
        }
        enemies.removeIf(e -> !e.active);
        gl.glDisable(GL.GL_BLEND);
    }


    private void drawEnemyBullets(GL gl) {

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor3f(1, 1, 1);

        Texture currentBoundTex = null;

        for (EnemyBullet b : enemyBullets) {
            if (!b.active) continue;

            Texture requiredTex = null;
            if (b.type == 0) {
                requiredTex = enemy_zombi_attack;
            } else if (b.type == 1) {
                requiredTex = enemy1_Scintest_attack;
            }

            if (requiredTex == null) continue;

            if (requiredTex != currentBoundTex) {
                if (currentBoundTex != null) currentBoundTex.disable();
                requiredTex.enable();
                requiredTex.bind();
                currentBoundTex = requiredTex;
            }

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(b.x, b.y + b.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(b.x + b.width, b.y + b.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(b.x + b.width, b.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(b.x, b.y);
            gl.glEnd();
        }

        if (currentBoundTex != null) {
            currentBoundTex.disable();
        }

        gl.glDisable(GL.GL_BLEND);

    }

    private void updateBullets(GL gl) {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if (b.active) {
                if (b.facingRight) b.x += 2;
                else b.x -= 2;
                if (b.x < 0 || b.x > 100) b.active = false;
            }
        }
        bullets.removeIf(b -> !b.active);
    }

    private void drawBullets(GL gl) {
        if (bulletTexture == null) return;
        bulletTexture.enable();
        bulletTexture.bind();
        gl.glBegin(GL.GL_QUADS);
        for (Bullet b : bullets) {
            if (b.active) {
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(b.x, b.y + b.height);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(b.x + b.width, b.y + b.height);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(b.x + b.width, b.y);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(b.x, b.y);
            }
        }
        gl.glEnd();
        bulletTexture.disable();
    }

    private void drawPlayer1(GL gl) {
        if (!isPaused && !isGameOver) {
            if (leftPressed) {
                playerX -= 0.8f;
                facingRight = false;
            }
            if (rightPressed) {
                playerX += 0.8f;
                facingRight = true;
            }
            playerX = Math.max(0, Math.min(playerX, 90));
            if (isJumping) {
                playerY += verticalVelocity;
                verticalVelocity -= gravity;
                if (playerY <= groundLevel) {
                    playerY = groundLevel;
                    isJumping = false;
                    verticalVelocity = 0;
                }
            }
            animateSprite(gl);
            updateBullets(gl);
        }
    }

    private void drawPlayer2(GL gl) {
        if (!isPaused && !isGameOver) {
            if (p2LeftPressed) {
                player2X -= 0.8f;
                p2FacingRight = false;
            }
            if (p2RightPressed) {
                player2X += 0.8f;
                p2FacingRight = true;
            }
            player2X = Math.max(0, Math.min(player2X, 90));

            if (!player2Textures.isEmpty()) {
                if (p2IsWalking) {
                    if (System.currentTimeMillis() - p2LastFrameTime > 75) {
                        p2FrameIndex++;
                        p2LastFrameTime = System.currentTimeMillis();
                    }
                    if (p2FrameIndex >= player2Textures.size()) p2FrameIndex = 0;
                } else p2FrameIndex = 0;

                Texture p2Frame = player2Textures.get(p2FrameIndex);
                if (p2Frame != null) {
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                    p2Frame.enable();
                    p2Frame.bind();
                    gl.glColor3f(1, 1, 1);
                    gl.glBegin(GL.GL_QUADS);
                    if (p2FacingRight) {
                        gl.glTexCoord2f(1.0f, 0.0f);
                        gl.glVertex2f(player2X, player2Y + 15);
                        gl.glTexCoord2f(0.0f, 0.0f);
                        gl.glVertex2f(player2X + 10, player2Y + 15);
                        gl.glTexCoord2f(0.0f, 1.0f);
                        gl.glVertex2f(player2X + 10, player2Y);
                        gl.glTexCoord2f(1.0f, 1.0f);
                        gl.glVertex2f(player2X, player2Y);
                    } else {
                        gl.glTexCoord2f(0.0f, 0.0f);
                        gl.glVertex2f(player2X, player2Y + 15);
                        gl.glTexCoord2f(1.0f, 0.0f);
                        gl.glVertex2f(player2X + 10, player2Y + 15);
                        gl.glTexCoord2f(1.0f, 1.0f);
                        gl.glVertex2f(player2X + 10, player2Y);
                        gl.glTexCoord2f(0.0f, 1.0f);
                        gl.glVertex2f(player2X, player2Y);
                    }
                    gl.glEnd();
                    p2Frame.disable();
                }
            }
        }
    }

    private void animateSprite(GL gl) {
        ArrayList<Texture> currentAnim;
        if (isShooting) {
            currentAnim = shootingTextures;
            if (System.currentTimeMillis() - shootingStartTime > 300) isShooting = false;
        } else if (isJumping && !jumpTextures.isEmpty()) currentAnim = jumpTextures;
        else if (isWalking) currentAnim = walkingTextures;
        else currentAnim = idleTextures;

        if (currentAnim.isEmpty()) return;
        if (System.currentTimeMillis() - lastFrameTime > 75) {
            currentFrameIndex++;
            lastFrameTime = System.currentTimeMillis();
        }
        if (currentFrameIndex >= currentAnim.size()) currentFrameIndex = 0;

        Texture frame = currentAnim.get(currentFrameIndex);
        if (frame != null) {
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            frame.enable();
            frame.bind();
            gl.glColor3f(1, 1, 1);
            gl.glBegin(GL.GL_QUADS);
            if (facingRight) {
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(playerX, playerY + playerHeight);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(playerX + playerWidth, playerY + playerHeight);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(playerX + playerWidth, playerY);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(playerX, playerY);
            } else {
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(playerX, playerY + playerHeight);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(playerX + playerWidth, playerY + playerHeight);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(playerX + playerWidth, playerY);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(playerX, playerY);
            }
            gl.glEnd();
            frame.disable();
            gl.glDisable(GL.GL_BLEND);
        }
    }

    private void drawBackground(GL gl) {
        if (backgroundTexture == null) return;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, 100, 0, 100, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glColor3f(1, 1, 1);
        backgroundTexture.enable();
        backgroundTexture.bind();
        final float BG_WIDTH = 100.0f;
        for (int i = -1; i <= 1; i++) {
            float currentX = backgroundScrollX + (i * BG_WIDTH);
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(currentX, 100);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(currentX + BG_WIDTH, 100);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(currentX + BG_WIDTH, 0);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(currentX, 0);
            gl.glEnd();
        }
        backgroundTexture.disable();
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    private void drawPlayer2HealthBar(GL gl) {
        if (healthImages != null) {
            int index;
            if (player2Health >= 80) index = 4;
            else if (player2Health >= 60) index = 3;
            else if (player2Health >= 40) index = 2;
            else if (player2Health >= 20) index = 1;
            else index = 0;
            if (healthImages[index] != null) {
                float x = 2;
                float y = 70;
                float w = 30;
                float h = 8;
                gl.glColor3f(1.0f, 1.0f, 1.0f);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                healthImages[index].enable();
                healthImages[index].bind();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(x, y + h);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(x + w, y + h);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(x + w, y);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(x, y);
                gl.glEnd();
                healthImages[index].disable();
                gl.glDisable(GL.GL_BLEND);
            }
        }
    }

    private void drawHUD(GL gl, GLAutoDrawable drawable) {
        int width = drawable.getWidth();
        int height = drawable.getHeight();
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        if (scoreBoardTexture != null) {
            scoreBoardTexture.enable();
            scoreBoardTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(scoreBoardBounds.x, scoreBoardBounds.y + scoreBoardBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(scoreBoardBounds.x + scoreBoardBounds.width, scoreBoardBounds.y + scoreBoardBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(scoreBoardBounds.x + scoreBoardBounds.width, scoreBoardBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(scoreBoardBounds.x, scoreBoardBounds.y);
            gl.glEnd();
            scoreBoardTexture.disable();
        }
        drawNumber(gl, score, scoreBoardBounds.x + scoreBoardBounds.width + 1, scoreBoardBounds.y + 1, 4, 6, 3);
        if (timerBoardTexture != null) {
            timerBoardTexture.enable();
            timerBoardTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(timerBoardBounds.x, timerBoardBounds.y + timerBoardBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(timerBoardBounds.x + timerBoardBounds.width, timerBoardBounds.y + timerBoardBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(timerBoardBounds.x + timerBoardBounds.width, timerBoardBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(timerBoardBounds.x, timerBoardBounds.y);
            gl.glEnd();
            timerBoardTexture.disable();
        }
        drawNumber(gl, timerSeconds, timerBoardBounds.x + timerBoardBounds.width + 1, timerBoardBounds.y + 1, 4, 6, 2);
        if (!isPaused && pauseButtonTexture != null) {
            pauseButtonTexture.enable();
            pauseButtonTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y);
            gl.glEnd();
            pauseButtonTexture.disable();
        }
        gl.glDisable(GL.GL_BLEND);
    }

    private void drawHealthBar(GL gl) {
        if (healthImages != null) {
            int index;

            if (playerHealth >= 80) index = 4;
            else if (playerHealth >= 60) index = 3;
            else if (playerHealth >= 40) index = 2;
            else if (playerHealth >= 20) index = 1;
            else index = 0;
            if (healthImages[index] != null) {
                float x = 2;
                float y = 82;
                float w = 30;
                float h = 8;
                gl.glColor3f(1.0f, 1.0f, 1.0f);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                healthImages[index].enable();
                healthImages[index].bind();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(x, y + h);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(x + w, y + h);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(x + w, y);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(x, y);
                gl.glEnd();
                healthImages[index].disable();
                gl.glDisable(GL.GL_BLEND);
            }
        }
    }

    private void drawNumber(GL gl, int number, int x, int y, int width, int height, int minDigits) {
        String numStr = String.format("%0" + minDigits + "d", number);
        for (int i = 0; i < numStr.length(); i++) {
            int digit = Character.getNumericValue(numStr.charAt(i));
            Texture tex = numbersTextures[digit];
            if (tex != null) {
                tex.enable();
                tex.bind();
                TextureCoords coords = tex.getImageTexCoords();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(coords.left(), coords.top());
                gl.glVertex2f(x + (i * width), y + height);
                gl.glTexCoord2f(coords.right(), coords.top());
                gl.glVertex2f(x + (i * width) + width, y + height);
                gl.glTexCoord2f(coords.right(), coords.bottom());
                gl.glVertex2f(x + (i * width) + width, y);
                gl.glTexCoord2f(coords.left(), coords.bottom());
                gl.glVertex2f(x + (i * width), y);
                gl.glEnd();
                tex.disable();
            }
        }
    }

    private void drawMuteButton(GL gl) {
        Texture currentMuteTexture = Sound.isMuted() ? muteOffTexture : muteOnTexture;
        if (currentMuteTexture != null) {
            float x = muteBtnBounds.x;
            float y = muteBtnBounds.y;
            float w = muteBtnBounds.width;
            float h = muteBtnBounds.height;
            currentMuteTexture.enable();
            currentMuteTexture.bind();
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(x, y + h);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(x + w, y + h);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(x + w, y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(x, y);
            gl.glEnd();
            currentMuteTexture.disable();
        }
    }

    private void drawPauseMenu(GL gl, GLAutoDrawable drawable) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.7f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(100, 0);
        gl.glVertex2f(100, 100);
        gl.glVertex2f(0, 100);
        gl.glEnd();
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        if (gamePausedTexture != null) {
            gamePausedTexture.enable();
            gamePausedTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(gamePausedBounds.x, gamePausedBounds.y + gamePausedBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(gamePausedBounds.x + gamePausedBounds.width, gamePausedBounds.y + gamePausedBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(gamePausedBounds.x + gamePausedBounds.width, gamePausedBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(gamePausedBounds.x, gamePausedBounds.y);
            gl.glEnd();
            gamePausedTexture.disable();
        }
        if (continueTexture != null) {
            continueTexture.enable();
            continueTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y + continueBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y + continueBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y);
            gl.glEnd();
            continueTexture.disable();
        }
        if (exitTexture != null) {
            exitTexture.enable();
            exitTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y + exitBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y + exitBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y);
            gl.glEnd();
            exitTexture.disable();
        }
        drawMuteButton(gl);
        gl.glDisable(GL.GL_BLEND);
    }

    private void renderEndScreen(GL gl, int width, int height) {
        Texture currentTex = isWin ? winTexture : loseTexture;
        if (currentTex != null) {
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glOrtho(0, 1, 0, 1, -1, 1);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glDisable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glColor3f(1, 1, 1);
            currentTex.enable();
            currentTex.bind();
            gl.glScaled(0.5, 0.5, 1);
            gl.glTranslatef(0.5f, 0.75f, 0);
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex2f(0.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex2f(1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex2f(1.0f, 0.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex2f(0.0f, 0.0f);
            gl.glEnd();
            currentTex.disable();
            gl.glDisable(GL.GL_BLEND);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glEnable(GL.GL_DEPTH_TEST);
        } else {
            menuRenderer.beginRendering(width, height);
            menuRenderer.setColor(isWin ? Color.GREEN : Color.RED);
            menuRenderer.draw(isWin ? "WIN" : "LOSE", width / 2, height / 2);
            menuRenderer.endRendering();
        }
        ScoreRenderer.beginRendering(width, height);
        menuRenderer.beginRendering(width, height);
        menuRenderer.setColor(Color.RED);
        menuRenderer.draw("Score: " + score, (width / 2) - 80, (height / 2) - 50);
        menuRenderer.setColor(Color.YELLOW);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // ⭐ التعديل: إحداثيات الأزرار الأفقية أسفل النتيجة ⭐
        float btnSize = 0.2f;
        float startX = 0.20f;
        float posY = 0.40f;   // موضع عمودي أعلى (أسفل النتيجة مباشرة)
        float spacing = 0.22f;

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, 1, 0, 1, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // ⭐ الأزرار في صف أفقي واحد: Restart، ثم Enter، ثم Exit ⭐
        Texture[] buttons = {Restart, Enter, Exit};

        for (int i = 0; i < buttons.length; i++) {
            Texture btnTex = buttons[i];
            if (btnTex == null) continue;

            float currentX = startX + (i * spacing);

            gl.glColor3f(1, 1, 1);
            btnTex.enable();
            btnTex.bind();
            TextureCoords coords = btnTex.getImageTexCoords();

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(coords.left(), coords.bottom());
            gl.glVertex2f(currentX, posY);
            gl.glTexCoord2f(coords.right(), coords.bottom());
            gl.glVertex2f(currentX + btnSize, posY);
            gl.glTexCoord2f(coords.right(), coords.top());
            gl.glVertex2f(currentX + btnSize, posY + btnSize);
            gl.glTexCoord2f(coords.left(), coords.top());
            gl.glVertex2f(currentX, posY + btnSize);
            gl.glEnd();
            btnTex.disable();
        }
        gl.glDisable(GL.GL_BLEND);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        menuRenderer.endRendering();
        ScoreRenderer.endRendering();
    }

    private void checkGameStatus() {
        if (isMultiplayer) {
            if (playerHealth <= 0 && player2Health <= 0) {
                playerHealth = 0;
                player2Health = 0;
                isGameRunning = false;
                isWin = false;
                Sound.playSound("Assets/Sounds/Boss.wav");
                System.out.println("aaaaaaaaaaaaaaaaahhhh");

            }
        } else {
            if (playerHealth <= 0) {
                playerHealth = 0;
                isGameRunning = false;
                isWin = false;
                Sound.playSound("Assets/Sounds/Boss.wav");
                System.out.println("aaaaaaaaaaaaaaaaahhhh");

            }
        }
        if (timerSeconds <= 0) {
            isGameRunning = false;
            isWin = true;
            Sound.playSound("Assets/Sounds/Victory.wav");
            Sound.playSound("Assets/Sounds/Victory1.wav");
            System.out.println("aaaaaaaaaaaaaaaaahhhh");
        }
    }

    private void updateTimer() {
        if (!isGameOver && System.currentTimeMillis() - lastTime > 1000) {
            timerSeconds--;
            lastTime = System.currentTimeMillis();
            if (timerSeconds <= 0) isGameOver = true;
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 100, 0, 100, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isPaused) {
            if (e.getKeyCode() == KeyEvent.VK_P) isPaused = !isPaused;
            return;
        }
        if (!isGameRunning) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                myFrame.dispose();
                new GameApp();
            }
            return;
        }

        if (playerHealth > 0) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
                facingRight = false;
                isWalking = true;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
                facingRight = true;
                isWalking = true;
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (!isJumping) {
                    isJumping = true;
                    verticalVelocity = jumpStrength;
                    currentFrameIndex = 0;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                isShooting = true;
                shootingStartTime = System.currentTimeMillis();
                currentFrameIndex = 0;
                float bulletStartX = facingRight ? playerX + 8 : playerX - 1;
                bullets.add(new Bullet(bulletStartX, playerY + 8, facingRight, false));
                Sound.playSound("Assets/Sounds/Shoot1.wav");

            }
        }

        if (isMultiplayer && player2Health > 0) {
            if (e.getKeyCode() == KeyEvent.VK_A) {
                p2LeftPressed = true;
                p2FacingRight = false;
                p2IsWalking = true;
            } else if (e.getKeyCode() == KeyEvent.VK_D) {
                p2RightPressed = true;
                p2FacingRight = true;
                p2IsWalking = true;
            } else if (e.getKeyCode() == KeyEvent.VK_F) {
                p2IsShooting = true;
                p2ShootingStartTime = System.currentTimeMillis();
                p2FrameIndex = 0;
                float bulletStartX = p2FacingRight ? player2X + 10 : player2X - 5;
                bullets.add(new Bullet(bulletStartX, player2Y + 8, p2FacingRight, true));
                Sound.playSound("Assets/Sounds/Shoot3.wav");

            }
        }

        if (e.getKeyCode() == KeyEvent.VK_P) isPaused = !isPaused;
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (isPaused) {
                myFrame.dispose();
                new GameApp();
            } else isPaused = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        isWalking = leftPressed || rightPressed;

        if (isMultiplayer) {
            if (e.getKeyCode() == KeyEvent.VK_A) p2LeftPressed = false;
            if (e.getKeyCode() == KeyEvent.VK_D) p2RightPressed = false;
            p2IsWalking = p2LeftPressed || p2RightPressed;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        double w = glCanvas.getWidth();
        double h = glCanvas.getHeight();
        double mouseX = (e.getX() / w) * 100.0;
        double mouseY = ((h - e.getY()) / h) * 100.0;
        if (isPaused) {
            if (mouseX >= muteBtnBounds.x && mouseX <= muteBtnBounds.x + muteBtnBounds.width &&
                    mouseY >= muteBtnBounds.y && mouseY <= muteBtnBounds.y + muteBtnBounds.height) {
                Sound.toggleMute();
                glCanvas.repaint();
                return;
            }
            if (mouseX >= continueBtnBounds.x && mouseX <= continueBtnBounds.x + continueBtnBounds.width && mouseY >= continueBtnBounds.y && mouseY <= continueBtnBounds.y + continueBtnBounds.height)
                isPaused = false;
            else if (mouseX >= exitBtnBounds.x && mouseX <= exitBtnBounds.x + exitBtnBounds.width && mouseY >= exitBtnBounds.y && mouseY <= exitBtnBounds.y + exitBtnBounds.height) {
                myFrame.dispose();
                if (animator != null) animator.stop();
                new GameApp();
            }
        } else {
            if (mouseX >= pauseGameBtnBounds.x && mouseX <= pauseGameBtnBounds.x + pauseGameBtnBounds.width && mouseY >= pauseGameBtnBounds.y && mouseY <= pauseGameBtnBounds.y + pauseGameBtnBounds.height)
                isPaused = true;
        }

        if (!isGameRunning) {
            float btnSize = 0.2f;
            float startX = 0.20f;
            float posY = 0.40f;
            float spacing = 0.22f;

            float button_bottom = posY * 100;
            float button_top = (posY + btnSize) * 100;

            // 1. فحص زر Restart (الموقع i=0)
            float restartX_left = startX * 100;
            float restartX_right = (startX + btnSize) * 100;

            if (mouseX >= restartX_left && mouseX <= restartX_right &&
                    mouseY >= button_bottom && mouseY <= button_top) {

                myFrame.dispose();
                Sound.stop();
                new GameGlListener(this.difficultyLevel, this.isMultiplayer);
                return;
            }

            // 2. فحص زر Exit (الموقع i=2)
            float exitX_left = (startX + 2 * spacing) * 100;
            float exitX_right = (startX + 2 * spacing + btnSize) * 100;

            if (mouseX >= exitX_left && mouseX <= exitX_right &&
                    mouseY >= button_bottom && mouseY <= button_top) {

                myFrame.dispose();
                if (animator != null) animator.stop();
                new GameApp();
                return;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}