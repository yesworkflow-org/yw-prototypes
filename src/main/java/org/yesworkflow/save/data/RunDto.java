package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RunDto {

    @SerializedName("username")
    public String username;
    @SerializedName("title")
    public String title;
    @SerializedName("description")
    public String description;
    @SerializedName("model")
    public String model;
    @SerializedName("modelChecksum")
    public String modelChecksum;
    @SerializedName("graph")
    public String graph;
    @SerializedName("recon")
    public String recon;
    @SerializedName("tags")
    public List<String> tags;
    @SerializedName("scripts")
    public List<ScriptDto> scripts;
    @SerializedName("files")
    public List<FileDto> files;

    public RunDto(String username,
                  String title,
                  String description,
                  String model,
                  String modelChecksum,
                  String graph,
                  String recon,
                  List<String> tags,
                  List<ScriptDto> scripts,
                  List<FileDto> files) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.model = model;
        this.modelChecksum = modelChecksum;
        this.graph = graph;
        this.recon = recon;
        this.tags = tags;
        this.scripts = scripts;
        this.files = files;
    }


    public static class Builder
    {
        public String username;
        public String title;
        public String description;
        public String model;
        public String modelChecksum;
        public String graph;
        public String recon;
        public List<String> tags;
        public List<ScriptDto> scripts;
        public List<FileDto> files;

        public Builder(String username, String model, String modelChecksum, String graph, String recon, List<ScriptDto> scripts)
        {
            this.username = username;
            this.model = model;
            this.modelChecksum = modelChecksum;
            this.graph = graph;
            this.recon = recon;
            this.scripts = scripts;
        }


        public Builder setTitle(String title)
        {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description)
        {
            this.description = description;
            return this;
        }

        public Builder setTags(List<String> tags)
        {
            this.tags = tags;
            return this;
        }

        public void setFiles(List<FileDto> files)
        {
            this.files = files;
        }


        public RunDto build()
        {
            return new RunDto(this);
        }
    }

    public RunDto(Builder builder)
    {
        this.username=builder.username;
        this.title=builder.title;
        this.description=builder.description;
        this.model=builder.model;
        this.modelChecksum =builder.modelChecksum;
        this.graph=builder.graph;
        this.recon=builder.recon;
        this.tags=builder.tags;
        this.scripts=builder.scripts;
        this.files=builder.files;
    }
}
