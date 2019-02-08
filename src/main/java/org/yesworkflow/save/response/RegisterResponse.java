package org.yesworkflow.save.response;

import org.apache.http.HttpResponse;
import org.yesworkflow.save.IYwSerializer;
import org.yesworkflow.save.data.AuthTokenDto;

public class RegisterResponse extends YwResponse<AuthTokenDto>
{
    @Override
    public YwResponse<AuthTokenDto> Build(HttpResponse response, IYwSerializer serializer)
    {
        super.build(response, serializer);
        this.ResponseObject = DeserializeResponseContent();
        return this;
    }

    @Override
    protected AuthTokenDto DeserializeResponseContent() {
        return this.serializer.Deserialize(this.ResponseBody, AuthTokenDto.class);
    }
}
