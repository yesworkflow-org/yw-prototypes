package org.yesworkflow.cli;

public enum YWExitCode {
    
    SUCCESS          (0),
    UNCAUGHT_ERROR  (-1),
    CLI_USAGE_ERROR (-2),
    MARKUP_ERROR    (-3);
    
    private int value;
    YWExitCode(int value) { this.value = value; }
    public int value() { return value;}
}