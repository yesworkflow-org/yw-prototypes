package org.yesworkflow;

import java.util.Properties;

public class VersionInfo {

    public final String softwareName;
    public final String productVersion;
    public final String qualifiedVersion;    
    public final String officialRepoUrl;
    
    public final String gitBranch;
    public final String gitCommitId;
    public final String gitCommitAbbrev;
    public final String gitCommitTime;
    public final String gitClosestTag;
    public final String gitCommitsSinceTag;

    public final String buildVersion;
    public final String buildTime;
    public final String buildHost;
    public final String buildUserName;
    public final String buildUserEmail;
    public final String buildPlatform;
    
    public final String osArch;
    public final String osName;
    public final String osVersion;
    
    public final String buildJavaVersion;
    public final String javaVmName;
    public final String javaVmVendor;
    public final String buildJavaVM;

    public static final String EOL = System.getProperty("line.separator");
    public static final String bannerDelimiter = 
            "-----------------------------------------------------------------------------";
    
    public static VersionInfo loadVersionInfoFromResource(String softwareName, String officialRepoUrl, 
                                                          String gitPropertyResource, String mavenPropertyResource)  {
        Properties gitProperties = new Properties();
        Properties mavenProperties = new Properties();
        
        try {
            gitProperties.load(VersionInfo.class.getClassLoader().getResourceAsStream(gitPropertyResource));
            mavenProperties.load(VersionInfo.class.getClassLoader().getResourceAsStream(mavenPropertyResource));
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        return new VersionInfo(softwareName, officialRepoUrl, gitProperties, mavenProperties);
    }

    public VersionInfo(String productName, String officialRepoUrl, Properties gitProperties, Properties mavenProperties) {
        
        this.softwareName = productName;
        this.officialRepoUrl = officialRepoUrl;
        
        gitBranch = gitProperties.getProperty("git.branch");
        gitCommitId = gitProperties.getProperty("git.commit.id");
        gitCommitAbbrev = gitProperties.getProperty("git.commit.id.abbrev");
        gitCommitTime = gitProperties.getProperty("git.commit.time");
        gitClosestTag = gitProperties.getProperty("git.closest.tag.name");
        gitCommitsSinceTag = gitProperties.getProperty("git.closest.tag.commit.count");
        
        buildVersion = gitProperties.getProperty("git.build.version");

        productVersion = productName + " " + buildVersion + "-" + gitCommitsSinceTag;
        qualifiedVersion = productVersion + " (" 
                            + (gitBranch.equals("master") ? "branch" : "BRANCH ") + gitBranch + 
                            ", commit " + gitCommitAbbrev + 
                            ")";
        
        buildTime = gitProperties.getProperty("git.build.time");
        buildHost = gitProperties.getProperty("git.build.host");
        buildUserName = gitProperties.getProperty("git.build.user.name");
        buildUserEmail = gitProperties.getProperty("git.commit.user.email");

        osArch = mavenProperties.getProperty("os.arch");
        osName = mavenProperties.getProperty("os.name");
        osVersion = mavenProperties.getProperty("os.version");
        
        buildJavaVersion = mavenProperties.getProperty("java.version");
        javaVmName = mavenProperties.getProperty("java.vm.name");
        javaVmVendor = mavenProperties.getProperty("java.vm.vendor");        
        buildJavaVM = javaVmName +" (" + javaVmVendor +  ")";
        
        buildPlatform = osName + " " + osVersion + " (" + osArch + ")";
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
                   .append("Remote repo: ").append(officialRepoUrl).append(EOL)
                   .append("Git branch: ").append(gitBranch).append(EOL)
                   .append("Last commit: ").append(gitCommitId).append(EOL)
                   .append("Commit time: ").append(gitCommitTime).append(EOL)
                   .append("Most recent tag: ").append(gitClosestTag).append(EOL)
                   .append("Commits since tag: ").append(gitCommitsSinceTag).append(EOL)
                   .append("Builder name: ").append(buildUserName).append(EOL)
                   .append("Builder email: ").append(buildUserEmail).append(EOL)
                   .append("Build host: ").append(buildHost).append(EOL)
                   .append("Build platform: ").append(buildPlatform).append(EOL)
                   .append("Build Java VM: ").append(buildJavaVM).append(EOL)
                   .append("Build Java version: ").append("JDK ").append(buildJavaVersion).append(EOL)
                   .append("Build time: ").append(buildTime).append(EOL)
                   .toString();
    }
}