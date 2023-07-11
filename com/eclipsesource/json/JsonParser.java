// 
// Decompiled by Procyon v0.5.36
// 

package com.eclipsesource.json;

import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;

class JsonParser
{
    private static final int MIN_BUFFER_SIZE = 10;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final Reader reader;
    private final char[] buffer;
    private int bufferOffset;
    private int index;
    private int fill;
    private int line;
    private int lineOffset;
    private int current;
    private StringBuilder captureBuffer;
    private int captureStart;
    
    JsonParser(final String string) {
        this(new StringReader(string), Math.max(10, Math.min(1024, string.length())));
    }
    
    JsonParser(final Reader reader) {
        this(reader, 1024);
    }
    
    JsonParser(final Reader reader, final int buffersize) {
        this.reader = reader;
        this.buffer = new char[buffersize];
        this.line = 1;
        this.captureStart = -1;
    }
    
    JsonValue parse() throws IOException {
        this.read();
        this.skipWhiteSpace();
        final JsonValue result = this.readValue();
        this.skipWhiteSpace();
        if (!this.isEndOfText()) {
            throw this.error("Unexpected character");
        }
        return result;
    }
    
    private JsonValue readValue() throws IOException {
        switch (this.current) {
            case 110: {
                return this.readNull();
            }
            case 116: {
                return this.readTrue();
            }
            case 102: {
                return this.readFalse();
            }
            case 34: {
                return this.readString();
            }
            case 91: {
                return this.readArray();
            }
            case 123: {
                return this.readObject();
            }
            case 45:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57: {
                return this.readNumber();
            }
            default: {
                throw this.expected("value");
            }
        }
    }
    
    private JsonArray readArray() throws IOException {
        this.read();
        final JsonArray array = new JsonArray();
        this.skipWhiteSpace();
        if (this.readChar(']')) {
            return array;
        }
        do {
            this.skipWhiteSpace();
            array.add(this.readValue());
            this.skipWhiteSpace();
        } while (this.readChar(','));
        if (!this.readChar(']')) {
            throw this.expected("',' or ']'");
        }
        return array;
    }
    
    private JsonObject readObject() throws IOException {
        this.read();
        final JsonObject object = new JsonObject();
        this.skipWhiteSpace();
        if (this.readChar('}')) {
            return object;
        }
        do {
            this.skipWhiteSpace();
            final String name = this.readName();
            this.skipWhiteSpace();
            if (!this.readChar(':')) {
                throw this.expected("':'");
            }
            this.skipWhiteSpace();
            object.add(name, this.readValue());
            this.skipWhiteSpace();
        } while (this.readChar(','));
        if (!this.readChar('}')) {
            throw this.expected("',' or '}'");
        }
        return object;
    }
    
    private String readName() throws IOException {
        if (this.current != 34) {
            throw this.expected("name");
        }
        return this.readStringInternal();
    }
    
    private JsonValue readNull() throws IOException {
        this.read();
        this.readRequiredChar('u');
        this.readRequiredChar('l');
        this.readRequiredChar('l');
        return Json.NULL;
    }
    
    private JsonValue readTrue() throws IOException {
        this.read();
        this.readRequiredChar('r');
        this.readRequiredChar('u');
        this.readRequiredChar('e');
        return Json.TRUE;
    }
    
    private JsonValue readFalse() throws IOException {
        this.read();
        this.readRequiredChar('a');
        this.readRequiredChar('l');
        this.readRequiredChar('s');
        this.readRequiredChar('e');
        return Json.FALSE;
    }
    
    private void readRequiredChar(final char ch) throws IOException {
        if (!this.readChar(ch)) {
            throw this.expected("'" + ch + "'");
        }
    }
    
    private JsonValue readString() throws IOException {
        return new JsonString(this.readStringInternal());
    }
    
    private String readStringInternal() throws IOException {
        this.read();
        this.startCapture();
        while (this.current != 34) {
            if (this.current == 92) {
                this.pauseCapture();
                this.readEscape();
                this.startCapture();
            }
            else {
                if (this.current < 32) {
                    throw this.expected("valid string character");
                }
                this.read();
            }
        }
        final String string = this.endCapture();
        this.read();
        return string;
    }
    
