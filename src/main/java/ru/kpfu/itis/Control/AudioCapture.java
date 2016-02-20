package ru.kpfu.itis.Control;

import ru.kpfu.itis.Utilities.Utilities;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class AudioCapture implements Runnable {

    private boolean isCapture;

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    private byte [] buffer;
    private ByteArrayOutputStream out; //Thread safe stream


    public AudioCapture() throws LineUnavailableException {

        audioFormat = Utilities.getAudioFormat();
        isCapture = true;
        Mixer.Info mixerInfo = Utilities.getRecordMixerInfo();
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat,mixerInfo);
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        buffer = new byte[(int) (audioFormat.getFrameSize() * audioFormat.getSampleRate())];

        out = new ByteArrayOutputStream();

        Thread t = new Thread(this);
        t.start();

    }


    public byte[] getAudioBytes(){
        return out.toByteArray();
    }

    public long getLength(){

        return out.size();
    }


    public void setCapture(boolean capture) {
        isCapture = capture;
    }


    private void writeData(){
        int count = targetDataLine.read(buffer,0,buffer.length);
        if (count > 0){
            out.write(buffer,0,count);
        }
    }


    @Override
    public void run() {

        while (isCapture){

            writeData();
        }

    }


}