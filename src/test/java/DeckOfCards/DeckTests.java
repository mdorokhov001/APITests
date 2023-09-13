package DeckOfCards;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

public class DeckTests {

    static String deckID = null;
    static String pileName = "drawnCards";
    static String card1Code = null;
    static String card2Code = null;

    @Test
    @DisplayName("Create a deck")
    public void test1() throws IOException {
        URL baseURL = new URL("https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1");
        HttpURLConnection connection = (HttpURLConnection) baseURL.openConnection();
        connection.setRequestMethod("GET");
       // connection.setRequestProperty();

        String content = readContent(connection);
        System.out.println("created desk: " + content);

        int b = content.indexOf("deck") + 11;
        int e = content.indexOf("remaining") - 4;
        deckID = content.substring(b,e);
      //  System.out.println(deckID);
    }

    @Test
    @DisplayName("Shuffle current deck")
    public void test2() throws IOException {
        if (deckID != null) {
            URL baseURL = new URL("https://deckofcardsapi.com/api/deck/" + deckID + "/shuffle/");
            HttpURLConnection connection = (HttpURLConnection) baseURL.openConnection();
            connection.setRequestMethod("GET");
            // connection.setRequestProperty();

            String content = readContent(connection);
            System.out.println("shuffled desk: " + content);
        } else {
            System.out.println("deck hasn't been shuffled");
        }
    }

    @Test
    @DisplayName("drawOneCard")
    public void test3() throws IOException {
        if (deckID != null) {
            URL baseURL = new URL("https://deckofcardsapi.com/api/deck/" + deckID + "/draw/?count=1");
            HttpURLConnection connection = (HttpURLConnection) baseURL.openConnection();
            connection.setRequestMethod("GET");
            // connection.setRequestProperty();

            String content = readContent(connection);
            System.out.println("drawn card: " + content);
            int b = content.indexOf("image") + 9;
            int e = content.indexOf("images") - 4;
            String link = content.substring(b,e);
            printCard(link);

            b = content.indexOf("code") + 8;
            e = content.indexOf("image") - 4;
            card1Code = content.substring(b,e);

        } else {
            System.out.println("card hasn't been drawn");
        }
    }

    @Test
    @DisplayName("addCardToPile")
    public void test4() throws IOException {
        if (deckID != null) {
            URL baseURL = new URL("https://deckofcardsapi.com/api/deck/" + deckID + "/pile/"+ pileName + "/add/?cards=" + card1Code);
            HttpURLConnection connection = (HttpURLConnection) baseURL.openConnection();
            connection.setRequestMethod("GET");
            // connection.setRequestProperty();

            String content = readContent(connection);
            System.out.println("added card to pile: " + content);

        } else {
            System.out.println("card hasn't been added to pile");
        }
    }

    public String readContent(HttpURLConnection connection) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            content.append(inputLine);
        }
        br.close();
        Map<String, List<String>> headerMap = connection.getHeaderFields();
        for (Map.Entry item : headerMap.entrySet()) {
            System.out.println(item.getKey() + " " + item.getValue());
        }
        Assertions.assertTrue(content.toString().contains("\"success\": true"));
        return content.toString();
    }

    public void printCard(String link){
        try{
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(link));
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
