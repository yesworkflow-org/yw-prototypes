package org.yesworkflow.save;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpSaver implements Saver{

    CloseableHttpClient client;

    public HttpSaver(){
        client = HttpClients.custom()
                .build();
    }

    public void save()
    {
        Scanner scanner;

        try {
            HttpGet httpGet = new HttpGet("http://localhost:8000/save/ping");
            HttpResponse httpResponse = client.execute(httpGet);
            scanner = new Scanner(httpResponse.getEntity().getContent(), "UTF-8").useDelimiter("\\A");

            System.out.println(String.format("Status: %d %s", httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase())); ;

            System.out.println(String.format("Body:   %s", scanner.next()));
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        } finally {
            try{
                client.close();
            } catch(IOException ioe) {
                System.out.println("Error closing client: "  + ioe.getMessage());
            }
        }
    }
}
