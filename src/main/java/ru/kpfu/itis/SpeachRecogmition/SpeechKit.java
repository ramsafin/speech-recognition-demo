package ru.kpfu.itis.SpeachRecogmition;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.kpfu.itis.Graphics.UserInterface;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class SpeechKit {

    private static final String KEY = "899eed12-3712-4151-a0ca-ba6fdbeba5c3";
    private static final String UUID = "01ae13cb744628b58fb536d496daa1e6"; //random
    private static final String TOPIC = "notes";
    private static final String URI = "https://asr.yandex.net/asr_xml?";
    private static final String PARAMS = "key="+KEY+"&uuid="+UUID+"&topic="+TOPIC;

    private static StringBuilder xml;



    public static void sendPOST(byte[] bytes) throws IOException {

        URL url = new URL(URI+PARAMS);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();


        conn.setRequestMethod("POST");
        conn.addRequestProperty("Content-Type","audio/x-pcm;bit=16;rate=16000");
        conn.addRequestProperty("Host","asr.yandex.net");
        conn.setDoOutput(true);


        try(DataOutputStream out = new DataOutputStream(conn.getOutputStream())){
            out.write(bytes,0,bytes.length);
        }

        int responseCode = conn.getResponseCode();

        if (responseCode != 200){
            xml = new StringBuilder();
            return;
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null){
                System.out.println(s);
                sb.append(s);
            }
            xml = sb;
        }
    }


    public static ArrayList<String> getValues(){

        ArrayList<String> result = new ArrayList<>();

        if (xml == null){return new ArrayList<>();}

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml.toString())));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression expression = xPath.compile("//recognitionResults[@success=\"1\"]/variant");
            NodeList node = (NodeList) expression.evaluate(doc,XPathConstants.NODESET);


            for (int i = 0; i < node.getLength(); i++) {
                result.add(node.item(i).getTextContent());
            }

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
        }

        return result;

    }


    //test
    public static void main(String[] args) {

        SwingUtilities.invokeLater(()->{
            try {
                new UserInterface();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        });
    }
}
