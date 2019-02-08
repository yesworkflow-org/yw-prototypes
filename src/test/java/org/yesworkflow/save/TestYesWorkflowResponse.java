package org.yesworkflow.save;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.save.data.DummyResponse;
import org.yesworkflow.save.data.TestData;
import org.yesworkflow.save.data.TestDto;
import org.yesworkflow.save.response.LoginResponse;
import org.yesworkflow.save.response.LogoutResponse;
import org.yesworkflow.save.response.SaveResponse;
import org.yesworkflow.save.response.UpdateResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestYesWorkflowResponse extends YesWorkflowTestCase
{
    @Test
    public void testYwResponse_header() throws IOException
    {
        String headerName = "Content-Type";
        String headerValue = "application/json";

        Header header = mock(Header.class);
        Header[] headers = { header };

        when(header.getName()).thenReturn(headerName);
        when(header.getValue()).thenReturn(headerValue);

        HttpResponse httpResponse = mockResponse(null, null, headers);

        DummyResponse ywResponse = new DummyResponse();
        ywResponse.Build(httpResponse, new JSONSerializer());

        Assert.assertEquals(headerValue, ywResponse.GetHeaderValue(headerName));
    }

    @Test
    public void testYWResponse_BadRequest() throws IOException
    {
        int statusCode = 500;
        String statusReason = "Bad Request";

        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(statusLine.getReasonPhrase()).thenReturn(statusReason);

        HttpResponse httpResponse = mockResponse(null, statusLine, null);

        DummyResponse ywResponse = new DummyResponse();
        ywResponse.Build(httpResponse, new JSONSerializer());

        Assert.assertTrue(ywResponse.BadRequest);
        Assert.assertFalse(ywResponse.OK);
        Assert.assertEquals(statusReason, ywResponse.GetStatusReason());
        Assert.assertEquals(statusCode, ywResponse.GetStatusCode());
    }

    @Test
    public void testYWResponse_OkResponse() throws IOException
    {
        int statusCode = 200;
        String statusReason = "OK";

        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(statusLine.getReasonPhrase()).thenReturn(statusReason);

        HttpResponse httpResponse = mockResponse(null, statusLine, null);

        DummyResponse ywResponse = new DummyResponse();
        ywResponse.Build(httpResponse, new JSONSerializer());

        Assert.assertFalse(ywResponse.BadRequest);
        Assert.assertTrue(ywResponse.OK);
        Assert.assertEquals(statusReason, ywResponse.GetStatusReason());
        Assert.assertEquals(statusCode, ywResponse.GetStatusCode());
    }

    @Test
    public void testYwResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(TestData.testDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        DummyResponse ywResponse = new DummyResponse();
        ywResponse.Build(httpResponse, serializer);

        TestDto expectedObject = serializer.Deserialize(TestData.testDtoJson, TestDto.class);
        TestDto actualObject = ywResponse.ResponseObject;

        Assert.assertEquals(TestData.testDtoJson, ywResponse.ResponseBody);
        Assert.assertEquals(expectedObject.one, actualObject.one);
        Assert.assertEquals(expectedObject.two, actualObject.two);
        Assert.assertEquals(expectedObject.three, actualObject.three);
    }

    @Test
    public void testSaveResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(TestData.runDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        SaveResponse ywResponse = new SaveResponse();
        ywResponse.Build(httpResponse, serializer);

        Assert.assertEquals(TestData.runDtoJson, ywResponse.ResponseBody);
    }

    @Test
    public void testUpdateResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(TestData.runDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        UpdateResponse ywResponse = new UpdateResponse();
        ywResponse.Build(httpResponse, serializer);

        Assert.assertEquals(TestData.runDtoJson, ywResponse.ResponseBody);
    }

    @Test
    public void testLoginResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(TestData.runDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        LoginResponse ywResponse = new LoginResponse();
        ywResponse.Build(httpResponse, serializer);

        Assert.assertEquals(TestData.runDtoJson, ywResponse.ResponseBody);
    }

    @Test
    public void testLogoutResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(TestData.runDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        LogoutResponse ywResponse = new LogoutResponse();
        ywResponse.Build(httpResponse, serializer);

        Assert.assertEquals(TestData.runDtoJson, ywResponse.ResponseBody);
    }

    private HttpResponse mockResponse(InputStream istream, StatusLine status, Header[] headers) throws IOException
    {
        HttpResponse res = mock(HttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);

        if(istream == null)
            istream = IOUtils.toInputStream(TestData.testDtoJson, StandardCharsets.UTF_8);

        if(status == null)
        {
            status = mock(StatusLine.class);

            when(status.getStatusCode()).thenReturn(200);
            when(status.getReasonPhrase()).thenReturn("OK");
        }

        if(headers == null)
            headers = new Header[] {};

        when(entity.getContent()).thenReturn(istream);

        when(res.getEntity()).thenReturn(entity);
        when(res.getAllHeaders()).thenReturn(headers);
        when(res.getStatusLine()).thenReturn(status);

        return res;
    }
}
