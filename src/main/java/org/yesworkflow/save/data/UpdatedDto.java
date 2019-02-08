package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class UpdatedDto
{
    @SerializedName("workflowId")
    public int workflowId;
    @SerializedName("versionNumber")
    public int versionNumber;
    @SerializedName("runNumber")
    public int runNumber;
    @SerializedName("newVersion")
    public boolean newVersion;


    public UpdatedDto(int workflowId, int versionNumber, int runNumber, boolean newVersion)
    {
        this.workflowId = workflowId;
        this.versionNumber = versionNumber;
        this.runNumber = runNumber;
        this.newVersion = newVersion;
    }
}
