package org.yesworkflow.save;

import org.yesworkflow.save.data.RunDto;
import org.yesworkflow.save.response.YwResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class HttpSaver implements Saver
{
    IYwSerializer ywSerializer = null;
    IClient client = null;
    Integer workflowId = null;
    String baseURL = "http://localhost:8000/";
    String username = null;
    String title = "Title";
    String description = "Description";
    String graph = "";
    String model = "";
    String model_checksum = "";
    String recon = "";
    ArrayList<String> tags = new ArrayList<String>();

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

        RunDto run = new RunDto(username, title, description, model, model_checksum, graph, recon, tags);
        try {
            YwResponse<RunDto> response;
            if(workflowId == null)
                response = client.SaveRun(run);
            else
                response = client.UpdateWorkflow(workflowId, run);

            System.out.println(String.format("Status: %d %s ", response.GetStatusCode(), response.GetStatusReason()));
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
                break;
            case "workflow":
                workflowId = (Integer) value;
                break;
            case "title":
                title = (String) value;
                break;
            case "description":
                description = (String) value;
                break;
            case "tags" :
                String valTags = (String) value;
                tags = new ArrayList<String>(Arrays.asList(valTags.split("\\s*,\\s*")));
                break;
            default:
                break;
        }

        return this;
    }
}
