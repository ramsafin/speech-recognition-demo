package ru.kpfu.itis.Graphics;

import ru.kpfu.itis.Control.AudioCapture;
import ru.kpfu.itis.Control.AudioPlay;
import ru.kpfu.itis.Control.AudioSave;
import ru.kpfu.itis.Exceptions.*;
import ru.kpfu.itis.SpeechRecogmition.SpeechKit;
import ru.kpfu.itis.Utilities.Utilities;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class UserInterface extends JFrame {

    private JTabbedPane pane;

    private JPanel recordPanel;
    private JPanel textAudioPanel;

    //textAudioPanel
    private JButton listenBtn;
    private JTextArea textArea;
    private JComboBox<String> speakerBox;
    private JComboBox<String> emotionBox;


    //record panel
    private JButton captureBtn;
    private JButton stopBtn;
    private JButton playBtn;
    private JButton saveBtn;

    private AudioCapture audioCapture;
    private AudioPlay audioPlay;
    private AudioSave audioSave;

    //recognition
    private JButton recognizeBtn;


    //setting panel
    private JPanel micPanel;
    private JPanel audioPanel;
    private JComboBox<Mixer.Info> micInfo;
    private JComboBox<Mixer.Info> audioInfo;
    private JPanel panel;


    //key
    private JPanel keySettingPanel;
    private JTextArea keyTextArea;
    private JButton changeKey;


    public UserInterface() throws LineUnavailableException {

        super("Audio panel");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setBounds(500,300,400,240);

        pane = new JTabbedPane();

        initKeyPanel();
        initSettingPanel();
        initRecordPanel();
        initTextPanel();

        this.add(pane);
        this.setVisible(true);
    }


    private ActionListener getChangeKeyListener(){
        return e->{
            String s = JOptionPane.showInputDialog(this,"Enter your SpeechKit API KEY");
            if (s == null || s.length() <= 0){
                return;
            }
            SpeechKit.setKEY(s);
            keyTextArea.setText(s);
        };
    }

    private void initKeyPanel(){
        keySettingPanel = new JPanel(new BorderLayout(20,20));

        JPanel center = new JPanel();

        keyTextArea = new JTextArea(6,32);
        keyTextArea.setEditable(false);
        changeKey = new JButton("change");

        changeKey.addActionListener(getChangeKeyListener());

        center.add(keyTextArea,BorderLayout.CENTER);
        center.add(changeKey,BorderLayout.SOUTH);

        keySettingPanel.add(center,BorderLayout.CENTER);
        pane.addTab("SpeechKit API's key",keySettingPanel);
    }


    private void initSettingPanel(){

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        initMic();
        initAudio();

        pane.addTab("Setting",panel);
    }



    private void initAudio() {

        audioPanel = new JPanel(new BorderLayout(50,90));

        audioPanel.setBorder(new TitledBorder("Audio device : "));

        audioInfo = new JComboBox<>(getMixerInfo(SourceDataLine.class));

        audioPanel.add(audioInfo,BorderLayout.CENTER);

        panel.add(audioPanel);

    }

    private void initMic() {

        micPanel = new JPanel(new BorderLayout(50,90));

        micPanel.setBorder(new TitledBorder("Microphone device : "));

        micInfo = new JComboBox<>(getMixerInfo(TargetDataLine.class));

        micPanel.add(micInfo,BorderLayout.CENTER);

        panel.add(micPanel);
    }


    private Mixer.Info[] toMixerArray(ArrayList<Mixer> arrayList){

        Mixer.Info [] mixers = new Mixer.Info[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {
            mixers[i] = arrayList.get(i).getMixerInfo();
        }

        return mixers;
    }


    private Mixer.Info[] getMixerInfo(Class<?> c){

        Mixer.Info infos[] = AudioSystem.getMixerInfo();

        DataLine.Info lineInfo = new DataLine.Info(c, Utilities.getAudioFormat());

        ArrayList<Mixer> arrayList = new ArrayList<>(3);

        for (Mixer.Info i : infos){
            Mixer mixer = AudioSystem.getMixer(i);
            if (mixer.isLineSupported(lineInfo)){
                arrayList.add(mixer);
            }
        }

        return toMixerArray(arrayList);
    }



    private void initTextPanel() {

        listenBtn = new JButton("listen");
        listenBtn.addActionListener(getListenListener());


        textArea = new JTextArea(5,40);
        textArea.setEnabled(true);
        textArea.setVisible(true);
        textArea.setLineWrap(true);
        textArea.setTabSize(4);
        textArea.setWrapStyleWord(true);
        textArea.setAutoscrolls(true);
        textArea.setEditable(true);
        textAudioPanel = new JPanel(new BorderLayout(20,20));

        JPanel p = new JPanel(new BorderLayout(20,5));

        speakerBox = new JComboBox<>(SpeechKit.getSpeakers());
        emotionBox = new JComboBox<>(SpeechKit.getEmotions());

        p.add(textArea,BorderLayout.CENTER);

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2,BoxLayout.Y_AXIS));

        p2.add(speakerBox);
        p2.add(emotionBox);

        p.add(p2,BorderLayout.SOUTH);

        textAudioPanel.add(listenBtn,BorderLayout.SOUTH);
        textAudioPanel.add(p,BorderLayout.CENTER);


        pane.addTab("listen text",textAudioPanel);
    }



    private ActionListener getListenListener(){
        return e->{

            String text = textArea.getText();

            if (text == null || text.equals("")){
                JOptionPane.showMessageDialog(this,"too few symbols in text");
                return;
            }

            try {

                //setting up emotion
                SpeechKit.setEmotion((String) emotionBox.getSelectedItem());

                //setting up speaker
                SpeechKit.setSpeaker((String) speakerBox.getSelectedItem());

                //waiting for the audio bytes
                byte [] bytes = SpeechKit.sendGET(text);

                new AudioPlay(bytes, (Mixer.Info) audioInfo.getSelectedItem());

            } catch (TextRecognitionException | AudioPlayException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage(),"exception",JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        };
    }



    private void initRecordPanel(){

        captureBtn = new JButton("record");
        stopBtn = new JButton("stop");
        playBtn = new JButton("play");
        saveBtn = new JButton("save");
        recognizeBtn = new JButton("recognize");


        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        saveBtn.setEnabled(false);

        saveBtn.setPreferredSize(new Dimension(60,40));
        playBtn.setPreferredSize(new Dimension(60,40));
        stopBtn.setPreferredSize(new Dimension(60,40));
        captureBtn.setPreferredSize(new Dimension(60,40));

        captureBtn.addActionListener(getStartListener());
        stopBtn.addActionListener(getStopListener());
        playBtn.addActionListener(getPlayListener());
        saveBtn.addActionListener(getSaveListener());

        recognizeBtn.addActionListener(getRecognizeListener());


        recordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,20));

        recordPanel.add(captureBtn);
        recordPanel.add(stopBtn);
        recordPanel.add(playBtn);
        recordPanel.add(saveBtn);
        recordPanel.add(recognizeBtn);

        pane.addTab("Recording - Saving - Recognition",recordPanel);
    }




    private ActionListener getRecognizeListener(){
        return e->{

            if (audioCapture.isCapture()){
                return;
            }

            if (audioCapture == null){
                JOptionPane.showMessageDialog(this,"You should record voice at first");
                return;
            }
            byte[] data = audioCapture.getAudioBytes();
            if (data.length > 0){

                try {
                    ArrayList<String> recText = SpeechKit.sendPOST(data);
                    JOptionPane.showMessageDialog(this,formatText(recText),"recognized text",JOptionPane.INFORMATION_MESSAGE);

                } catch (RecognitionSpeechException e1) {
                    JOptionPane.showMessageDialog(this,e1.getMessage(),"exception",JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }

            }else {
                JOptionPane.showMessageDialog(this,"You should record some audio before sending!");
            }
        };
    }

    private String formatText(ArrayList<String> text){
        int i = 1;
        StringBuilder s = new StringBuilder();
        for (String t : text){
            s.append(i++).append(") ").append(t).append("\n");
        }
        return s.toString();
    }


    private ActionListener getStartListener(){
        return e->{

            if (audioPlay != null && audioPlay.isAudioPlay()){
                return;
            }

            if (audioSave != null && audioSave.isSave()){
                return;
            }

            captureBtn.setEnabled(false);
            playBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            saveBtn.setEnabled(false);

            try {
                audioCapture = new AudioCapture((Mixer.Info) micInfo.getSelectedItem());
            } catch (AudioCaptureException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage(),"exception",JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        };
    }



    private ActionListener getPlayListener(){
        return e->{


            try {
                audioPlay = new AudioPlay(audioCapture.getAudioBytes(), (Mixer.Info) audioInfo.getSelectedItem());

            } catch (AudioPlayException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage(),"exception",JOptionPane.ERROR_MESSAGE);
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

            } catch (AudioSaveException e1) {
                JOptionPane.showMessageDialog(this,e1.getMessage(),"exception",JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }

            JOptionPane.showMessageDialog(this,"The music has been saved!");

        };
    }



    private ActionListener getStopListener(){
        return e->{
            captureBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            saveBtn.setEnabled(true);

            //stop capturing
            audioCapture.setCapture(false);
        };
    }



}
