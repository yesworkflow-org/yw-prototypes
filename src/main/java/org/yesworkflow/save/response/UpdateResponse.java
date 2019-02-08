package org.yesworkflow.save.response;

import org.apache.http.HttpResponse;
import org.yesworkflow.save.IYwSerializer;
import org.yesworkflow.save.data.UpdatedDto;

public class UpdateResponse extends YwResponse<UpdatedDto>
{
    @Override
    public YwResponse<UpdatedDto> Build(HttpResponse response, IYwSerializer serializer)
    {
        this.build(response, serializer);
        this.ResponseObject = DeserializeResponseContent();
        return this;
    }

    @Override
    protected UpdatedDto DeserializeResponseContent()
    {
        return serializer.Deserialize(this.ResponseBody, UpdatedDto.class);
    }
}
