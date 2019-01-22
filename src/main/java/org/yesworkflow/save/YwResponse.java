package org.yesworkflow.save;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;


public abstract class YwResponse<Dto> {
    private String defaultEncoding = "UTF-8";

    public boolean OK;
    public boolean BadRequest;
    public String ResponseBody;
    public Dto ResponseObject;
    protected Hashtable<String, String> headers;
    protected int statusCode;
    protected String statusReason;
    protected IYwSerializer serializer;

    public abstract void Build(HttpResponse response, IYwSerializer serializer);

    protected abstract Dto DeserializeResponseContent(String json);

    public String GetHeaderValue(String headerName)
    {
        return headers.get(headerName);
    }

    public int GetStatusCode()
    {
        return this.statusCode;
    }

    public String GetStatusReason()
    {
        return this.statusReason;
    }

    protected void build(HttpResponse response, IYwSerializer serializer)
    {
        this.serializer = serializer;
        if(this.serializer == null)
            this.serializer = new JSONSerializer();

        this.statusCode = response.getStatusLine().getStatusCode();
        this.statusReason = response.getStatusLine().getReasonPhrase();
        this.OK = this.statusCode == 200;
        this.BadRequest = this.statusCode >= 500;
        this.headers = AllocateHeaders(response);
        this.ResponseBody = ScanResponse(response);
        this.ResponseObject = DeserializeResponseContent(this.ResponseBody);
    }

    private String ScanResponse(HttpResponse response)
    {
        String responseBody = "";
        try{
            Scanner scanner = new Scanner(response.getEntity().getContent(), defaultEncoding).useDelimiter("\\A");
            responseBody = scanner.next();
        } catch(IOException ioe)
        {
            // TODO:: Error handling
        }
        return responseBody;
    }

    private Hashtable<String, String> AllocateHeaders(HttpResponse response)
    {
        Hashtable<String, String> hashtable = new Hashtable<>();
        for(Header header : response.getAllHeaders())
        {
            hashtable.put(header.getName(), header.getValue());
        }
        return hashtable;
    }
}
