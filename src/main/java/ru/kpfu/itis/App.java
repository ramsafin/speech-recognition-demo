package ru.kpfu.itis;

import ru.kpfu.itis.Graphics.UserInterface;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

public class App {

    public static void main( String[] args )  {

        SwingUtilities.invokeLater(()->{
            try {
                new UserInterface();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        });

    }
}
