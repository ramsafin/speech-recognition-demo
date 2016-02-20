package ru.kpfu.itis.Utilities;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class Utilities {

    /**
     * AudiFormat
     */

    private static final float SAMPLE_RATE = 16000.0f;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 2;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    public static AudioFormat getAudioFormat(){

        return new AudioFormat(SAMPLE_RATE,SAMPLE_SIZE_IN_BITS,CHANNELS,SIGNED,BIG_ENDIAN);
    }



    /**
     * Your mic device mixer
     * default 1
     */

    private static final int RECORD_MIXER_INFO = 1;  //в общем здесь ставь число от 0 до бесконечности пока не заработает play

    public static Mixer.Info getRecordMixerInfo(){

        return AudioSystem.getMixerInfo()[RECORD_MIXER_INFO];
    }


    /**
     * Play output mixer info
     * default 0
     */

    private static final int PLAY_MIXER_INFO = 0; //а здесь динамики если он у тебя есть конечно)

    public static Mixer.Info getPlayMixerInfo(){

        return AudioSystem.getMixerInfo()[PLAY_MIXER_INFO];
    }



}
