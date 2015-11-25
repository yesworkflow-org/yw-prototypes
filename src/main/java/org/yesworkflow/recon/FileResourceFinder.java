package org.yesworkflow.recon;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.data.UriTemplate;

public class FileResourceFinder extends SimpleFileVisitor<Path> implements ResourceFinder {
    
    private FileSystem fileSystem = FileSystems.getDefault();
    private PathMatcher matcher;
    private Path basePath;
    private List<String> patternMatches;
    
    @Override
    public FileResourceFinder configure(Map<String, Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public FileResourceFinder configure(String key, Object value) throws Exception {
        return this;
    }

    @Override
    public Collection<String> findMatchingResources(String baseUri, UriTemplate uriTemplate, ResourceRole role) {
        this.basePath = Paths.get(baseUri);
        patternMatches = new LinkedList<String>();
        Path resourceSearchBase = this.basePath.resolve(uriTemplate.leadingPath);
        if (Files.isRegularFile(resourceSearchBase)) {
            patternMatches.add(this.basePath.relativize(resourceSearchBase).toString());
        } else {
            this.matcher = fileSystem.getPathMatcher(uriTemplate.getGlobPattern());
            try {
                Files.walkFileTree(resourceSearchBase, this);
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }              
        }
        return patternMatches;
    }
    
    @Override public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttributes) throws IOException {
      Path runRelativePath = basePath.relativize(filePath);
      if (matcher.matches(runRelativePath)) {
          patternMatches.add(runRelativePath.toString());
      }
      return FileVisitResult.CONTINUE;
    }
    
    @Override  public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes fileAttributes) throws IOException {
      // TODO: Improve performance by detecting when directory cannot lead to a match for the template
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public Collection<String> findUnmatchedResources(String baseUri, ResourceRole role) {
        return new LinkedList<String>();
    }
}