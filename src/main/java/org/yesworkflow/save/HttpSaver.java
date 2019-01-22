package org.yesworkflow.save;

import java.util.Map;
import java.util.Scanner;

public class HttpSaver implements Saver{
    IYwSerializer ywSerializer = null;
    IClient client = null;
    String baseURL = "http://localhost:8000/";
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

        RunDto run = new RunDto(username, title, description, model, model_checksum, graph, recon);
        try {
            SaveResponse response = client.SaveRun(run);
            System.out.println(String.format("Status: %d %s ", response.statusCode, response.statusReason));
            System.out.println(String.format("Body:   %s", response.ResponseBody));
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
