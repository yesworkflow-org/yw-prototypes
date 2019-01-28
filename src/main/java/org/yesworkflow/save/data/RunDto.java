package org.yesworkflow.save.data;

import java.util.ArrayList;
import java.util.List;

public class RunDto {
    public String username;
    public String title;
    public String description;
    public String model;
    public String model_checksum;
    public String graph;
    public String recon;
    public ArrayList<String> tags;
    public List<String> sourceCodeList;
    public List<String> sourceCodeListHash;

    public RunDto(String username, String title, String description, String model, String model_checksum, String graph, String recon, ArrayList<String> tags, List<String> sourceCodeList, List<String> sourceCodeListHash)
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
}
