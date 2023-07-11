// 
// Decompiled by Procyon v0.5.36
// 

package com.eclipsesource.json;

public class ParseException extends RuntimeException
{
    private final int offset;
    private final int line;
    private final int column;
    
    ParseException(final String message, final int offset, final int line, final int column) {
        super(message + " at " + line + ":" + column);
        this.offset = offset;
        this.line = line;
        this.column = column;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public int getColumn() {
        return this.column;
    }
}
