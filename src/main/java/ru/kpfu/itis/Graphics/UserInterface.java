package ru.kpfu.itis.Graphics;

import ru.kpfu.itis.Control.AudioCapture;
import ru.kpfu.itis.Control.AudioPlay;
import ru.kpfu.itis.Control.AudioSave;
import ru.kpfu.itis.Exceptions.IllegalFilePathException;
import ru.kpfu.itis.SpeachRecogmition.SpeechKit;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class UserInterface extends JFrame {

    private JPanel controlPanel;

    private JButton startBtn;
    private JButton stopBtn;
    private JButton playBtn;
    private JButton saveBtn;
    private JButton sendPostBtn;

    private AudioCapture audioCapture;
    private AudioPlay audioPlay;
    private AudioSave audioSave;


    public UserInterface() throws LineUnavailableException {

        super("Control panel");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setBounds(500,300,350,150);

        init();

        this.setVisible(true);
    }


    private void init(){

        startBtn = new JButton("start");
        stopBtn = new JButton("stop");
        playBtn = new JButton("play");
        saveBtn = new JButton("save");
        sendPostBtn = new JButton("send POST");

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        saveBtn.setEnabled(false);

        startBtn.addActionListener(getStartListener());
        stopBtn.addActionListener(getStopListener());
        playBtn.addActionListener(getPlayListener());
        saveBtn.addActionListener(getSaveListener());


        sendPostBtn.addActionListener(e->{
            try {
                SpeechKit.sendPOST(audioCapture.getAudioBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEADING,10,20));

        controlPanel.add(startBtn);
        controlPanel.add(stopBtn);
        controlPanel.add(playBtn);
        controlPanel.add(saveBtn);
        controlPanel.add(sendPostBtn);

        this.add(controlPanel);


    }


    private ActionListener getStartListener(){
        return e->{
            startBtn.setEnabled(false);
            playBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            saveBtn.setEnabled(false);

            try {
                audioCapture = new AudioCapture();
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }
        };
    }



    private ActionListener getPlayListener(){
        return e->{


            try {
                audioPlay = new AudioPlay(audioCapture.getAudioBytes());

            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }



        };
    }


    private ActionListener getSaveListener(){
        return e->{

            long length = audioCapture.getLength();
            ByteArrayInputStream in = new ByteArrayInputStream(audioCapture.getAudioBytes());
            try {
                audioSave = new AudioSave(JOptionPane.showInputDialog(this,"Enter file path to save"),in,length);
            } catch (IllegalFilePathException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage());
                e1.printStackTrace();
            }
            JOptionPane.showMessageDialog(this,"The music has been saved!");

        };
    }



    private ActionListener getStopListener(){
        return e->{
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            saveBtn.setEnabled(true);

            audioCapture.setCapture(false);
        };
    }

}
