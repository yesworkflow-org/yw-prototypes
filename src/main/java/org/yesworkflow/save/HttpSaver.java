package org.yesworkflow.save;

import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpResponse;

public class HttpSaver implements Saver{

    IYwSerializer ywSerializer = null;
    IClient client = null;
    String baseURL = "https://localhost:8000/";
    String username = null;
    String title = "Title";
    String description = "Description";
    String graph = "";
    String model = "";
    String model_checksum = "";
    String recon = "";

    public HttpSaver(IYwSerializer ywSerializer){
        this.ywSerializer = ywSerializer;
    }

    public Saver build(String model, String graph, String recon)
    {
        this.model = model;
        this.graph = graph;
        this.recon = recon;
        return this;
    }


    public Saver save()
    {
        client = new YwClient(baseURL, ywSerializer);

        Scanner scanner;

        RunPOJO run = new RunPOJO(username, title, description, model, model_checksum, graph, recon);
        try {
            HttpResponse httpResponse = client.SaveRun(run);
            scanner = new Scanner(httpResponse.getEntity().getContent(), "UTF-8").useDelimiter("\\A");

            System.out.println(String.format("Status: %d %s", httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase()));

            System.out.println(String.format("Body:   %s", scanner.next()));
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }

        return this;
    }

    public Saver configure(Map<String, Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Saver configure(String key, Object value) throws Exception {
        switch(key.toLowerCase())
        {
            case "serveraddress":
                baseURL = (String) value;
                break;
            case "username":
                username = (String) value;
            default:
                break;
        }


        return this;
    }
}
