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

public class GameGlListener implements GLEventListener, KeyListener, MouseListener {

    class Bullet {
        float x, y;
        boolean facingRight;
        boolean active;

        public Bullet(float x, float y, boolean facingRight) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.active = true;
        }
    }

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

    private Texture winTexture;
    private Texture loseTexture;
    private Texture Enter;
    private Texture To;
    private Texture Exit;


    Texture[] numbersTextures = new Texture[10];
    Texture[] healthImages = new Texture[6];
    int playerHealth = 78;
    ArrayList<Texture> idleTextures = new ArrayList<>();
    ArrayList<Texture> walkingTextures = new ArrayList<>();
    ArrayList<Texture> shootingTextures = new ArrayList<>();
    ArrayList<Texture> jumpTextures = new ArrayList<>();
    Texture bulletTexture;

    float playerX = 10;
    float playerY = 15;
    float groundLevel = 15;

    // Movement Flags
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean isWalking = false;

    // Jump Physics
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

    Rectangle continueBtnBounds = new Rectangle(35, 50, 30, 10);
    Rectangle exitBtnBounds = new Rectangle(35, 35, 30, 10);
    Rectangle gamePausedBounds = new Rectangle(25, 70, 50, 15);

    Rectangle pauseGameBtnBounds = new Rectangle(82, 88, 16, 8);
    Rectangle scoreBoardBounds = new Rectangle(2, 88, 20, 8);
    Rectangle timerBoardBounds = new Rectangle(40, 88, 20, 8);

    public GameGlListener(String difficulty) {
        this.difficultyLevel = difficulty;
//        timerSeconds = 0;

        if (difficulty.equals("Easy")) {
            timerSeconds = 60;
        } else if (difficulty.equals("Medium")) {
            timerSeconds = 30;
        } else if (difficulty.equals("Hard")) {
            timerSeconds = 3;
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
            File bgFile = new File("Assets/Background.png");
            backgroundTexture = TextureIO.newTexture(bgFile, true);

            File pauseFile = new File("Assets/Pauseboard (1).png");
            pauseButtonTexture = TextureIO.newTexture(pauseFile, true);

            File scoreFile = new File("Assets/scoreboard (1).png");
            scoreBoardTexture = TextureIO.newTexture(scoreFile, true);

            File timerFile = new File("Assets/timerboard (1).png");
            timerBoardTexture = TextureIO.newTexture(timerFile, true);

            File gpFile = new File("Assets/gamepausedboard (1).png");
            gamePausedTexture = TextureIO.newTexture(gpFile, true);

            File contFile = new File("Assets/continue (1).png");
            continueTexture = TextureIO.newTexture(contFile, true);

            File exFile = new File("Assets/exitboard (1).png");
            exitTexture = TextureIO.newTexture(exFile, true);


            winTexture = TextureIO.newTexture(new File("Assets/youwin.png"), true);
            loseTexture = TextureIO.newTexture(new File("Assets/youlose.png"), true);
            Enter = TextureIO.newTexture(new File("Assets/word enter.png"), true);
            To = TextureIO.newTexture(new File("Assets/word to.png"), true);
            Exit = TextureIO.newTexture(new File("Assets/exit.png"), true);
            backgroundTexture = TextureIO.newTexture(bgFile, true);

            for (int i = 0; i < 10; i++) {
                File numFile = new File("Assets/numbers board (" + i + ").png");
                numbersTextures[i] = TextureIO.newTexture(numFile, true);
            }
            for (int i = 0; i < 5; i++) {
                File numFile = new File("Assets/helthbar/" + i + ".png");
                healthImages[i] = TextureIO.newTexture(numFile, true);
            }

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
        } else {
            renderEndScreen(gl, drawable.getWidth(), drawable.getHeight());
        }
        if (isPaused) {
            drawPauseMenu(gl, drawable);
        } else {
            updateTimer();
        }
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

        // رسم النصوص الأخرى
        ScoreRenderer.beginRendering(width, height);
        menuRenderer.beginRendering(width, height);

        menuRenderer.setColor(Color.WHITE);
        menuRenderer.draw("Score: " + playerHealth, (width / 2) - 80, (height / 2) - 50);
        menuRenderer.setColor(Color.YELLOW);


        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        float btnSize = 0.2f;
        float startX = 0.27f;
        float posY = 0.25f;
        float spacing = 0.12f;

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, 1, 0, 1, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        Texture[] buttons = {Enter, To, Exit};
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
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);

        menuRenderer.endRendering();
        ScoreRenderer.endRendering();
    }

    private void checkGameStatus() {
        if (playerHealth <= 0) {
            isGameRunning = false;
            isWin = false;
        }
        if (timerSeconds <= 0) {
            isGameRunning = false;
            isWin = true;
        }
    }


    private void drawBackground(GL gl) {
        if (backgroundTexture == null) return;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, 1, 0, 1, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glDisable(GL.GL_DEPTH_TEST);

        gl.glColor3f(1, 1, 1);
        backgroundTexture.enable();
        backgroundTexture.bind();

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

        if (isGameOver) {
            textRenderer.beginRendering(width, height);
            textRenderer.setColor(Color.RED);
//            textRenderer.draw("GAME OVER!", width / 2 - 50, height / 2);
            textRenderer.endRendering();
        }
    }

    private void drawHealthBar(GL gl) {
        if (healthImages != null) {

            int index = playerHealth;

            if (playerHealth >= 80) {
                index = 5;
            } else if (playerHealth >= 60) {
                index = 4;
            } else if (playerHealth >= 40) {
                index = 3;
            } else if (playerHealth >= 20) {
                index = 2;
            } else if (playerHealth >= 5) {
                index = 1;
            } else {
                index = 0;
            }

            if (healthImages[index] != null) {
                float x = 2;
                float y = 82;
                float w = 30;
                float h = 8;

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
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(x + (i * width), y + height);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(x + (i * width) + width, y + height);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(x + (i * width) + width, y);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(x + (i * width), y);
                gl.glEnd();
                tex.disable();
            }
        }
    }

    private void drawPauseMenu(GL gl, GLAutoDrawable drawable) {
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
    }

    private void drawGame(GL gl) {
        if (!isPaused && !isGameOver) {

            if (leftPressed) {
                playerX -= 0.8f;
                facingRight = false;
            }
            if (rightPressed) {
                playerX += 0.8f;
                facingRight = true;
            }

            final float PLAYER_WIDTH = 10f;
            final float MAX_X = 100f - PLAYER_WIDTH;

            if (playerX < 0) {
                playerX = 0;
            }

            if (playerX > MAX_X) {
                playerX = MAX_X;
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

            animateSprite(gl);
            updateBullets(gl);
        }
    }

    private void animateSprite(GL gl) {
        ArrayList<Texture> currentAnim;

        if (isShooting) {
            currentAnim = shootingTextures;
            if (System.currentTimeMillis() - shootingStartTime > 300) {
                isShooting = false;
            }
        } else if (isJumping && !jumpTextures.isEmpty()) {
            currentAnim = jumpTextures;
        } else if (isWalking) {
            currentAnim = walkingTextures;
        } else {
            currentAnim = idleTextures;
        }

        if (currentAnim.isEmpty()) return;

        if (System.currentTimeMillis() - lastFrameTime > 75) {
            currentFrameIndex++;
            lastFrameTime = System.currentTimeMillis();
        }

        if (currentFrameIndex >= currentAnim.size()) {
            currentFrameIndex = 0;
        }

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
                gl.glVertex2f(playerX, playerY + 15);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(playerX + 10, playerY + 15);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(playerX + 10, playerY);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(playerX, playerY);
            } else {
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(playerX, playerY + 15);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(playerX + 10, playerY + 15);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(playerX + 10, playerY);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(playerX, playerY);
            }
            gl.glEnd();
            frame.disable();
        }
    }

    private void updateBullets(GL gl) {
        if (bulletTexture == null) return;

        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if (b.active) {
                if (b.facingRight) b.x += 2;
                else b.x -= 2;

                if (b.x < 0 || b.x > 100) b.active = false;

                bulletTexture.enable();
                bulletTexture.bind();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex2f(b.x, b.y + 3);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex2f(b.x + 5, b.y + 3);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex2f(b.x + 5, b.y);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex2f(b.x, b.y);
                gl.glEnd();
                bulletTexture.disable();
            }
        }
    }

    private void updateTimer() {
        if (!isGameOver && System.currentTimeMillis() - lastTime > 1000) {
            timerSeconds--;
            lastTime = System.currentTimeMillis();
            if (timerSeconds <= 0) {
                isGameOver = true;
            }
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
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = !isPaused;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
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
                currentFrameIndex = 0; // Reset animation to start
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isShooting = true;
            shootingStartTime = System.currentTimeMillis();
            currentFrameIndex = 0; // Reset animation to start
            float bulletStartX = facingRight ? playerX + 8 : playerX - 1;
            bullets.add(new Bullet(bulletStartX, playerY + 8, facingRight));
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (isPaused) {
                myFrame.dispose();
                new GameApp();
            } else {
                isPaused = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }

        isWalking = leftPressed || rightPressed;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        double w = glCanvas.getWidth();
        double h = glCanvas.getHeight();
        double mouseX = (e.getX() / w) * 100.0;
        double mouseY = ((h - e.getY()) / h) * 100.0;

        if (isPaused) {
            if (mouseX >= continueBtnBounds.x && mouseX <= continueBtnBounds.x + continueBtnBounds.width &&
                    mouseY >= continueBtnBounds.y && mouseY <= continueBtnBounds.y + continueBtnBounds.height) {
                isPaused = false;
            } else if (mouseX >= exitBtnBounds.x && mouseX <= exitBtnBounds.x + exitBtnBounds.width &&
                    mouseY >= exitBtnBounds.y && mouseY <= exitBtnBounds.y + exitBtnBounds.height) {
                myFrame.dispose();
                if (animator != null) animator.stop();
                new GameApp();
            }
        } else {
            if (mouseX >= pauseGameBtnBounds.x && mouseX <= pauseGameBtnBounds.x + pauseGameBtnBounds.width &&
                    mouseY >= pauseGameBtnBounds.y && mouseY <= pauseGameBtnBounds.y + pauseGameBtnBounds.height) {
                isPaused = true;
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