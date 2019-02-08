package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class CreatedDto
{
    @SerializedName("workflowId")
    public int workflowId;
    @SerializedName("versionNumber")
    public int versionNumber;
    @SerializedName("runNumber")
    public int runNumber;

    public CreatedDto(int workflowId, int versionNumber, int runNumber)
    {
        this.workflowId = workflowId;
        this.versionNumber = versionNumber;
        this.runNumber = runNumber;
    }
}
