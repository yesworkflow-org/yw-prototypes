package org.yesworkflow.recon;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.data.UriTemplate;

public class FileResourceFinder extends SimpleFileVisitor<Path> {
    
    private String pattern;
    private final PathMatcher matcher;
    private final Path runBase;
    
    private final List<Path> resourcePaths = new LinkedList<Path>();
    
    public FileResourceFinder(UriTemplate template, Path runDirectoryBase) {
        super();
        this.runBase = runDirectoryBase;
        this.pattern = template.getGlobPattern();
        this.matcher = FileSystems.getDefault().getPathMatcher(pattern);
    }
    
    @Override public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttributes) throws IOException {
      Path runRelativePath = runBase.relativize(filePath);
      if (matcher.matches(runRelativePath)) {
          resourcePaths.add(runRelativePath);
      }
      return FileVisitResult.CONTINUE;
    }
    
    @Override  public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes fileAttributes) throws IOException {
      // TODO: Improve performance by detecting when directory cannot lead to a match for the template
        return FileVisitResult.CONTINUE;
    }
    
    public List<Path> getResourcePaths() {
        return resourcePaths;
    }
}