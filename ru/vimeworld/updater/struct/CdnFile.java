// 
// Decompiled by Procyon v0.5.36
// 

package ru.vimeworld.updater.struct;

import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.JsonObject;

public class CdnFile
{
    public final String path;
    public final int size;
    public final String md5;
    public final String url;
    public final Versions versions;
    
    public CdnFile(final JsonObject json) {
        this.path = json.getString("path", null);
        this.md5 = json.getString("md5", null);
        this.url = json.getString("url", null);
        this.size = json.getInt("size", 0);
        final JsonValue value = json.get("versions");
        if (value != null && value.isObject()) {
            this.versions = new Versions(value.asObject());
        }
        else {
            this.versions = null;
        }
    }
    
    public static class Versions
    {
        public final CdnFile deflate;
        
        public Versions(final JsonObject json) {
            this.deflate = tryReadFile(json, "deflate");
        }
        
        private static CdnFile tryReadFile(final JsonObject json, final String key) {
            final JsonValue value = json.get(key);
            if (value != null && value.isObject()) {
                return new CdnFile(value.asObject());
            }
            return null;
        }
    }
}
