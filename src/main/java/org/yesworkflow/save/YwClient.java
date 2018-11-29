package org.yesworkflow.save;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class YwClient implements IClient {
    CloseableHttpClient client;
    String baseUrl;
    IYwSerializer serializer;

    public YwClient(String connection, IYwSerializer serializer)
    {
        client = HttpClients.custom()
                .build();
        baseUrl = connection;
        this.serializer = serializer;
    }

    public HttpResponse Get(String route) {
        HttpGet httpGet = new HttpGet(String.join(baseUrl, route));
        return executeGetRequest(httpGet);
    }

    public HttpResponse Ping()
    {
        return executeGetRequest(new HttpGet(String.join("",baseUrl, "save/ping/")));
    }

    public HttpResponse SaveRun(Object runPOJO)
    {
        return executePostRequest(new HttpPost(String.join("", baseUrl, "save/")), runPOJO);
    }

    private HttpResponse executeGetRequest(HttpGet getRequest)
    {
        getRequest.addHeader("accept", "application/json");
        return executeRequest(getRequest);
    }

    private HttpResponse executePostRequest(HttpPost postRequest, Object RequestPOJO)
    {
        HttpResponse response = null;
        try
        {
            StringEntity json = new StringEntity(serializer.Serialize(RequestPOJO));
            json.setContentType("application/json");
            postRequest.setEntity(json);
            response = executeRequest(postRequest);
        } catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        return response;
    }

    private HttpResponse executeRequest(HttpRequestBase request)
    {
        HttpResponse httpResponse = null;
        Exception exception = null;
        try {
            httpResponse = client.execute(request);
        } catch (IOException e) {
            exception = e;
        } catch (Exception e){
            exception = e;
        } finally {
            if(exception != null) {
                System.out.println(exception.getMessage());
            }
        }
        return httpResponse;
    }

    public IClient Close()
    {
        try{
            client.close();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return this;
    }
}
