package ru.kpfu.itis.Control;

import ru.kpfu.itis.Exceptions.IllegalFilePathException;
import ru.kpfu.itis.Utilities.Utilities;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AudioSave implements Runnable {

    private AudioInputStream ais;
    private AudioFileFormat.Type type;
    private File outFile;               //output audio file


    //type should be wav, because I haven't done it yet
    public AudioSave(String filePath, InputStream in, AudioFileFormat.Type type, long length) throws IllegalFilePathException{

        this.type = type;
        outFile = new File(filePath);

        if (!outFile.exists()){
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalFilePathException("File path is not illegal!");
            }
        }

        AudioFormat audioFormat = Utilities.getAudioFormat();

        ais = new AudioInputStream(in,audioFormat,length);

        Thread t = new Thread(this);
        t.start();
    }


    @Override
    public void run() {

        try {
            AudioSystem.write(ais,type,outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
