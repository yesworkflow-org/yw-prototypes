package org.yesworkflow.save;

import org.junit.*;
import org.yesworkflow.save.data.*;
import org.yesworkflow.save.response.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

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
        String username = UUID.randomUUID().toString();
        String password = "Passoword!@#";

        RegisterDto registerDto = new RegisterDto.Builder(username, password).build();
        LoginDto loginDto = new LoginDto.Builder(username, password).build();

        client.CreateUser(registerDto);
        client.Login(loginDto);

        ScriptDto scriptDto = new ScriptDto("name", "content", "checksum");
        ArrayList<ScriptDto> scripts = new ArrayList<>();
        scripts.add(scriptDto);
        RunDto run = new RunDto.Builder(username, "model", "check", "graph", "recon", scripts)
                                .build();
        SaveResponse response = client.SaveRun(run);
        assertTrue(response.ResponseBody, response.OK);
        // TODO:: verify response body once that stops changing
        // assertEquals();
    }

    @Test
    public void testYwClient_Register()
    {
        RegisterDto registerDto = new RegisterDto.Builder(UUID.randomUUID().toString(), "Password!@#")
                                                    .build();
        RegisterResponse response = client.CreateUser(registerDto);
        assertTrue(response.ResponseBody, response.OK);
        // TODO:: verify response body once that stops changing
        // assertEquals();
    }

    @Test
    public void testYwClient_Login()
    {
        RegisterDto registerDto = new RegisterDto.Builder(UUID.randomUUID().toString(), "Password!@#")
                .build();
        client.CreateUser(registerDto);

        LoginDto loginDto = new LoginDto.Builder(registerDto.username, registerDto.password1)
                                            .build();
        LoginResponse response = client.Login(loginDto);
        assertTrue(response.ResponseBody, response.OK);
        // TODO:: verify response body once that stops changing
        // assertEquals();

    }

    @Test
    public void testYwClient_Logout()
    {
        RegisterDto registerDto = new RegisterDto.Builder(UUID.randomUUID().toString(), "Password!@#")
                .build();
        client.CreateUser(registerDto);

        LoginDto loginDto = new LoginDto.Builder(registerDto.username, registerDto.password1)
                .build();
        client.Login(loginDto);

        LogoutResponse response = client.Logout();

        assertTrue(response.ResponseBody, response.OK);
        // TODO:: verify response body once that stops changing
        // assertEquals();
    }

    @Test
    public void testYwClient_UpdateWorkflow()
    {
        String username = UUID.randomUUID().toString();
        String password = "Passoword!@#";

        RegisterDto registerDto = new RegisterDto.Builder(username, password).build();
        LoginDto loginDto = new LoginDto.Builder(username, password).build();

        client.CreateUser(registerDto);
        client.Login(loginDto);

        ScriptDto scriptDto = new ScriptDto("name", "content", "checksum");
        ArrayList<ScriptDto> scripts = new ArrayList<>();
        scripts.add(scriptDto);
        RunDto run = new RunDto.Builder(username, "model", "check", "graph", "recon", scripts)
                .build();

        SaveResponse saveResponse = client.SaveRun(run);
        UpdateResponse updateResponse = client.UpdateWorkflow(saveResponse.ResponseObject.workflowId, run);
        assertTrue(updateResponse.ResponseBody, updateResponse.OK);
        assertEquals(updateResponse.ResponseObject.workflowId, saveResponse.ResponseObject.workflowId);
        assertEquals(updateResponse.ResponseObject.versionNumber, saveResponse.ResponseObject.versionNumber);
        assertEquals(updateResponse.ResponseObject.runNumber, saveResponse.ResponseObject.runNumber + 1);
        assertFalse(updateResponse.ResponseObject.newVersion);
    }
}
