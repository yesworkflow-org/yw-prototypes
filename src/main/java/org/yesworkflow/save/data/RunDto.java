package org.yesworkflow.save.data;

import java.util.List;

public class RunDto {
    public String username;
    public String title;
    public String description;
    public String model;
    public String model_checksum;
    public String graph;
    public String recon;
    public List<String> tags;
    public List<String> sourceCodeList;
    public List<String> sourceCodeListHash;

    public RunDto(String username,
                  String title,
                  String description,
                  String model,
                  String model_checksum,
                  String graph,
                  String recon,
                  List<String> tags,
                  List<String> sourceCodeList,
                  List<String> sourceCodeListHash)
    {
        this.username=username;
        this.title=title;
        this.description=description;
        this.model=model;
        this.model_checksum=model_checksum;
        this.graph=graph;
        this.recon=recon;
        this.tags=tags;
        this.sourceCodeList=sourceCodeList;
        this.sourceCodeListHash=sourceCodeListHash;
    }

    public static class Builder
    {
        private String username;
        private String model;
        private String model_checksum;
        private String graph;
        private String recon;
        private String title;
        private String description;
        private List<String> tags;
        private List<String> sourceCodeList;
        private List<String> sourceCodeListHash;

        public Builder(String username, String model, String model_checksum, String graph, String recon)
        {
            this.username = username;
            this.model = model;
            this.model_checksum = model_checksum;
            this.graph = graph;
            this.recon = recon;
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

        public Builder setSourceCodeList(List<String> sourceCodeList)
        {
            this.sourceCodeList = sourceCodeList;
            return this;
        }

        public Builder setSourceCodeListHash(List<String> sourceCodeLishHash)
        {
            this.sourceCodeListHash = sourceCodeLishHash;
            return this;
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
        this.model_checksum=builder.model_checksum;
        this.graph=builder.graph;
        this.recon=builder.recon;
        this.tags=builder.tags;
        this.sourceCodeList=builder.sourceCodeList;
        this.sourceCodeListHash=builder.sourceCodeListHash;
    }
}
