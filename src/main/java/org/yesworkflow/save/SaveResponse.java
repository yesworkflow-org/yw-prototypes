package org.yesworkflow.save;

import org.apache.http.HttpResponse;

public class SaveResponse extends YwResponse<RunDto>{

    private RunDto runDto;

    @Override
    public void Build(HttpResponse response, IYwSerializer serializer)
    {
        build(response, serializer);
        this.runDto = DeserializeResponseContent(this.ResponseBody);
    }

    @Override
    protected RunDto DeserializeResponseContent(String json)
    {
        return serializer.Deserialize(json, RunDto.class);
    }

}
