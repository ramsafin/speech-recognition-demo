package ru.kpfu.itis.Control;

import ru.kpfu.itis.Utilities.Utilities;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This class plays audio
 */

public class AudioPlay implements Runnable {


    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;


    public AudioPlay(byte [] audioBytes) throws LineUnavailableException {

        audioFormat = Utilities.getAudioFormat();

        int length = audioBytes.length/audioFormat.getFrameSize();

        audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes),audioFormat,length);

        Mixer.Info mixerInfo = Utilities.getPlayMixerInfo();

        sourceDataLine = AudioSystem.getSourceDataLine(audioFormat,mixerInfo);

        sourceDataLine.open(audioFormat);

        sourceDataLine.start();

        Thread t = new Thread(this);
        t.start();
    }





    @Override
    public void run() {

        byte buffer[] = new byte[(int) audioFormat.getSampleRate() * audioFormat.getFrameSize()];

        try {

            int count;
            while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {

                if (count > 0) {
                    sourceDataLine.write(buffer, 0, count);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        sourceDataLine.drain();

        sourceDataLine.close();
    }
}
