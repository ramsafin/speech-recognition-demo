package ru.kpfu.itis.SpeechRecogmition;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.kpfu.itis.Exceptions.RecognitionSpeechException;
import ru.kpfu.itis.Exceptions.TextRecognitionException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

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
     * @throws RecognitionSpeechException
     */

    public static ArrayList<String> sendPOST(byte[] bytes) throws RecognitionSpeechException {

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
                    sb.append(s);
                }

                return getInternalXMLText(sb);
            }

        }catch (Exception e){
            throw new RecognitionSpeechException(e.getMessage());
        }
    }

    /**
     * This part is for sending text and getting bytes
     * Then it will be saved into .wav file
     */

    private static String speaker = "zahar"; // omazh, zahar , ermil, jane | zahar by default
    private static String format = "wav";   //mp3
    private static String emotion = "evil"; //neutral, evil, mixed
    private static String robot = "false";  //true
    private static String textRecognitionURI = "https://tts.voicetech.yandex.net/generate?";


    public static void setSpeaker(String speaker) {
        SpeechKit.speaker = speaker;
    }

    public static String[] getSpeakers() {
        return new String[]{"zahar","omazh","ermil","jane"};
    }

    public static String[] getEmotions() {
        return new String[]{"neutral","mixed","evil","good"};
    }

    public static void setEmotion(String emotion) {
        SpeechKit.emotion = emotion;
    }

    public static byte[] sendGET(String textToSend) throws TextRecognitionException {

        try{

            System.out.println(emotion);
            System.out.println(speaker);

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

    private static String expression = "//recognitionResults";

    private static ArrayList<String> getInternalXMLText(StringBuilder xml) throws RecognitionSpeechException {

        ArrayList<String> arrayList = new ArrayList<>();

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(xml.toString().getBytes("UTF-8"));

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(in);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(SpeechKit.expression);

            NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                arrayList.add(nodeList.item(i).getTextContent());
            }

        } catch (ParserConfigurationException | XPathExpressionException | SAXException | IOException e) {
            throw new RecognitionSpeechException(e.getMessage());
        }


        return arrayList;
    }

}
