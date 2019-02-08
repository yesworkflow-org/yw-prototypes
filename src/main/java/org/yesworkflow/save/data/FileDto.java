package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class FileDto
{
    @SerializedName("name")
    public String name;
    @SerializedName("uri")
    public String uri;
    @SerializedName("size")
    public int size;
    @SerializedName("checksum")
    public String checksum;
    @SerializedName("lastModified")
    public LocalDateTime lastModified;

    public FileDto(String name, String uri, int size, String checksum, LocalDateTime lastModified) {
        this.name = name;
        this.uri = uri;
        this.size = size;
        this.checksum = checksum;
        this.lastModified = lastModified;
    }
}
