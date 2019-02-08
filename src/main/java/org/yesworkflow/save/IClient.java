package org.yesworkflow.save;

import org.yesworkflow.save.data.LoginDto;
import org.yesworkflow.save.data.RegisterDto;
import org.yesworkflow.save.data.RunDto;
import org.yesworkflow.save.response.*;

import java.io.IOException;

public interface IClient {
    PingResponse Ping();
    RegisterResponse CreateUser(RegisterDto registerDto);
    LoginResponse Login(LoginDto loginDto);
    LogoutResponse Logout();
    SaveResponse SaveRun(RunDto runDto);
    UpdateResponse UpdateWorkflow(Integer workflowId, RunDto runDto);
    IClient Close() throws IOException;
}
