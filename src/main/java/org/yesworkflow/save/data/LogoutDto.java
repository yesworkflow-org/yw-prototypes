package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class LogoutDto
{
    @SerializedName("detail")
    public String detail;

    public LogoutDto(String detail)
    {
        this.detail = detail;
    }
}
