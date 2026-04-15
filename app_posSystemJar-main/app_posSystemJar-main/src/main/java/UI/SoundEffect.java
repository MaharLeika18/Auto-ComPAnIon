package UI;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class SoundEffect {

    public static void playClickSound() {
        String soundFilePath = "SoundEffect/SelectClick.wav";

        try (InputStream audioSrc = SoundEffect.class.getClassLoader().getResourceAsStream(soundFilePath);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc)) {

            if (audioStream == null) {
                System.err.println("Sound file not found: " + soundFilePath);
                return;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            System.err.println("Unsupported audio file format: " + soundFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("I/O error occurred while loading sound: " + soundFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error playing sound: " + soundFilePath);
        }
    }
}