    private void readEscape() throws IOException {
        this.read();
        switch (this.current) {
            case 34:
            case 47:
            case 92: {
                this.captureBuffer.append((char)this.current);
                break;
            }
            case 98: {
                this.captureBuffer.append('\b');
                break;
            }
            case 102: {
                this.captureBuffer.append('\f');
                break;
            }
            case 110: {
                this.captureBuffer.append('\n');
                break;
            }
            case 114: {
                this.captureBuffer.append('\r');
                break;
            }
            case 116: {
                this.captureBuffer.append('\t');
                break;
            }
            case 117: {
                final char[] hexChars = new char[4];
                for (int i = 0; i < 4; ++i) {
                    this.read();
                    if (!this.isHexDigit()) {
                        throw this.expected("hexadecimal digit");
                    }
                    hexChars[i] = (char)this.current;
                }
                this.captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                break;
            }
            default: {
                throw this.expected("valid escape sequence");
            }
        }
        this.read();
    }
    
    private JsonValue readNumber() throws IOException {
        this.startCapture();
        this.readChar('-');
        final int firstDigit = this.current;
        if (!this.readDigit()) {
            throw this.expected("digit");
        }
        if (firstDigit != 48) {
            while (this.readDigit()) {}
        }
        this.readFraction();
        this.readExponent();
        return new JsonNumber(this.endCapture());
    }
    
    private boolean readFraction() throws IOException {
        if (!this.readChar('.')) {
            return false;
        }
        if (!this.readDigit()) {
            throw this.expected("digit");
        }
        while (this.readDigit()) {}
        return true;
    }
    
    private boolean readExponent() throws IOException {
        if (!this.readChar('e') && !this.readChar('E')) {
            return false;
        }
        if (!this.readChar('+')) {
            this.readChar('-');
        }
        if (!this.readDigit()) {
            throw this.expected("digit");
        }
        while (this.readDigit()) {}
        return true;
    }
    
    private boolean readChar(final char ch) throws IOException {
        if (this.current != ch) {
            return false;
        }
        this.read();
        return true;
    }
    
    private boolean readDigit() throws IOException {
        if (!this.isDigit()) {
            return false;
        }
        this.read();
        return true;
    }
    
    private void skipWhiteSpace() throws IOException {
        while (this.isWhiteSpace()) {
            this.read();
        }
    }
    
    private void read() throws IOException {
        if (this.index == this.fill) {
            if (this.captureStart != -1) {
                this.captureBuffer.append(this.buffer, this.captureStart, this.fill - this.captureStart);
                this.captureStart = 0;
            }
            this.bufferOffset += this.fill;
            this.fill = this.reader.read(this.buffer, 0, this.buffer.length);
            this.index = 0;
            if (this.fill == -1) {
                this.current = -1;
                return;
            }
        }
        if (this.current == 10) {
            ++this.line;
            this.lineOffset = this.bufferOffset + this.index;
        }
        this.current = this.buffer[this.index++];
    }
    
    private void startCapture() {
        if (this.captureBuffer == null) {
            this.captureBuffer = new StringBuilder();
        }
        this.captureStart = this.index - 1;
    }
    
    private void pauseCapture() {
        final int end = (this.current == -1) ? this.index : (this.index - 1);
        this.captureBuffer.append(this.buffer, this.captureStart, end - this.captureStart);
        this.captureStart = -1;
    }
    
    private String endCapture() {
        final int end = (this.current == -1) ? this.index : (this.index - 1);
        String captured;
        if (this.captureBuffer.length() > 0) {
            this.captureBuffer.append(this.buffer, this.captureStart, end - this.captureStart);
            captured = this.captureBuffer.toString();
            this.captureBuffer.setLength(0);
        }
        else {
            captured = new String(this.buffer, this.captureStart, end - this.captureStart);
        }
        this.captureStart = -1;
        return captured;
    }
    
    private ParseException expected(final String expected) {
        if (this.isEndOfText()) {
            return this.error("Unexpected end of input");
        }
        return this.error("Expected " + expected);
    }
    
    private ParseException error(final String message) {
        final int absIndex = this.bufferOffset + this.index;
        final int column = absIndex - this.lineOffset;
        final int offset = this.isEndOfText() ? absIndex : (absIndex - 1);
        return new ParseException(message, offset, this.line, column - 1);
    }
    
    private boolean isWhiteSpace() {
        return this.current == 32 || this.current == 9 || this.current == 10 || this.current == 13;
    }
    
    private boolean isDigit() {
        return this.current >= 48 && this.current <= 57;
    }
    
    private boolean isHexDigit() {
        return (this.current >= 48 && this.current <= 57) || (this.current >= 97 && this.current <= 102) || (this.current >= 65 && this.current <= 70);
    }
    
    private boolean isEndOfText() {
        return this.current == -1;
    }
}
