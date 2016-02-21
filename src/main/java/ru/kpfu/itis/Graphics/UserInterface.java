package ru.kpfu.itis.Graphics;

import ru.kpfu.itis.Control.AudioCapture;
import ru.kpfu.itis.Control.AudioPlay;
import ru.kpfu.itis.Control.AudioSave;
import ru.kpfu.itis.Exceptions.IllegalFilePathException;
import ru.kpfu.itis.Exceptions.TextRecognitionException;
import ru.kpfu.itis.SpeechRecogmition.SpeechKit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

public class UserInterface extends JFrame {

    private JTabbedPane pane;

    private JPanel recordPanel;
    private JPanel textAudioPanel;

    //textAudioPanel
    private JButton listenBtn;
    private JTextArea textArea;


    //record panel
    private JButton startBtn;
    private JButton stopBtn;
    private JButton playBtn;
    private JButton saveBtn;

    private AudioCapture audioCapture;
    private AudioPlay audioPlay;
    private AudioSave audioSave;


    public UserInterface() throws LineUnavailableException {

        super("Audio panel");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setBounds(500,300,350,200);

        pane = new JTabbedPane();

        initRecordPanel();
        initTextPanel();

        this.add(pane);
        this.setVisible(true);
    }

    private void initTextPanel() {

        listenBtn = new JButton("listen");
        textArea = new JTextArea(5,40);
        textArea.setEnabled(true);
        textArea.setVisible(true);
        textArea.setLineWrap(true);
        textArea.setTabSize(4);
        textArea.setWrapStyleWord(true);
        textArea.setAutoscrolls(true);
        textArea.setEditable(true);

        textAudioPanel = new JPanel(new BorderLayout(20,20));
        textAudioPanel.add(listenBtn,BorderLayout.SOUTH);
        textAudioPanel.add(textArea,BorderLayout.CENTER);

        listenBtn.addActionListener(e->{
            String text = textArea.getText();
            if (text == null || text.equals("")){
                JOptionPane.showMessageDialog(this,"too few symbols in text");
                return;
            }

            try {
                byte [] bytes = SpeechKit.sendGET(text);
                AudioPlay play = new AudioPlay(bytes);
            } catch (TextRecognitionException | LineUnavailableException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage());
                e1.printStackTrace();
            }
        });

        pane.addTab("listen text",textAudioPanel);
    }


    private void initRecordPanel(){

        startBtn = new JButton("start");
        stopBtn = new JButton("stop");
        playBtn = new JButton("play");
        saveBtn = new JButton("save");

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        saveBtn.setEnabled(false);

        saveBtn.setPreferredSize(new Dimension(60,40));
        playBtn.setPreferredSize(new Dimension(60,40));
        stopBtn.setPreferredSize(new Dimension(60,40));
        startBtn.setPreferredSize(new Dimension(60,40));

        startBtn.addActionListener(getStartListener());
        stopBtn.addActionListener(getStopListener());
        playBtn.addActionListener(getPlayListener());
        saveBtn.addActionListener(getSaveListener());


        recordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,20));

        recordPanel.add(startBtn);
        recordPanel.add(stopBtn);
        recordPanel.add(playBtn);
        recordPanel.add(saveBtn);

        pane.addTab("recording and saving",recordPanel);
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
                JOptionPane.showMessageDialog(this,e1.getMessage());
                e1.printStackTrace();
            }
        };
    }



    private ActionListener getPlayListener(){
        return e->{

            try {
                audioPlay = new AudioPlay(audioCapture.getAudioBytes());

            } catch (LineUnavailableException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage());
                e1.printStackTrace();
            }

        };
    }

    //saves only in wav
    private ActionListener getSaveListener(){
        return e->{

            long length = audioCapture.getLength();
            ByteArrayInputStream in = new ByteArrayInputStream(audioCapture.getAudioBytes());
            try {
                String path = JOptionPane.showInputDialog(this,"Enter file path to save");
                if (path == null|| path.equals("")){
                    return;
                }
                audioSave = new AudioSave(path,in, AudioFileFormat.Type.WAVE,length);

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
