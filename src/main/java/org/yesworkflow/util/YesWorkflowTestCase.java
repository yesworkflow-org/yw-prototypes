package org.yesworkflow.util;

/* This file is an adaptation of KuratorAkkaTestCase.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class YesWorkflowTestCase extends TestCase {
    
    public static final String EOL = System.getProperty("line.separator");

    protected OutputStream stdoutBuffer;
    protected OutputStream stderrBuffer;
    
    protected PrintStream stdoutStream;
    protected PrintStream stderrStream;

    @Override
    public void setUp() throws Exception {
        
        super.setUp();

        stdoutBuffer = new ByteArrayOutputStream();
        stdoutStream = new PrintStream(stdoutBuffer);
    
        stderrBuffer = new ByteArrayOutputStream();
        stderrStream = new PrintStream(stderrBuffer);
    }
    
    // reads a file from the classpath, replacing stored EOL with local EOL sequence
    public static String readTextFileOnClasspath(String path) throws IOException {
        InputStream stream = YesWorkflowTestCase.class.getClassLoader().getResourceAsStream(path);
        InputStreamReader reader = new InputStreamReader(stream);
        String contents = readTextFromReader(reader);
        return contents;
    } 
    
    // reads a file from input stream line by line, replacing stored EOL with local EOL sequence
    public static String readTextFromReader(InputStreamReader fileReader) throws IOException {
        BufferedReader reader = new BufferedReader(fileReader);
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(EOL);
        }
        return stringBuilder.toString();
    }
}
