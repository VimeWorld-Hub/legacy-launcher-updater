// 
// Decompiled by Procyon v0.5.36
// 

package com.twmacinta.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class MD5
{
    MD5State state;
    MD5State finals;
    static byte[] padding;
    private static final char[] HEX_CHARS;
    
    public synchronized void Init() {
        this.state = new MD5State();
        this.finals = null;
    }
    
    public MD5() {
        this.Init();
    }
    
    public MD5(final Object ob) {
        this();
        this.Update(ob.toString());
    }
    
    private void Decode(final byte[] buffer, final int shift, final int[] out) {
        out[0] = ((buffer[shift] & 0xFF) | (buffer[shift + 1] & 0xFF) << 8 | (buffer[shift + 2] & 0xFF) << 16 | buffer[shift + 3] << 24);
        out[1] = ((buffer[shift + 4] & 0xFF) | (buffer[shift + 5] & 0xFF) << 8 | (buffer[shift + 6] & 0xFF) << 16 | buffer[shift + 7] << 24);
        out[2] = ((buffer[shift + 8] & 0xFF) | (buffer[shift + 9] & 0xFF) << 8 | (buffer[shift + 10] & 0xFF) << 16 | buffer[shift + 11] << 24);
        out[3] = ((buffer[shift + 12] & 0xFF) | (buffer[shift + 13] & 0xFF) << 8 | (buffer[shift + 14] & 0xFF) << 16 | buffer[shift + 15] << 24);
        out[4] = ((buffer[shift + 16] & 0xFF) | (buffer[shift + 17] & 0xFF) << 8 | (buffer[shift + 18] & 0xFF) << 16 | buffer[shift + 19] << 24);
        out[5] = ((buffer[shift + 20] & 0xFF) | (buffer[shift + 21] & 0xFF) << 8 | (buffer[shift + 22] & 0xFF) << 16 | buffer[shift + 23] << 24);
        out[6] = ((buffer[shift + 24] & 0xFF) | (buffer[shift + 25] & 0xFF) << 8 | (buffer[shift + 26] & 0xFF) << 16 | buffer[shift + 27] << 24);
        out[7] = ((buffer[shift + 28] & 0xFF) | (buffer[shift + 29] & 0xFF) << 8 | (buffer[shift + 30] & 0xFF) << 16 | buffer[shift + 31] << 24);
        out[8] = ((buffer[shift + 32] & 0xFF) | (buffer[shift + 33] & 0xFF) << 8 | (buffer[shift + 34] & 0xFF) << 16 | buffer[shift + 35] << 24);
        out[9] = ((buffer[shift + 36] & 0xFF) | (buffer[shift + 37] & 0xFF) << 8 | (buffer[shift + 38] & 0xFF) << 16 | buffer[shift + 39] << 24);
        out[10] = ((buffer[shift + 40] & 0xFF) | (buffer[shift + 41] & 0xFF) << 8 | (buffer[shift + 42] & 0xFF) << 16 | buffer[shift + 43] << 24);
        out[11] = ((buffer[shift + 44] & 0xFF) | (buffer[shift + 45] & 0xFF) << 8 | (buffer[shift + 46] & 0xFF) << 16 | buffer[shift + 47] << 24);
        out[12] = ((buffer[shift + 48] & 0xFF) | (buffer[shift + 49] & 0xFF) << 8 | (buffer[shift + 50] & 0xFF) << 16 | buffer[shift + 51] << 24);
        out[13] = ((buffer[shift + 52] & 0xFF) | (buffer[shift + 53] & 0xFF) << 8 | (buffer[shift + 54] & 0xFF) << 16 | buffer[shift + 55] << 24);
        out[14] = ((buffer[shift + 56] & 0xFF) | (buffer[shift + 57] & 0xFF) << 8 | (buffer[shift + 58] & 0xFF) << 16 | buffer[shift + 59] << 24);
        out[15] = ((buffer[shift + 60] & 0xFF) | (buffer[shift + 61] & 0xFF) << 8 | (buffer[shift + 62] & 0xFF) << 16 | buffer[shift + 63] << 24);
    }
    
    private void Transform(final MD5State state, final byte[] buffer, final int shift, final int[] decode_buf) {
        int a = state.state[0];
        int b = state.state[1];
        int c = state.state[2];
        int d = state.state[3];
        final int[] x = decode_buf;
        this.Decode(buffer, shift, decode_buf);
        a += ((b & c) | (~b & d)) + x[0] - 680876936;
        a = (a << 7 | a >>> 25) + b;
        d += ((a & b) | (~a & c)) + x[1] - 389564586;
        d = (d << 12 | d >>> 20) + a;
        c += ((d & a) | (~d & b)) + x[2] + 606105819;
        c = (c << 17 | c >>> 15) + d;
        b += ((c & d) | (~c & a)) + x[3] - 1044525330;
        b = (b << 22 | b >>> 10) + c;
        a += ((b & c) | (~b & d)) + x[4] - 176418897;
        a = (a << 7 | a >>> 25) + b;
        d += ((a & b) | (~a & c)) + x[5] + 1200080426;
        d = (d << 12 | d >>> 20) + a;
        c += ((d & a) | (~d & b)) + x[6] - 1473231341;
        c = (c << 17 | c >>> 15) + d;
        b += ((c & d) | (~c & a)) + x[7] - 45705983;
        b = (b << 22 | b >>> 10) + c;
        a += ((b & c) | (~b & d)) + x[8] + 1770035416;
        a = (a << 7 | a >>> 25) + b;
        d += ((a & b) | (~a & c)) + x[9] - 1958414417;
        d = (d << 12 | d >>> 20) + a;
        c += ((d & a) | (~d & b)) + x[10] - 42063;
        c = (c << 17 | c >>> 15) + d;
        b += ((c & d) | (~c & a)) + x[11] - 1990404162;
        b = (b << 22 | b >>> 10) + c;
        a += ((b & c) | (~b & d)) + x[12] + 1804603682;
        a = (a << 7 | a >>> 25) + b;
        d += ((a & b) | (~a & c)) + x[13] - 40341101;
        d = (d << 12 | d >>> 20) + a;
        c += ((d & a) | (~d & b)) + x[14] - 1502002290;
        c = (c << 17 | c >>> 15) + d;
        b += ((c & d) | (~c & a)) + x[15] + 1236535329;
        b = (b << 22 | b >>> 10) + c;
        a += ((b & d) | (c & ~d)) + x[1] - 165796510;
        a = (a << 5 | a >>> 27) + b;
        d += ((a & c) | (b & ~c)) + x[6] - 1069501632;
        d = (d << 9 | d >>> 23) + a;
        c += ((d & b) | (a & ~b)) + x[11] + 643717713;
        c = (c << 14 | c >>> 18) + d;
        b += ((c & a) | (d & ~a)) + x[0] - 373897302;
        b = (b << 20 | b >>> 12) + c;
        a += ((b & d) | (c & ~d)) + x[5] - 701558691;
        a = (a << 5 | a >>> 27) + b;
        d += ((a & c) | (b & ~c)) + x[10] + 38016083;
        d = (d << 9 | d >>> 23) + a;
        c += ((d & b) | (a & ~b)) + x[15] - 660478335;
        c = (c << 14 | c >>> 18) + d;
        b += ((c & a) | (d & ~a)) + x[4] - 405537848;
        b = (b << 20 | b >>> 12) + c;
        a += ((b & d) | (c & ~d)) + x[9] + 568446438;
        a = (a << 5 | a >>> 27) + b;
        d += ((a & c) | (b & ~c)) + x[14] - 1019803690;
        d = (d << 9 | d >>> 23) + a;
        c += ((d & b) | (a & ~b)) + x[3] - 187363961;
        c = (c << 14 | c >>> 18) + d;
        b += ((c & a) | (d & ~a)) + x[8] + 1163531501;
        b = (b << 20 | b >>> 12) + c;
        a += ((b & d) | (c & ~d)) + x[13] - 1444681467;
        a = (a << 5 | a >>> 27) + b;
        d += ((a & c) | (b & ~c)) + x[2] - 51403784;
        d = (d << 9 | d >>> 23) + a;
        c += ((d & b) | (a & ~b)) + x[7] + 1735328473;
        c = (c << 14 | c >>> 18) + d;
        b += ((c & a) | (d & ~a)) + x[12] - 1926607734;
        b = (b << 20 | b >>> 12) + c;
        a += (b ^ c ^ d) + x[5] - 378558;
        a = (a << 4 | a >>> 28) + b;
        d += (a ^ b ^ c) + x[8] - 2022574463;
        d = (d << 11 | d >>> 21) + a;
        c += (d ^ a ^ b) + x[11] + 1839030562;
        c = (c << 16 | c >>> 16) + d;
        b += (c ^ d ^ a) + x[14] - 35309556;
        b = (b << 23 | b >>> 9) + c;
        a += (b ^ c ^ d) + x[1] - 1530992060;
        a = (a << 4 | a >>> 28) + b;
        d += (a ^ b ^ c) + x[4] + 1272893353;
        d = (d << 11 | d >>> 21) + a;
        c += (d ^ a ^ b) + x[7] - 155497632;
        c = (c << 16 | c >>> 16) + d;
        b += (c ^ d ^ a) + x[10] - 1094730640;
        b = (b << 23 | b >>> 9) + c;
        a += (b ^ c ^ d) + x[13] + 681279174;
        a = (a << 4 | a >>> 28) + b;
        d += (a ^ b ^ c) + x[0] - 358537222;
        d = (d << 11 | d >>> 21) + a;
        c += (d ^ a ^ b) + x[3] - 722521979;
        c = (c << 16 | c >>> 16) + d;
        b += (c ^ d ^ a) + x[6] + 76029189;
        b = (b << 23 | b >>> 9) + c;
        a += (b ^ c ^ d) + x[9] - 640364487;
        a = (a << 4 | a >>> 28) + b;
        d += (a ^ b ^ c) + x[12] - 421815835;
        d = (d << 11 | d >>> 21) + a;
        c += (d ^ a ^ b) + x[15] + 530742520;
        c = (c << 16 | c >>> 16) + d;
        b += (c ^ d ^ a) + x[2] - 995338651;
        b = (b << 23 | b >>> 9) + c;
        a += (c ^ (b | ~d)) + x[0] - 198630844;
        a = (a << 6 | a >>> 26) + b;
        d += (b ^ (a | ~c)) + x[7] + 1126891415;
        d = (d << 10 | d >>> 22) + a;
        c += (a ^ (d | ~b)) + x[14] - 1416354905;
        c = (c << 15 | c >>> 17) + d;
        b += (d ^ (c | ~a)) + x[5] - 57434055;
        b = (b << 21 | b >>> 11) + c;
        a += (c ^ (b | ~d)) + x[12] + 1700485571;
        a = (a << 6 | a >>> 26) + b;
        d += (b ^ (a | ~c)) + x[3] - 1894986606;
        d = (d << 10 | d >>> 22) + a;
        c += (a ^ (d | ~b)) + x[10] - 1051523;
        c = (c << 15 | c >>> 17) + d;
        b += (d ^ (c | ~a)) + x[1] - 2054922799;
        b = (b << 21 | b >>> 11) + c;
        a += (c ^ (b | ~d)) + x[8] + 1873313359;
        a = (a << 6 | a >>> 26) + b;
        d += (b ^ (a | ~c)) + x[15] - 30611744;
        d = (d << 10 | d >>> 22) + a;
        c += (a ^ (d | ~b)) + x[6] - 1560198380;
        c = (c << 15 | c >>> 17) + d;
        b += (d ^ (c | ~a)) + x[13] + 1309151649;
        b = (b << 21 | b >>> 11) + c;
        a += (c ^ (b | ~d)) + x[4] - 145523070;
        a = (a << 6 | a >>> 26) + b;
        d += (b ^ (a | ~c)) + x[11] - 1120210379;
        d = (d << 10 | d >>> 22) + a;
        c += (a ^ (d | ~b)) + x[2] + 718787259;
        c = (c << 15 | c >>> 17) + d;
        b += (d ^ (c | ~a)) + x[9] - 343485551;
        b = (b << 21 | b >>> 11) + c;
        final int[] state2 = state.state;
        final int n = 0;
        state2[n] += a;
        final int[] state3 = state.state;
        final int n2 = 1;
        state3[n2] += b;
        final int[] state4 = state.state;
        final int n3 = 2;
        state4[n3] += c;
        final int[] state5 = state.state;
        final int n4 = 3;
        state5[n4] += d;
    }
    
    public void Update(final MD5State stat, final byte[] buffer, final int offset, int length) {
        this.finals = null;
        if (length - offset > buffer.length) {
            length = buffer.length - offset;
        }
        int index = (int)(stat.count & 0x3FL);
        stat.count += length;
        int partlen = 64 - index;
        int i;
        if (length >= partlen) {
            final int[] decode_buf = new int[16];
            if (partlen == 64) {
                partlen = 0;
            }
            else {
                for (i = 0; i < partlen; ++i) {
                    stat.buffer[i + index] = buffer[i + offset];
                }
                this.Transform(stat, stat.buffer, 0, decode_buf);
            }
            for (i = partlen; i + 63 < length; i += 64) {
                this.Transform(stat, buffer, i + offset, decode_buf);
            }
            index = 0;
        }
        else {
            i = 0;
        }
        if (i < length) {
            final int start = i;
            while (i < length) {
                stat.buffer[index + i - start] = buffer[i + offset];
                ++i;
            }
        }
    }
    
    public void Update(final byte[] buffer, final int offset, final int length) {
        this.Update(this.state, buffer, offset, length);
    }
    
    public void Update(final byte[] buffer, final int length) {
        this.Update(this.state, buffer, 0, length);
    }
    
    public void Update(final byte[] buffer) {
        this.Update(buffer, 0, buffer.length);
    }
    
    public void Update(final byte b) {
        final byte[] buffer = { b };
        this.Update(buffer, 1);
    }
    
    public void Update(final String s) {
        final byte[] chars = s.getBytes();
        this.Update(chars, chars.length);
    }
    
    public void Update(final String s, String charset_name) throws UnsupportedEncodingException {
        if (charset_name == null) {
            charset_name = "ISO8859_1";
        }
        final byte[] chars = s.getBytes(charset_name);
        this.Update(chars, chars.length);
    }
    
    public void Update(final int i) {
        this.Update((byte)(i & 0xFF));
    }
    
    private byte[] Encode(final int[] input, final int len) {
        final byte[] out = new byte[len];
        int i;
        for (int j = i = 0; j < len; j += 4) {
            out[j] = (byte)(input[i] & 0xFF);
            out[j + 1] = (byte)(input[i] >>> 8 & 0xFF);
            out[j + 2] = (byte)(input[i] >>> 16 & 0xFF);
            out[j + 3] = (byte)(input[i] >>> 24 & 0xFF);
            ++i;
        }
        return out;
    }
    
    public synchronized byte[] Final() {
        if (this.finals == null) {
            final MD5State fin = new MD5State(this.state);
            final int[] count_ints = { (int)(fin.count << 3), (int)(fin.count >> 29) };
            final byte[] bits = this.Encode(count_ints, 8);
            final int index = (int)(fin.count & 0x3FL);
            final int padlen = (index < 56) ? (56 - index) : (120 - index);
            this.Update(fin, MD5.padding, 0, padlen);
            this.Update(fin, bits, 0, 8);
            this.finals = fin;
        }
        return this.Encode(this.finals.state, 16);
    }
    
    public static String asHex(final byte[] hash) {
        final char[] buf = new char[hash.length * 2];
        int i = 0;
        int x = 0;
        while (i < hash.length) {
            buf[x++] = MD5.HEX_CHARS[hash[i] >>> 4 & 0xF];
            buf[x++] = MD5.HEX_CHARS[hash[i] & 0xF];
            ++i;
        }
        return new String(buf);
    }
    
    public String asHex() {
        return asHex(this.Final());
    }
    
    public static byte[] getHash(final File f) throws IOException {
        if (!f.exists()) {
            throw new FileNotFoundException(f.toString());
        }
        InputStream close_me = null;
        try {
            long buf_size = f.length();
            if (buf_size < 512L) {
                buf_size = 512L;
            }
            if (buf_size > 65536L) {
                buf_size = 65536L;
            }
            final byte[] buf = new byte[(int)buf_size];
            final MD5InputStream in = (MD5InputStream)(close_me = new MD5InputStream(new FileInputStream(f)));
            while (in.read(buf) != -1) {}
            in.close();
            return in.hash();
        }
        catch (IOException e) {
            if (close_me != null) {
                try {
                    close_me.close();
                }
                catch (Exception ex) {}
            }
            throw e;
        }
    }
    
    public static boolean hashesEqual(final byte[] hash1, final byte[] hash2) {
        if (hash1 == null) {
            return hash2 == null;
        }
        if (hash2 == null) {
            return false;
        }
        int targ = 16;
        if (hash1.length < 16) {
            if (hash2.length != hash1.length) {
                return false;
            }
            targ = hash1.length;
        }
        else if (hash2.length < 16) {
            return false;
        }
        for (int i = 0; i < targ; ++i) {
            if (hash1[i] != hash2[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        MD5.padding = new byte[] { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
