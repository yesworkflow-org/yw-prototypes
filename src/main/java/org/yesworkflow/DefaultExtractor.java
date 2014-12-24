package org.yesworkflow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.exceptions.UsageException;

public class DefaultExtractor implements Extractor {

    private char commentCharacter;
    private BufferedReader sourceReader = null;
    private String sourcePath = null;
    private String databasePath = null;
    private List<String> commentLines;
    
    @Override
    public DefaultExtractor commentCharacter(char c) {
        this.commentCharacter = c;
        return this;
    }
    
    public DefaultExtractor sourceReader(BufferedReader reader) {
        this.sourceReader = reader;
        return this;
    }    
    
    @Override
    public DefaultExtractor sourcePath(String path) {
        this.sourcePath = path;
        return this;
    }

    @Override
    public DefaultExtractor databasePath(String path) {
        this.databasePath = path;
        return this;
    }
    
    @Override
    public void extract() throws Exception {
        
        if (sourceReader == null) {
            sourceReader = getFileReaderForPath(sourcePath);
        }

        extractLines();
    }

    @Override    
    public List<String> getLines() {
        return commentLines;
    }
    
    private void extractLines() throws IOException {
        commentLines = new LinkedList<String>();
        String line = null;
        while ((line = sourceReader.readLine()) != null) {
            String ywCommentLine = extractCommentLine(line);
            if (ywCommentLine != null) {
                commentLines.add(ywCommentLine);
            }
        }        
    }

    private String extractCommentLine(String line) {
        
        String trimmedLine = line.trim();
        
        if (trimmedLine.charAt(0) != commentCharacter) return null;
        
        int ywCommentTagBegin = trimmedLine.indexOf('@');
        if (ywCommentTagBegin == -1) return null;
        
        return trimmedLine.substring(ywCommentTagBegin);
    }

    private BufferedReader getFileReaderForPath(String path) throws UsageException {

        if (sourcePath == null) throw new UsageException("No source path provided to extractor");

        BufferedReader reader = null;
        try {        
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new UsageException("Input source file not found: " + path);
        }
        
        return reader;
    }
}
