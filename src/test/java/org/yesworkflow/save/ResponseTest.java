package org.yesworkflow.save;

import org.apache.http.HttpResponse;
import org.yesworkflow.save.response.YwResponse;

public class ResponseTest extends YwResponse<TestDto> {
    public YwResponse<TestDto> Build(HttpResponse response, IYwSerializer serializer)
    {
        this.build(response, serializer);
        this.ResponseObject = DeserializeResponseContent();
        return this;
    }

    protected TestDto DeserializeResponseContent()
    {
        return serializer.Deserialize(this.ResponseBody, TestDto.class);
    }
}
