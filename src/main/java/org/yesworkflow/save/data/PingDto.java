package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class PingDto
{
    @SerializedName("data")
    public String data;

    public PingDto(String data)
    {
        this.data = data;
    }
}
