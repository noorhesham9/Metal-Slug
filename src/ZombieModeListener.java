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

public class ZombieModeListener implements GLEventListener, KeyListener, MouseListener {

    class Bullet {
        float x, y;
        boolean facingRight;
        boolean active;
        float width = 5, height = 3;

        public Bullet(float x, float y, boolean facingRight) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.active = true;
        }
    }

    class Zombie {
        float x, y;
        int type;
        boolean facingRight;
        boolean active;
        float width, height;
        int state;
        long deathStartTime;

        public Zombie(float x, float y, int type, boolean facingRight) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.facingRight = facingRight;
            this.active = true;
            this.state = 0;

            if (type == 0) {
                this.width = 12;
                this.height = 15;
            } else if(type==1){
                this.width = 15;
                this.height = 17;
            }
            else if (type == 2) {
                this.width = 17;
                this.height = 18;
            }
        }
    }

    class ZombiePlane {
        float x;
        float y = 100.0f;
        float speed = 0.9f;
        boolean active = false;
        boolean movingRight = true;

        public void activate() {
            this.active = true;
            this.x = 0;
            this.y = 100.0f;
            this.movingRight = true;
        }
    }

    class ZombieBomb {
        float x, y;
        boolean active;
        float width = 3, height = 3;
        float speed = 1.3f;

        public ZombieBomb(float x, float y) {
            this.x = x;
            this.y = y;
            this.active = true;
        }
    }

    class ZombieExplosion {
        float x, y;
        boolean active;
        long startTime;
        final long DURATION = 300;
        float width = 10, height = 12;

        public ZombieExplosion(float x, float y) {
            this.x = x - (width / 2);
            this.y = y;
            this.active = true;
            this.startTime = System.currentTimeMillis();
        }
    }

    float backgroundScrollX = 0.0f;
    final float PARALLAX_SPEED = 0.12f;
    ZombiePlane plane = new ZombiePlane();
    long lastPlaneSpawnTime = 0;
    ArrayList<ZombieBomb> planeBombs = new ArrayList<>();
    Texture planeTexture;
    Texture bombTexture;

    ArrayList<ZombieExplosion> explosions = new ArrayList<>();
    Texture explosionTexture;
    final float ZOMBIE_SCALE = 1.5f;

    JFrame myFrame;
    boolean isPaused = false;
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
    Texture bulletTexture;
    Texture muteOnTexture;
    Texture muteOffTexture;

    Texture zombieNormalTexture;
    Texture zombieBigTexture;
    Texture zombieDeadTexture;

    private float playerSpeed = 0.6f;

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

    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Zombie> zombies = new ArrayList<>();
    long lastSpawnTime = 0;
    long spawnInterval = 2000;
    Random random = new Random();
    private long planeCooldown = 15000;

    Rectangle continueBtnBounds = new Rectangle(35, 50, 30, 10);
    Rectangle exitBtnBounds = new Rectangle(35, 35, 30, 10);
    Rectangle gamePausedBounds = new Rectangle(25, 70, 50, 15);
    Rectangle pauseGameBtnBounds = new Rectangle(82, 88, 16, 8);
    Rectangle scoreBoardBounds = new Rectangle(2, 88, 20, 8);
    Rectangle timerBoardBounds = new Rectangle(40, 88, 20, 8);
    Rectangle muteBtnBounds = new Rectangle(5, 80, 10, 10);

    public ZombieModeListener(String difficulty) {
        this.difficultyLevel = difficulty;

        if (difficulty.equals("Easy")) {
            timerSeconds = 30;
            spawnInterval = 3000;
            this.planeCooldown = 20000;
        } else if (difficulty.equals("Medium")) {
            timerSeconds = 45;
            spawnInterval = 2000;
            this.planeCooldown = 10000;
        } else if (difficulty.equals("Hard")) {
            timerSeconds = 60;
            spawnInterval = 1500;
            this.planeCooldown = 8000;
        }

        GLCapabilities capabilities = new GLCapabilities();
        glCanvas = new GLCanvas(capabilities);
        glCanvas.addGLEventListener(this);
        glCanvas.addKeyListener(this);
        glCanvas.addMouseListener(this);

        myFrame = new JFrame("ZOMBIE MODE - " + difficulty);
        myFrame.setLayout(new BorderLayout());
        myFrame.add(glCanvas, BorderLayout.CENTER);
        myFrame.setSize(1400, 700);
        myFrame.setLocationRelativeTo(null);
        myFrame.setResizable(false);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);

        animator = new FPSAnimator(glCanvas, 60);
        animator.start();

        lastTime = System.currentTimeMillis();
        glCanvas.requestFocus();

        Sound.playBackground("Assets/ZombieMode/MusicBackGround.wav");
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(0.1f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        try {
            backgroundTexture = TextureIO.newTexture(new File("Assets/ZombieMode/BackGround.png"), true);

            pauseButtonTexture = TextureIO.newTexture(new File("Assets/Pauseboard (1).png"), true);
            scoreBoardTexture = TextureIO.newTexture(new File("Assets/scoreboard (1).png"), true);
            timerBoardTexture = TextureIO.newTexture(new File("Assets/timerboard (1).png"), true);
            gamePausedTexture = TextureIO.newTexture(new File("Assets/gamepausedboard (1).png"), true);
            continueTexture = TextureIO.newTexture(new File("Assets/continue (1).png"), true);
            exitTexture = TextureIO.newTexture(new File("Assets/exitboard (1).png"), true);
            winTexture = TextureIO.newTexture(new File("Assets/youwin.png"), true);
            loseTexture = TextureIO.newTexture(new File("Assets/youlose.png"), true);
            muteOnTexture = TextureIO.newTexture(new File("Assets/MuteOff (1).png"), true);
            muteOffTexture = TextureIO.newTexture(new File("Assets/MuteOn (1).png"), true);

            for (int i = 0; i < 10; i++) {
                numbersTextures[i] = TextureIO.newTexture(new File("Assets/numbers board (" + i + ").png"), true);
            }
            for (int i = 0; i < 5; i++) {
                healthImages[i] = TextureIO.newTexture(new File("Assets/helthbar/" + i + ".png"), true);
            }

            zombieNormalTexture = TextureIO.newTexture(new File("Assets/ZombieMode/EnemyZombieMacro/Zombie.png"), true);
            zombieBigTexture = TextureIO.newTexture(new File("Assets/ZombieMode/enemy/Enemy1 (1).png"), true);
            zombieDeadTexture = TextureIO.newTexture(new File("Assets/ZombieMode/EnemyZombieMacro/ZombieDeath.png"), true);

            File w1 = new File("Assets/playerWalking/15.png");
            File w2 = new File("Assets/playerWalking/13.png");
            File w3 = new File("Assets/playerWalking/14.png");
            if (w1.exists()) walkingTextures.add(TextureIO.newTexture(w1, true));
            if (w2.exists()) walkingTextures.add(TextureIO.newTexture(w2, true));
            if (w3.exists()) walkingTextures.add(TextureIO.newTexture(w3, true));

            int i = 1;
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

            planeTexture = TextureIO.newTexture(new File("Assets/EnemyUFO/EnemyUFO.png"), true);
            bombTexture = TextureIO.newTexture(new File("Assets/EnemyUFO/UFOBullet.png"), true);
            explosionTexture = TextureIO.newTexture(new File("Assets/EnemyUFO/UFODeath.png"), true);

        } catch (IOException e) {
            System.err.println("Error loading textures: " + e.getMessage());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Sound.stop();
                    Sound.playBackground("Assets/ZombieMode/MusicBackGround2.wav");
                    System.out.println("Background music started from init()");
                } catch (Exception e) {
                    System.err.println("Error in music thread: " + e.getMessage());
                }
            }
        }).start();


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
            Sound.playBackground("Assets/ZombieMode/MusicBackGround.wav");

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

            if (backgroundScrollX > 100) backgroundScrollX -= 100;
            if (backgroundScrollX < -100) backgroundScrollX += 100;

            final float MAX_X = 100f - playerWidth;
            if (playerX < 0) playerX = 0;
            if (playerX > MAX_X) playerX = MAX_X;

            spawnZombiePlane();
            updatePlane();
            updateZombies();
            updateBullets(gl);
            updateBombs();
            updateExplosions(gl);
            checkCollisions();
            spawnZombies();

            if (playerHealth > 0) animateSprite(gl);

            drawBullets(gl);
            drawZombies(gl);
            drawPlane(gl);
            drawBombs(gl);
            drawExplosions(gl);
        }
    }

    private void spawnZombiePlane() {
        long currentTime = System.currentTimeMillis();
        if (plane.active) return;
        if (currentTime - lastPlaneSpawnTime >= planeCooldown) {
            plane.activate();
            lastPlaneSpawnTime = currentTime;

        }
    }

    private void updatePlane() {
        if (plane.active) {
            if (plane.movingRight) {
                plane.x += plane.speed;
                if (plane.x > 100) {
                    plane.active = false;
                }

                if (random.nextInt(100) < 2) {
                    planeBombs.add(new ZombieBomb(plane.x + 10, plane.y));
                }
            } else {
                plane.x -= plane.speed;
                if (plane.x < -20) {
                    plane.active = false;
                }
            }
        }
    }

    private void updateBombs() {
        for (ZombieBomb bomb : planeBombs) {
            if (bomb.active) {
                bomb.y -= bomb.speed;
                if (bomb.y < groundLevel) {
                    bomb.y = groundLevel;
                    bomb.active = false;
                    spawnExplosion(bomb.x, groundLevel);
                    Sound.playSound("Assets/Sounds/explosion.wav");
                }
            }
        }
        planeBombs.removeIf(b -> !b.active);
    }

    private void drawPlane(GL gl) {
        if (!plane.active || planeTexture == null) return;
        float w = 25; float h = 10;
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor3f(1, 1, 1);
        planeTexture.enable();
        planeTexture.bind();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(plane.x, plane.y + h);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(plane.x + w, plane.y + h);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(plane.x + w, plane.y);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(plane.x, plane.y);
        gl.glEnd();
        planeTexture.disable();
        gl.glDisable(GL.GL_BLEND);
    }

    private void drawBombs(GL gl) {
        if (bombTexture == null) return;
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        bombTexture.enable();
        bombTexture.bind();
        gl.glColor3f(1, 1, 1);

        for (ZombieBomb b : planeBombs) {
            if (!b.active) continue;
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(b.x, b.y + b.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(b.x + b.width, b.y + b.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(b.x + b.width, b.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(b.x, b.y);
            gl.glEnd();
        }
        bombTexture.disable();
        gl.glDisable(GL.GL_BLEND);
    }

    private void updateExplosions(GL gl) {
        explosions.removeIf(exp -> System.currentTimeMillis() - exp.startTime > exp.DURATION);
    }

    private void drawExplosions(GL gl) {
        if (explosionTexture == null) return;
        gl.glEnable(GL.GL_BLEND);
        explosionTexture.enable();
        explosionTexture.bind();
        gl.glColor3f(1, 1, 1);

        for (ZombieExplosion exp : explosions) {
            if (exp.active) {
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(exp.x, exp.y + exp.height);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(exp.x + exp.width, exp.y + exp.height);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(exp.x + exp.width, exp.y);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(exp.x, exp.y);
                gl.glEnd();
            }
        }
        explosionTexture.disable();
        gl.glDisable(GL.GL_BLEND);
    }

    private void spawnZombies() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > spawnInterval) {
            float spawnX = random.nextBoolean() ? -10 : 110;
            boolean facing = spawnX < 0;
            int type = random.nextInt(2);
            zombies.add(new Zombie(spawnX, groundLevel, type, facing));
            lastSpawnTime = currentTime;
            Sound.playSound("Assets/ZombieMode/mixkit-angry-zombie-grunting-297.wav");
        }
    }

    private void updateZombies() {
        for (int i = 0; i < zombies.size(); i++) {
            Zombie z = zombies.get(i);
            if (!z.active || z.state == 2) continue;

            if (z.x < playerX) z.facingRight = false;
            else z.facingRight = true;

            float speed = (z.type == 0) ? 0.6f : 0.3f;

            if (z.x < playerX) z.x += speed;
            else z.x -= speed;
        }
    }

    private void checkCollisions() {
        for (Bullet b : bullets) {
            if (!b.active) continue;
            for (Zombie z : zombies) {
                if (!z.active || z.state == 2) continue;
                if (b.x < z.x + z.width && b.x + b.width > z.x &&
                        b.y < z.y + z.height && b.y + b.height > z.y) {
                    b.active = false;
                    z.state = 2;
                    z.deathStartTime = System.currentTimeMillis();

                    score += (z.type == 0) ? 10 : 25;
                    Sound.playSound("Assets/Sounds/Scientist.wav");
                    break;
                }
            }
        }

        for (Zombie z : zombies) {
            if (z.active && z.state != 2 && playerHealth > 0) {
                if (z.x < playerX + playerWidth && z.x + z.width > playerX &&
                        z.y < playerY + playerHeight && z.y + z.height > playerY) {
                    z.active = false;
                    spawnExplosion(playerX + playerWidth / 2, playerY);

                    int damage = (z.type == 0) ? 20 : 40;
                    playerHealth -= damage;


                    if (playerHealth <= 0) {
                        Sound.playSound("Assets/Sounds/Death.wav");
                    }
                }
            }
        }

        for (ZombieBomb bomb : planeBombs) {
            if (bomb.active && playerHealth > 0) {
                if (bomb.x < playerX + playerWidth && bomb.x + bomb.width > playerX &&
                        bomb.y < playerY + playerHeight && bomb.y + bomb.height > playerY) {
                    bomb.active = false;
                    spawnExplosion(bomb.x, bomb.y);
                    playerHealth -= 30;

                    if (playerHealth <= 0) {
                        Sound.playSound("Assets/Sounds/Death.wav");
                    }
                }
            }
        }
    }

    private void spawnExplosion(float x, float y) {
        explosions.add(new ZombieExplosion(x, y));
    }

    private void drawZombies(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor3f(1, 1, 1);

        for (int i = 0; i < zombies.size(); i++) {
            Zombie z = zombies.get(i);
            if (!z.active) continue;

            Texture tex;
            if (z.state == 2) {
                tex = zombieDeadTexture;
                if (System.currentTimeMillis() - z.deathStartTime > 500) z.active = false;
            } else {
                tex = (z.type == 0) ? zombieNormalTexture : zombieBigTexture;
            }

            if (tex != null) {
                gl.glMatrixMode(GL.GL_MODELVIEW);
                gl.glPushMatrix();

                gl.glTranslatef(z.x, z.y, 0);


                float scale = ZOMBIE_SCALE;
                if (z.type == 1) scale = 1.8f;
                gl.glScalef(scale, scale, 1.0f);

                tex.enable();
                tex.bind();

                float w = z.width;
                float h = z.height;

                gl.glBegin(GL.GL_QUADS);

                if (z.facingRight) {
                    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(0.0f, h);
                    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(w, h);
                    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(w, 0.0f);
                    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(0.0f, 0.0f);
                } else {
                    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(0.0f, h);
                    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(w, h);
                    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(w, 0.0f);
                    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(0.0f, 0.0f);
                }
                gl.glEnd();

                tex.disable();

                gl.glPopMatrix();
            }
        }

        zombies.removeIf(z -> !z.active);
        gl.glDisable(GL.GL_BLEND);
    }
    private void updateBullets(GL gl) {
        bullets.removeIf(b -> !b.active || b.x < -10 || b.x > 110);

        for (Bullet b : bullets) {
            if (b.active) {
                if (b.facingRight) b.x += 2.0f;
                else b.x -= 2.0f;
            }
        }
    }

    private void drawBullets(GL gl) {
        if (bulletTexture == null) return;
        bulletTexture.enable();
        bulletTexture.bind();
        gl.glBegin(GL.GL_QUADS);
        for (Bullet b : bullets) {
            if (b.active) {
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(b.x, b.y + b.height);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(b.x + b.width, b.y + b.height);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(b.x + b.width, b.y);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(b.x, b.y);
            }
        }
        gl.glEnd();
        bulletTexture.disable();
    }

    private void animateSprite(GL gl) {
        ArrayList<Texture> currentAnim;
        if (isShooting) {
            currentAnim = shootingTextures;
            if (System.currentTimeMillis() - shootingStartTime > 200) isShooting = false;
        } else if (isJumping && !jumpTextures.isEmpty()) {
            currentAnim = jumpTextures;
        } else if (isWalking) {
            currentAnim = walkingTextures;
        } else {
            currentAnim = idleTextures;
        }

        if (currentAnim.isEmpty()) return;

        if (System.currentTimeMillis() - lastFrameTime > 100) {
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
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(playerX, playerY + playerHeight);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(playerX + playerWidth, playerY + playerHeight);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(playerX + playerWidth, playerY);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(playerX, playerY);
            } else {
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(playerX, playerY + playerHeight);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(playerX + playerWidth, playerY + playerHeight);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(playerX + playerWidth, playerY);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(playerX, playerY);
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
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(currentX, 100);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(currentX + BG_WIDTH, 100);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(currentX + BG_WIDTH, 0);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(currentX, 0);
            gl.glEnd();
        }
        backgroundTexture.disable();
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
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
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(scoreBoardBounds.x, scoreBoardBounds.y + scoreBoardBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(scoreBoardBounds.x + scoreBoardBounds.width, scoreBoardBounds.y + scoreBoardBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(scoreBoardBounds.x + scoreBoardBounds.width, scoreBoardBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(scoreBoardBounds.x, scoreBoardBounds.y);
            gl.glEnd();
            scoreBoardTexture.disable();
        }
        drawNumber(gl, score, scoreBoardBounds.x + scoreBoardBounds.width + 1, scoreBoardBounds.y + 1, 4, 6, 3);

        if (timerBoardTexture != null) {
            timerBoardTexture.enable();
            timerBoardTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(timerBoardBounds.x, timerBoardBounds.y + timerBoardBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(timerBoardBounds.x + timerBoardBounds.width, timerBoardBounds.y + timerBoardBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(timerBoardBounds.x + timerBoardBounds.width, timerBoardBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(timerBoardBounds.x, timerBoardBounds.y);
            gl.glEnd();
            timerBoardTexture.disable();
        }
        drawNumber(gl, timerSeconds, timerBoardBounds.x + timerBoardBounds.width + 1, timerBoardBounds.y + 1, 4, 6, 2);

        if (!isPaused && pauseButtonTexture != null) {
            pauseButtonTexture.enable();
            pauseButtonTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y);
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
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(x, y + h);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(x + w, y + h);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(x + w, y);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(x, y);
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
                gl.glTexCoord2f(coords.left(), coords.top()); gl.glVertex2f(x + (i * width), y + height);
                gl.glTexCoord2f(coords.right(), coords.top()); gl.glVertex2f(x + (i * width) + width, y + height);
                gl.glTexCoord2f(coords.right(), coords.bottom()); gl.glVertex2f(x + (i * width) + width, y);
                gl.glTexCoord2f(coords.left(), coords.bottom()); gl.glVertex2f(x + (i * width), y);
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
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(x, y + h);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(x + w, y + h);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(x + w, y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(x, y);
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
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(gamePausedBounds.x, gamePausedBounds.y + gamePausedBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(gamePausedBounds.x + gamePausedBounds.width, gamePausedBounds.y + gamePausedBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(gamePausedBounds.x + gamePausedBounds.width, gamePausedBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(gamePausedBounds.x, gamePausedBounds.y);
            gl.glEnd();
            gamePausedTexture.disable();
        }

        if (continueTexture != null) {
            continueTexture.enable();
            continueTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y + continueBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y + continueBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y);
            gl.glEnd();
            continueTexture.disable();
        }

        if (exitTexture != null) {
            exitTexture.enable();
            exitTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y + exitBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y + exitBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y);
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
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(0.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 0.0f);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(0.0f, 0.0f);
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
            menuRenderer.draw(isWin ? "YOU SURVIVED!" : "ZOMBIE FOOD!", width / 2, height / 2);
            menuRenderer.endRendering();
        }

        ScoreRenderer.beginRendering(width, height);
        menuRenderer.beginRendering(width, height);
        menuRenderer.setColor(Color.RED);
        menuRenderer.draw("Score: " + score, (width / 2) - 80, (height / 2) - 50);
        menuRenderer.setColor(Color.YELLOW);
        menuRenderer.draw("Press ENTER to return", (width / 2) - 120, (height / 2) - 100);
        menuRenderer.endRendering();
        ScoreRenderer.endRendering();
    }

    private void checkGameStatus() {
        if (playerHealth <= 0) {
            playerHealth = 0;
            isGameRunning = false;
            isWin = false;
            Sound.stop();
            Sound.playSound("Assets/Sounds/game_over.wav");
        }

        if (timerSeconds <= 0) {
            isGameRunning = false;
            isWin = true;
            Sound.stop();
            Sound.playSound("Assets/Sounds/victory.wav");
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
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

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
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
                facingRight = true;
                isWalking = true;
            }
            else if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (!isJumping) {
                    isJumping = true;
                    verticalVelocity = jumpStrength;
                    currentFrameIndex = 0;
                    Sound.playSound("Assets/Sounds/Reload.wav");
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                isShooting = true;
                shootingStartTime = System.currentTimeMillis();
                currentFrameIndex = 0;
                float bulletStartX = facingRight ? playerX + 8 : playerX - 1;
                bullets.add(new Bullet(bulletStartX, playerY + 8, facingRight));
                Sound.playSound("Assets/Sounds/shoot.wav");
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
            if (mouseX >= continueBtnBounds.x && mouseX <= continueBtnBounds.x + continueBtnBounds.width &&
                    mouseY >= continueBtnBounds.y && mouseY <= continueBtnBounds.y + continueBtnBounds.height)
                isPaused = false;
            else if (mouseX >= exitBtnBounds.x && mouseX <= exitBtnBounds.x + exitBtnBounds.width &&
                    mouseY >= exitBtnBounds.y && mouseY <= exitBtnBounds.y + exitBtnBounds.height) {
                myFrame.dispose();
                if (animator != null) animator.stop();
                new GameApp();
            }
        } else {
            if (mouseX >= pauseGameBtnBounds.x && mouseX <= pauseGameBtnBounds.x + pauseGameBtnBounds.width &&
                    mouseY >= pauseGameBtnBounds.y && mouseY <= pauseGameBtnBounds.y + pauseGameBtnBounds.height)
                isPaused = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}