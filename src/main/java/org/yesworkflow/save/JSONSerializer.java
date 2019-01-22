package org.yesworkflow.save;

import com.google.gson.Gson;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JSONSerializer implements IYwSerializer
{

    private Gson gson;
    public JSONSerializer(){
        gson = new Gson();
    }

    public InputStream SerializeToInputStream(Object object)
    {
        return org.apache.commons.io.IOUtils.toInputStream(Serialize(object), StandardCharsets.UTF_8);
    }

    public String Serialize(Object object)
    {
        return gson.toJson(object);
    }

    public <T> T Deserialize(String json, Class<T> Dto)
    {
        return gson.fromJson(json, Dto);
    }
}
