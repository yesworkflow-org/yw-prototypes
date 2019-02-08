package org.yesworkflow.save.response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.yesworkflow.save.IYwSerializer;
import org.yesworkflow.save.JSONSerializer;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

public abstract class YwResponse<Dto>
{
    private String defaultEncoding = "UTF-8";

    public boolean OK;
    public boolean BadRequest;
    public String ResponseBody;
    public Dto ResponseObject;
    protected Hashtable<String, String> headers;
    protected int statusCode;
    protected String statusReason;
    protected IYwSerializer serializer;

    public abstract YwResponse<Dto> Build(HttpResponse response, IYwSerializer serializer);

    protected abstract Dto DeserializeResponseContent();

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
        this.OK = this.statusCode >= 200 && this.statusCode < 300;
        this.BadRequest = this.statusCode >= 500;
        this.headers = AllocateHeaders(response);
        this.ResponseBody = ScanResponse(response);
        this.ResponseObject = DeserializeResponseContent();
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
