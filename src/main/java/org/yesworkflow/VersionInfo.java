package org.yesworkflow;

import java.io.IOException;
import java.util.Properties;

public class VersionInfo {

    public final String softwareName;
    public final String version;
    public final String qualifiedVersion;
    
    public final String gitRemoteOrigin;
    public final String gitBranch;
    public final String gitCommitId;
    public final String gitCommitAbbrev;
    public final String gitClosestTag;
    public final String gitCommitsSinceTag;

    public final String mavenBuildVersion;
    public final String mavenBuildTime;

    public static final String EOL = System.getProperty("line.separator");
    public static final String bannerDelimiter = 
            "-----------------------------------------------------------------------------";
    
    public static VersionInfo loadVersionInfoFromResource(String softwareName, String classPathResource)  {
        Properties gitProperties = new Properties();
        try {
            gitProperties.load(VersionInfo.class.getClassLoader().getResourceAsStream(classPathResource));
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return new VersionInfo(softwareName, gitProperties);
    }
    
    public VersionInfo(String softwareName, Properties gitProperties) {
        
        this.softwareName = softwareName;
        
        gitRemoteOrigin = gitProperties.getProperty("git.remote.origin.url");
        gitBranch = gitProperties.getProperty("git.branch");
        gitCommitId = gitProperties.getProperty("git.commit.id");
        gitCommitAbbrev = gitProperties.getProperty("git.commit.id.abbrev");
//      gitClosestTag = gitProperties.getProperty("git.closest.tag.name");
//      gitCommitsSinceTag = gitProperties.getProperty("git.closest.tag.commit.count");
//      mavenBuildVersion = gitProperties.getProperty("git.build.version");
        mavenBuildVersion = gitProperties.getProperty("git.commit.id.describe-short");
        mavenBuildTime = gitProperties.getProperty("git.build.time");
        
        version = softwareName + " " + mavenBuildVersion; // + "-" + gitCommitsSinceTag;
        qualifiedVersion = version + " (branch " + gitBranch + ", commit " + gitCommitAbbrev + ")";
        
        String[] versionParts = mavenBuildVersion.split("-");
        gitClosestTag = versionParts[0];
        gitCommitsSinceTag = versionParts[1];
    }
    
    public String versionBanner() {
        return new StringBuilder()
                   .append(bannerDelimiter).append(EOL)
                   .append(qualifiedVersion).append(EOL)
                   .append(bannerDelimiter).append(EOL)
                   .toString();
    }
    
    public String versionDetails() {
        return new StringBuilder()
                   .append("Remote repo: ").append(gitRemoteOrigin).append(EOL)
                   .append("Git branch: ").append(gitBranch).append(EOL)
                   .append("Last commit: ").append(gitCommitId).append(EOL)
                   .append("Most recent tag: ").append(gitClosestTag).append(EOL)
                   .append("Commits since tag: ").append(gitCommitsSinceTag).append(EOL)
                   .append("Build time: ").append(mavenBuildTime).append(EOL)
                   .toString();
    }
}