import javax.sound.sampled.*;
import java.io.File;

public class Sound {
    private static Clip bgMusic;
    private static boolean isMuted = false;
    public static void playSound(String filePath) {
        try { File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip(); clip.open(audioStream); clip.start(); }
        catch (Exception e) { System.out.println("Error playing sound: " + e.getMessage());
        } }
    public static void playBackground(String filePath) {
        if (isMuted) return;
        try {
            if (bgMusic != null && bgMusic.isRunning()) {
                return;
            }

            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
            bgMusic.start();

        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }


    public static void stop() {
        if (bgMusic != null) {
            bgMusic.stop();
        }
    }


    public static void toggleMute() {
        if (isMuted) {
            isMuted = false;
            if (bgMusic != null) bgMusic.start();
        } else {
            isMuted = true;
            if (bgMusic != null) bgMusic.stop();
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }
}
