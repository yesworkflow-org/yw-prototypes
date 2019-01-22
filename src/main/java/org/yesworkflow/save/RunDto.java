package org.yesworkflow.save;

public class RunDto {
    public String username;
    public String title;
    public String description;
    public String model;
    public String model_checksum;
    public String graph;
    public String recon;

    public RunDto(String username, String title, String description, String model, String model_checksum, String graph, String recon)
    {
        this.username=username;
        this.title=title;
        this.description=description;
        this.model=model;
        this.model_checksum=model_checksum;
        this.graph=graph;
        this.recon=recon;
    }
}
