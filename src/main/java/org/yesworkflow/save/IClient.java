package org.yesworkflow.save;

import java.io.IOException;

public interface IClient {
    SaveResponse Ping();
    SaveResponse SaveRun(Object runPOJO);
    IClient Close() throws IOException;
}
