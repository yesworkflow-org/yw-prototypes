package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class AuthTokenDto
{
    @SerializedName("key")
    public String key;

    public AuthTokenDto(String key)
    {
        this.key = key;
    }
}
