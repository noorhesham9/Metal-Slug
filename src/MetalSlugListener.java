import javax.media.opengl.*;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MetalSlugListener implements GLEventListener, KeyListener, MouseListener {

    JFrame myFrame;
    boolean isPaused = false;
    int timerSeconds = 30;
    long lastTime;
    boolean isGameOver = false;

    GLCanvas glCanvas;
    FPSAnimator animator;
    TextRenderer timerRenderer;
    TextRenderer menuRenderer;
    Texture backgroundTexture;

    Rectangle continueBtnBounds = new Rectangle(35, 50, 30, 10);
    Rectangle exitBtnBounds = new Rectangle(35, 35, 30, 10);
    Rectangle pauseGameBtnBounds = new Rectangle(92, 92, 4, 4);

    public MetalSlugListener() {
        GLCapabilities capabilities = new GLCapabilities();
        glCanvas = new GLCanvas(capabilities);
        glCanvas.addGLEventListener(this);
        glCanvas.addKeyListener(this);
        glCanvas.addMouseListener(this);

        myFrame = new JFrame("Metal Slug - Game Mode");
        myFrame.setLayout(new BorderLayout());
        myFrame.add(glCanvas, BorderLayout.CENTER);

        myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
            File bgFile = new File("Assets/background1.png");
            backgroundTexture = TextureIO.newTexture(bgFile, true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        timerRenderer = new TextRenderer(new Font("Stencil", Font.BOLD, 40));
        menuRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 28));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        drawBackground(gl);
        drawGame(gl);
        drawHUD(gl, drawable);

        if (isPaused) {
            drawPauseMenu(gl, drawable);
        } else {
            updateTimer();
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
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(0.0f, 0.0f);
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

        timerRenderer.beginRendering(width, height);
        timerRenderer.setColor(Color.BLACK);
        timerRenderer.draw("" + timerSeconds, (width / 2) + 2, height - 52);
        timerRenderer.setColor(new Color(255, 215, 0));
        timerRenderer.draw("" + timerSeconds, width / 2, height - 50);
        timerRenderer.endRendering();

        if (!isPaused) {
            gl.glColor3f(0.8f, 0.8f, 0.8f);
            gl.glBegin(GL.GL_QUADS);
            gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y);
            gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y);
            gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glEnd();

            menuRenderer.beginRendering(width, height);
            menuRenderer.setColor(Color.BLACK);
            menuRenderer.draw("||", (int) (width * 0.928), (int) (height * 0.93));
            menuRenderer.endRendering();
        }

        if (isGameOver) {
            menuRenderer.beginRendering(width, height);
            menuRenderer.setColor(Color.RED);
            menuRenderer.draw("TIME UP!", width / 2 - 50, height / 2);
            menuRenderer.endRendering();
        }
    }

    private void drawPauseMenu(GL gl, GLAutoDrawable drawable) {
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.7f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0); gl.glVertex2f(100, 0);
        gl.glVertex2f(100, 100); gl.glVertex2f(0, 100);
        gl.glEnd();

        gl.glColor3f(0.2f, 0.6f, 0.2f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y);
        gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y);
        gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y + continueBtnBounds.height);
        gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y + continueBtnBounds.height);
        gl.glEnd();

        gl.glColor3f(0.6f, 0.2f, 0.2f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y);
        gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y);
        gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y + exitBtnBounds.height);
        gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y + exitBtnBounds.height);
        gl.glEnd();

        int w = drawable.getWidth();
        int h = drawable.getHeight();

        menuRenderer.beginRendering(w, h);
        menuRenderer.setColor(Color.WHITE);
        menuRenderer.draw("GAME PAUSED", w / 2 - 80, h / 2 + 100);
        menuRenderer.draw("CONTINUE", w / 2 - 60, h / 2 + 20);
        menuRenderer.draw("EXIT MENU", w / 2 - 60, h / 2 - 80);
        menuRenderer.endRendering();
    }

    private void drawGame(GL gl) {
    }

    private void updateTimer() {
        if (!isGameOver && System.currentTimeMillis() - lastTime > 1000) {
            timerSeconds--;
            lastTime = System.currentTimeMillis();
            if (timerSeconds <= 0) {
                timerSeconds = 0;
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
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = !isPaused;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (isPaused) {
                myFrame.dispose();
                new GameApp();
            } else {
                isPaused = true;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        double w = glCanvas.getWidth();
        double h = glCanvas.getHeight();
        double mouseX = (e.getX() / w) * 100.0;
        double mouseY = ((h - e.getY()) / h) * 100.0;

        if (isPaused) {
            if (mouseX > 30 && mouseX < 70 && mouseY > 45) {
                isPaused = false;
            }
            else if (mouseX > 30 && mouseX < 70 && mouseY <= 45 && mouseY > 20) {
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

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}