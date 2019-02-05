package org.yesworkflow.save;

import org.junit.*;
import org.junit.Assert.*;
import org.yesworkflow.save.data.RunDto;
import org.yesworkflow.save.data.TestData;
import org.yesworkflow.save.response.PingResponse;
import org.yesworkflow.save.response.SaveResponse;
import org.yesworkflow.save.response.UpdateResponse;

import java.io.IOException;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestYwClient
{
    private String connection;
    private IClient client;

    @BeforeClass
    public static void checkServer()
    {
        boolean hostAvailable;
        try(Socket s = new Socket(TestData.testingurl, TestData.testingport))
        {
            hostAvailable = s.isConnected();
        }
        catch(IOException e)
        {
            hostAvailable = false;
        }
        Assume.assumeTrue(String.format("Ignoring TestYwClient test(s). Host '%s' at port '%d' not available",
                                        TestData.testingurl,
                                        TestData.testingport),
                          hostAvailable);
    }

    @Before
    public void setUp() throws Exception
    {
        connection = String.format("http://%s:%d/",
                                   TestData.testingurl,
                                   TestData.testingport);
        IYwSerializer serializer = new JSONSerializer();
        client = new YwClient(connection, serializer);
    }

    @After
    public void tearDown() throws Exception
    {
        client.Close();
    }

    @Test
    public void testYwClient_Ping()
    {
        PingResponse response = client.Ping();
        assertTrue(response.OK);
        // TODO:: verify response body once that stops changing
        // assertEquals(TestData.pingResponseBody, response.ResponseBody);
    }

    @Test
    public void testYwClient_Save()
    {
        RunDto run = new RunDto.Builder("niehuser", "model", "check", "graph", "recon")
                                .build();
        SaveResponse response = client.SaveRun(run);
        assertTrue(response.OK);
        // TODO:: verify response body once that stops changing
        // assertEquals();
    }

    @Test
    public void testYwClient_UpdateWorkflow()
    {
        RunDto run = new RunDto.Builder("niehuser", "model", "check", "graph", "recon")
                                .build();

        client.SaveRun(run);
        UpdateResponse response = client.UpdateWorkflow(1, run);
        // TODO:: verify response body and ensure workflow is the users once those details are figured out
        assertTrue(response.OK);
        // assertEquals()
    }
}
