package org.yesworkflow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileIO {

    public static final String EOL = System.getProperty("line.separator");
    
    public static String readTextFileOnClasspath(String path) throws IOException {
        InputStream s = FileIO.class.getClassLoader().getResourceAsStream(path);
        InputStreamReader r = new InputStreamReader(s);
        return readTextFromReader(r);
    }

    public static String readTextFromReader(InputStreamReader fileReader) throws IOException {
        BufferedReader br = new BufferedReader(fileReader);
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) stringBuilder.append(line).append(EOL);
        return stringBuilder.toString();
    }
    
    public static String localizeLineEndings(String original) throws IOException {

        String corrected = original.replaceAll("\r", "");
        
        if (!EOL.equals("\n")) {
            corrected = corrected.replaceAll("\n", EOL);
        }
        return corrected;
    }
    
    public static String normalizePath(String path) {
        
        // replace backslashes in path with forward slashes to prevent problems
        // using the directory as a replacement string
        // (backslashes occur in Windows paths but Java can use forward slashes on Windows)
        return path.replace('\\', '/');
    }
}
