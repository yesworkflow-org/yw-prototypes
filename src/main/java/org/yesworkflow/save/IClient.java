package org.yesworkflow.save;

import org.apache.http.HttpResponse;
import java.io.IOException;

public interface IClient {
    HttpResponse Ping();
    HttpResponse SaveRun(Object runPOJO);
    IClient Close() throws IOException;
}
