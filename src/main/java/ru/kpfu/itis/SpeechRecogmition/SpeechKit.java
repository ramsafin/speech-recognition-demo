package ru.kpfu.itis.SpeechRecogmition;

import ru.kpfu.itis.Exceptions.RecognitionSpeechException;
import ru.kpfu.itis.Exceptions.TextRecognitionException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class SpeechKit {

    //my own key that's why you should use it carefully
    private static final String KEY = "f755d5ff-a1f0-47d2-b44a-233da815834c";

    //this is random hex
    private static final String UUID = "01ae13cb744628b58fb536d496daa1e6";

    //may be music,maps,notes,etc. see in yandex.ru
    private static final String TOPIC = "notes";

    //URI for speech recognition
    private static final String SpeechRecognitionURI = "https://asr.yandex.net/asr_xml?";


    /**
     * Sends post to the yandex and it will return xml with recognition results
     * @param bytes - audio bytes for sending
     * @return StringBuilder - xml with results
     * @throws IOException
     */

    public static StringBuilder sendPOST(byte[] bytes) throws RecognitionSpeechException {

        try{

            String params = "key="+KEY+"&uuid="+UUID+"&topic="+TOPIC;

            String uri = SpeechRecognitionURI+params;

            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();


            conn.setRequestMethod("POST");

            conn.addRequestProperty("Content-Type","audio/x-pcm;bit=16;rate=16000");   //see yandex.ru the advice this
            conn.addRequestProperty("Host","asr.yandex.net");
            conn.setDoOutput(true);

            //writing audio data
            try(DataOutputStream out = new DataOutputStream(conn.getOutputStream())){
                    out.write(bytes,0,bytes.length);
            } catch (IOException e) {
                throw new RecognitionSpeechException(e.getMessage());
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != 200){
                throw new RecognitionSpeechException("Can't send and recognize speech");
            }

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
                String s;
                StringBuilder sb = new StringBuilder();
                while ((s = reader.readLine()) != null){
                    System.out.println(s);
                    sb.append(s);
                }
                return sb;
            }

        }catch (Exception e){
            throw new RecognitionSpeechException(e.getMessage());
        }
    }

    /**
     * This part is for sending text and getting bytes
     * Then it will be saved into .wav file
     */

    private static String speaker = "zahar"; // omazh, zahar , ermil
    private static String format = "wav";   //mp3
    private static String emotion = "evil"; //neutral, evil, mixed
    private static String robot = "false";  //true
    private static String textRecognitionURI = "https://tts.voicetech.yandex.net/generate?";


    public static byte[] sendGET(String textToSend) throws TextRecognitionException {

        try{
            String text = URLEncoder.encode(textToSend,"UTF8");

            String params = "text=\""+text+"\"&format="+format+"&speaker="+speaker+"&key="+KEY+"&emotion="+emotion+"&robot="+robot;

            URL url = new URL(textRecognitionURI+params);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            byte [] buff = new byte[1024]; //buffer for reading bytes

            try(ByteArrayOutputStream res = new ByteArrayOutputStream();
                BufferedInputStream in = new BufferedInputStream(conn.getInputStream())
            ){
                int i;
                while ( (i = in.read(buff,0,buff.length)) > 0){
                    res.write(buff,0,i);
                }
                return res.toByteArray();
            }

        }catch (Exception e){
            throw new TextRecognitionException(e.getMessage());
        }
    }

}
