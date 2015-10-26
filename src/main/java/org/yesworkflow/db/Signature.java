package org.yesworkflow.db;

public class Signature {
    public String inputOrOutput;
    public String variable = null;
    public String alias = null;
    public String uri = null;
    public String inBlock;

    public Signature(String block_name){
        inBlock = block_name;
    }

    public Signature setTag(String tag){
        inputOrOutput = tag;
        return this;
    }

    public Signature setVariable(String variable){
        this.variable = variable;
        return this;
    }

    public Signature setAlias(String alias){
        this.alias = alias;
        return this;
    }

    public Signature setURI(String uri){
        this.uri = uri;
        return this;
    }
}
