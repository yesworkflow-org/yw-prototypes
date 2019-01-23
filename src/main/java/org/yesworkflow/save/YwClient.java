package org.yesworkflow.save;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.yesworkflow.save.data.RunDto;
import org.yesworkflow.save.response.SaveResponse;
import org.yesworkflow.save.response.UpdateResponse;
import org.yesworkflow.save.response.YwResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class YwClient implements IClient {
    private CloseableHttpClient client;
    private String baseUrl;
    private IYwSerializer serializer;

    public YwClient(String connection, IYwSerializer serializer)
    {
        client = HttpClients.custom()
                            .build();
        baseUrl = connection;
        this.serializer = serializer;
    }

    // TODO::create ping response
    public SaveResponse Ping()
    {
        return executeGetRequest("save/ping/", SaveResponse.class);
    }

    public SaveResponse SaveRun(RunDto runDto)
    {
        return executePostRequest("save/", runDto, SaveResponse.class);
    }

    public UpdateResponse UpdateWorkflow(Integer workflowId, RunDto runDto)
    {
        return executePostRequest(String.format("save/%d/", workflowId), runDto, UpdateResponse.class);
    }

    private <Response extends YwResponse<?>> Response executeGetRequest(String route,
                                                                        Class<Response> rClass)
    {
        HttpGet getRequest = new HttpGet(String.join("", baseUrl, route));
        getRequest.addHeader("accept", "application/json");
        return executeRequest(getRequest, rClass);
    }

    private <Response extends YwResponse<?>> Response executePostRequest(String route,
                                                                         Object RequestPOJO,
                                                                         Class<Response> rClass)
    {
        HttpPost postRequest = new HttpPost(String.join("", baseUrl, route));
        Response response = null;
        try
        {
            StringEntity json = new StringEntity(serializer.Serialize(RequestPOJO));
            json.setContentType("application/json");
            postRequest.setEntity(json);
            response = executeRequest(postRequest, rClass);
        } catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        return response;
    }

    private <Response extends YwResponse<?>> Response executeRequest(HttpRequestBase request,
                                                                     Class<Response> rClass)
    {
        HttpResponse httpResponse = null;
        Exception exception = null;
        try {
            httpResponse = client.execute(request);
        } catch (IOException e) {
            //TODO:: think about handling
            System.out.println(exception.getMessage());
        }

        Response ywResponse = null;
        try
        {
            ywResponse = rClass.getConstructor().newInstance();
            ywResponse.Build(httpResponse, serializer);
        }
        catch (ReflectiveOperationException e)
        {
            //TODO:: handle better
        }

        return ywResponse;
    }

    public IClient Close()
    {
        try{
            client.close();
        } catch (IOException e)
        {
            // TODO:: handle better
            System.out.println(e.getMessage());
        }

        return this;
    }
}
