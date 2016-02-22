package ru.kpfu.itis.Control;

import ru.kpfu.itis.Exceptions.AudioPlayException;
import ru.kpfu.itis.Utilities.Utilities;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This class plays audio
 */

public class AudioPlay implements Runnable {

    private boolean isAudioPlay;


    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;


    public AudioPlay(byte [] audioBytes, Mixer.Info info) throws AudioPlayException {

        try {

            audioFormat = Utilities.getAudioFormat();

            int length = audioBytes.length/audioFormat.getFrameSize();

            audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes),audioFormat,length);

            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat,info);

            sourceDataLine.open(audioFormat);

            sourceDataLine.start();

            isAudioPlay = true;

            Thread t = new Thread(this);
            t.start();

        }catch (LineUnavailableException e){
            throw new AudioPlayException("Can't play audio : " + e.getMessage());
        }
    }


    public boolean isAudioPlay() {
        return isAudioPlay;
    }

    @Override
    public void run() {

        byte buffer[] = new byte[(int) audioFormat.getSampleRate() * audioFormat.getFrameSize()];

        try {

            int count;
            while ((count = audioInputStream.read(buffer, 0, buffer.length)) > 0) {

                if (count > 0) {
                    sourceDataLine.write(buffer, 0, count);
                }

            }

            isAudioPlay = false;


        } catch (IOException e) {
            isAudioPlay = false;
            e.printStackTrace();
        }

        sourceDataLine.drain();

        sourceDataLine.close();
    }
}
