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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestHttpSaver extends YesWorkflowTestCase
{
    private static final String testDtoString = "{\"one\":\"first\",\"two\":\"second\",\"three\":\"third\"}";
    private static final String testSaveDtoString = "{\"username\":\"crandoms\",\"title\":\"workflow\",\"description\":\"desc\",\"model\":\"mod\",\"model_checksum\":\"mod_check\",\"graph\":\"graph\",\"recon\":\"recon\"}";

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
        String expectedOutput = testDtoString;
        String actualOutput = serializer.Serialize(testDto);

        Assert.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testJSONSerializer_deserialize()
    {
        IYwSerializer serializer = new JSONSerializer();

        TestDto actual = serializer.Deserialize(testDtoString, TestDto.class);
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

        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header header = mock(Header.class);
        Header[] headers = { header };
        HttpEntity httpEntity = mock(HttpEntity.class);
        InputStream inputStream = IOUtils.toInputStream(testDtoString, StandardCharsets.UTF_8);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(statusLine.getReasonPhrase()).thenReturn("Ok");

        when(header.getName()).thenReturn(headerName);
        when(header.getValue()).thenReturn(headerValue);

        when(httpEntity.getContent()).thenReturn(inputStream);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getAllHeaders()).thenReturn(headers);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        TestYwResponse ywResponse = new TestYwResponse();
        ywResponse.build(httpResponse, new JSONSerializer());

        Assert.assertEquals(headerValue, ywResponse.GetHeaderValue(headerName));
    }

    @Test
    public void testYWResponse_BadRequest() throws IOException
    {
        int statusCode = 500;
        String statusReason = "Bad Request";

        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header[] headers = {};
        HttpEntity httpEntity = mock(HttpEntity.class);
        InputStream inputStream = IOUtils.toInputStream(testDtoString, StandardCharsets.UTF_8);

        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(statusLine.getReasonPhrase()).thenReturn(statusReason);

        when(httpEntity.getContent()).thenReturn(inputStream);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getAllHeaders()).thenReturn(headers);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        TestYwResponse ywResponse = new TestYwResponse();
        ywResponse.build(httpResponse, new JSONSerializer());

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

        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header[] headers = {};
        HttpEntity httpEntity = mock(HttpEntity.class);
        InputStream inputStream = IOUtils.toInputStream(testDtoString, StandardCharsets.UTF_8);

        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(statusLine.getReasonPhrase()).thenReturn(statusReason);

        when(httpEntity.getContent()).thenReturn(inputStream);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getAllHeaders()).thenReturn(headers);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        TestYwResponse ywResponse = new TestYwResponse();
        ywResponse.build(httpResponse, new JSONSerializer());

        Assert.assertFalse(ywResponse.BadRequest);
        Assert.assertTrue(ywResponse.OK);
        Assert.assertEquals(statusReason, ywResponse.GetStatusReason());
        Assert.assertEquals(statusCode, ywResponse.GetStatusCode());
    }

    @Test
    public void testYwResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();

        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header[] headers = {};
        HttpEntity httpEntity = mock(HttpEntity.class);
        InputStream inputStream = IOUtils.toInputStream(testDtoString, StandardCharsets.UTF_8);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(statusLine.getReasonPhrase()).thenReturn("Ok");

        when(httpEntity.getContent()).thenReturn(inputStream);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getAllHeaders()).thenReturn(headers);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        TestYwResponse ywResponse = new TestYwResponse();
        ywResponse.build(httpResponse, serializer);

        TestDto expectedObject = serializer.Deserialize(testDtoString, TestDto.class);
        TestDto actualObject = ywResponse.ResponseObject;

        Assert.assertEquals(testDtoString, ywResponse.ResponseBody);
        Assert.assertEquals(expectedObject.one, actualObject.one);
        Assert.assertEquals(expectedObject.two, actualObject.two);
        Assert.assertEquals(expectedObject.three, actualObject.three);
    }

    @Test
    public void testSaveResponse_Content() throws IOException
    {
        IYwSerializer serializer = new JSONSerializer();

        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header[] headers = {};
        HttpEntity httpEntity = mock(HttpEntity.class);
        InputStream inputStream = IOUtils.toInputStream(testSaveDtoString, StandardCharsets.UTF_8);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(statusLine.getReasonPhrase()).thenReturn("Ok");

        when(httpEntity.getContent()).thenReturn(inputStream);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getAllHeaders()).thenReturn(headers);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        SaveResponse ywResponse = new SaveResponse();
        ywResponse.build(httpResponse, serializer);

        Assert.assertEquals(testSaveDtoString, ywResponse.ResponseBody);
    }

    @Test
    public void testSave()
    {
        //TODO:: Integration Tests and Client Tests
    }
}
