package org.yesworkflow.util;

/* This file is an adaptation of KuratorAkkaTestCase.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

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
}
