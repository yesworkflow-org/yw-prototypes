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
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestHttpSaver extends YesWorkflowTestCase
{
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
    public void testSaver_TagParse() throws Exception
    {
        IYwSerializer serializer = new JSONSerializer();
        HttpSaver saver = new HttpSaver(serializer);
        saver.configure("tags", "a, b, c, d, e");
        ArrayList<String> x = new ArrayList<String>();
        x.add("a");
        x.add("b");
        x.add("c");
        x.add("d");
        x.add("e");
        Assert.assertEquals(x, saver.tags);
    }

    @Test
    public void testSaver_WorkflowParse() throws Exception
    {
        IYwSerializer serializer = new JSONSerializer();
        HttpSaver saver = new HttpSaver(serializer);
        Integer expected = 1;

        saver.configure("workflow", "1");
        Assert.assertEquals(expected, saver.workflowId);
    }

    @Test
    public void testSave() throws Exception
    {
        //TODO:: Integration Tests and Client Tests

    }
}
