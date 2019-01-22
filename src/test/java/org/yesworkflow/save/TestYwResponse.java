package org.yesworkflow.save;

import org.apache.http.HttpResponse;

public class TestYwResponse extends YwResponse<TestDto> {
    public void Build(HttpResponse response, IYwSerializer serializer)
    {
        this.build(response, serializer);
        this.ResponseObject = DeserializeResponseContent(this.ResponseBody);
    }

    protected TestDto DeserializeResponseContent(String json)
    {
        return serializer.Deserialize(json, TestDto.class);
    }
}
