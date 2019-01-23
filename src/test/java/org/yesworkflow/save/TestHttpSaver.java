package org.yesworkflow.save;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.recon.DefaultReconstructor;
import org.yesworkflow.recon.Reconstructor;
import org.yesworkflow.save.response.SaveResponse;
import org.yesworkflow.save.response.UpdateResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestHttpSaver extends YesWorkflowTestCase
{
    private static final String testDtoJson = "{\"one\":\"first\",\"two\":\"second\",\"three\":\"third\"}";
    private static final String runDtoJson = "{\"username\":\"crandoms\",\"title\":\"workflow\",\"description\":\"desc\",\"model\":\"mod\",\"model_checksum\":\"mod_check\",\"graph\":\"graph\",\"recon\":\"recon\"}";

    private YesWorkflowDB ywdb = null;
    private Extractor extractor = null;
    private Modeler modeler = null;
    private Reconstructor reconstructor = null;
    private CloseableHttpClient httpClient = null;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        this.extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        this.modeler = new DefaultModeler(this.ywdb, super.stdoutStream, super.stderrStream);
        this.reconstructor = new DefaultReconstructor(super.stdoutStream, super.stderrStream);
        this.httpClient = mock(CloseableHttpClient.class);
    }

    @Test
    public void testJSONSerializer_serialize()
    {
        IYwSerializer serializer = new JSONSerializer();

        TestDto testDto = new TestDto("first", "second", "third");
        String expectedOutput = testDtoJson;
        String actualOutput = serializer.Serialize(testDto);

        Assert.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testJSONSerializer_deserialize()
    {
        IYwSerializer serializer = new JSONSerializer();

        TestDto actual = serializer.Deserialize(testDtoJson, TestDto.class);
        TestDto expected = new TestDto("first", "second", "third");

        Assert.assertEquals(expected.one, actual.one);
        Assert.assertEquals(expected.two, actual.two);
        Assert.assertEquals(expected.three, actual.three);
    }

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

        ResponseTest ywResponse = new ResponseTest();
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

        ResponseTest ywResponse = new ResponseTest();
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

        ResponseTest ywResponse = new ResponseTest();
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
        InputStream inputStream = IOUtils.toInputStream(testDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        ResponseTest ywResponse = new ResponseTest();
        ywResponse.Build(httpResponse, serializer);

        TestDto expectedObject = serializer.Deserialize(testDtoJson, TestDto.class);
        TestDto actualObject = ywResponse.ResponseObject;

        Assert.assertEquals(testDtoJson, ywResponse.ResponseBody);
        Assert.assertEquals(expectedObject.one, actualObject.one);
        Assert.assertEquals(expectedObject.two, actualObject.two);
        Assert.assertEquals(expectedObject.three, actualObject.three);
    }

    @Test
    public void testSaveResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(runDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        SaveResponse ywResponse = new SaveResponse();
        ywResponse.Build(httpResponse, serializer);

        Assert.assertEquals(runDtoJson, ywResponse.ResponseBody);
    }

    @Test
    public void testUpdateResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();
        InputStream inputStream = IOUtils.toInputStream(runDtoJson, StandardCharsets.UTF_8);

        HttpResponse httpResponse = mockResponse(inputStream, null, null);

        UpdateResponse ywResponse = new UpdateResponse();
        ywResponse.Build(httpResponse, serializer);

        Assert.assertEquals(runDtoJson, ywResponse.ResponseBody);
    }
    
    private HttpResponse mockResponse(InputStream istream, StatusLine status, Header[] headers) throws IOException
    {
        HttpResponse res = mock(HttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);

        if(istream == null)
            istream = IOUtils.toInputStream(testDtoJson, StandardCharsets.UTF_8);

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

    @Test
    public void testSave()
    {
        //TODO:: Integration Tests and Client Tests
    }
}
