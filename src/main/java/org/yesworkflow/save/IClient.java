package org.yesworkflow.save;

import org.yesworkflow.save.data.RunDto;
import org.yesworkflow.save.response.PingResponse;
import org.yesworkflow.save.response.SaveResponse;
import org.yesworkflow.save.response.UpdateResponse;

import java.io.IOException;

public interface IClient {
    PingResponse Ping();
    SaveResponse SaveRun(RunDto runDto);
    UpdateResponse UpdateWorkflow(Integer workflowId, RunDto runDto);
    IClient Close() throws IOException;
}
