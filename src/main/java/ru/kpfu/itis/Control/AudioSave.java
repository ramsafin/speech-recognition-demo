package ru.kpfu.itis.Control;

import ru.kpfu.itis.Utilities.Utilities;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AudioSave implements Runnable {

    private boolean isSave;

    private AudioInputStream ais;
    private AudioFileFormat.Type type;
    private File outFile; //output audio file


    public AudioSave(File file, InputStream in, AudioFileFormat.Type type, long length) {

        this.type = type;
        outFile = file;

        AudioFormat audioFormat = Utilities.getAudioFormat();

        ais = new AudioInputStream(in, audioFormat, length);

        isSave = true;

        Thread t = new Thread(this);
        t.start();
    }


    public boolean isSave() {
        return isSave;
    }

    @Override
    public void run() {

        try {
            AudioSystem.write(ais, type, outFile);
            isSave = false;
        } catch (IOException e) {
            isSave = false;
            e.printStackTrace();
        }

    }
}
