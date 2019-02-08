package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class ScriptDto
{
    @SerializedName("name")
    public String name;
    @SerializedName("content")
    public String content;
    @SerializedName("checksum")
    public String checksum;

    public ScriptDto(String name, String content, String checksum)
    {
        this.name = name;
        this.content = content;
        this.checksum = checksum;
    }
}
