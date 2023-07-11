// 
// Decompiled by Procyon v0.5.36
// 

package com.twmacinta.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class MD5InputStream extends FilterInputStream
{
    private MD5 md5;
    
    public MD5InputStream(final InputStream in) {
        super(in);
        this.md5 = new MD5();
    }
    
    @Override
    public int read() throws IOException {
        final int c = this.in.read();
        if (c == -1) {
            return -1;
        }
        if ((c & 0xFFFFFF00) != 0x0) {
            System.out.println("MD5InputStream.read() got character with (c & ~0xff) != 0)!");
        }
        else {
            this.md5.Update(c);
        }
        return c;
    }
    
    @Override
    public int read(final byte[] bytes, final int offset, final int length) throws IOException {
        final int r;
        if ((r = this.in.read(bytes, offset, length)) == -1) {
            return r;
        }
        this.md5.Update(bytes, offset, r);
        return r;
    }
    
    public byte[] hash() {
        return this.md5.Final();
    }
    
    public MD5 getMD5() {
        return this.md5;
    }
}
