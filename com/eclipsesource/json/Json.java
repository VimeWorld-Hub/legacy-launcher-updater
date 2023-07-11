// 
// Decompiled by Procyon v0.5.36
// 

package com.eclipsesource.json;

import java.io.Reader;
import java.io.IOException;

public final class Json
{
    public static final JsonValue NULL;
    public static final JsonValue TRUE;
    public static final JsonValue FALSE;
    
    private Json() {
    }
    
    public static JsonValue value(final int value) {
        return new JsonNumber(Integer.toString(value, 10));
    }
    
    public static JsonValue value(final long value) {
        return new JsonNumber(Long.toString(value, 10));
    }
    
    public static JsonValue value(final float value) {
        if (Float.isInfinite(value) || Float.isNaN(value)) {
            throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
        }
        return new JsonNumber(cutOffPointZero(Float.toString(value)));
    }
    
    public static JsonValue value(final double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
        }
        return new JsonNumber(cutOffPointZero(Double.toString(value)));
    }
    
    public static JsonValue value(final String string) {
        return (string == null) ? Json.NULL : new JsonString(string);
    }
    
    public static JsonValue value(final boolean value) {
        return value ? Json.TRUE : Json.FALSE;
    }
    
    public static JsonValue array() {
        return new JsonArray();
    }
    
    public static JsonArray array(final int... values) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }
        final JsonArray array = new JsonArray();
        for (final int value : values) {
            array.add(value);
        }
        return array;
    }
    
    public static JsonArray array(final long... values) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }
        final JsonArray array = new JsonArray();
        for (final long value : values) {
            array.add(value);
        }
        return array;
    }
    
    public static JsonArray array(final float... values) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }
        final JsonArray array = new JsonArray();
        for (final float value : values) {
            array.add(value);
        }
        return array;
    }
    
    public static JsonArray array(final double... values) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }
        final JsonArray array = new JsonArray();
        for (final double value : values) {
            array.add(value);
        }
        return array;
    }
    
    public static JsonArray array(final boolean... values) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }
        final JsonArray array = new JsonArray();
        for (final boolean value : values) {
            array.add(value);
        }
        return array;
    }
    
    public static JsonArray array(final String... strings) {
        if (strings == null) {
            throw new NullPointerException("values is null");
        }
        final JsonArray array = new JsonArray();
        for (final String value : strings) {
            array.add(value);
        }
        return array;
    }
    
    public static JsonObject object() {
        return new JsonObject();
    }
    
    public static JsonValue parse(final String string) {
        if (string == null) {
            throw new NullPointerException("string is null");
        }
        try {
            return new JsonParser(string).parse();
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public static JsonValue parse(final Reader reader) throws IOException {
        if (reader == null) {
            throw new NullPointerException("reader is null");
        }
        return new JsonParser(reader).parse();
    }
    
    private static String cutOffPointZero(final String string) {
        if (string.endsWith(".0")) {
            return string.substring(0, string.length() - 2);
        }
        return string;
    }
    
    static {
        NULL = new JsonLiteral("null");
        TRUE = new JsonLiteral("true");
        FALSE = new JsonLiteral("false");
    }
}
