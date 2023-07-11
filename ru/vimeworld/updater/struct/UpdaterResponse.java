// 
// Decompiled by Procyon v0.5.36
// 

package ru.vimeworld.updater.struct;

import java.util.Iterator;
import com.eclipsesource.json.JsonValue;
import java.util.ArrayList;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import java.util.List;

public class UpdaterResponse
{
    public CdnFile updater;
    public List<CdnFile> files;
    public Arch x32;
    public Arch x64;
    
    public UpdaterResponse(final JsonObject json) {
        this.updater = new CdnFile(json.get("updater").asObject());
        this.files = readFilesList(json.get("files").asArray());
        this.x32 = new Arch(json.get("x32").asObject());
        this.x64 = new Arch(json.get("x64").asObject());
    }
    
    public Arch getArch(final String name) {
        return name.equals("x32") ? this.x32 : this.x64;
    }
    
    private static List<CdnFile> readFilesList(final JsonArray arr) {
        final List<CdnFile> files = new ArrayList<CdnFile>();
        for (final JsonValue val : arr) {
            if (val.isObject()) {
                files.add(new CdnFile(val.asObject()));
            }
        }
        return files;
    }
    
    public static class Arch
    {
        public CdnFile starter;
        public CdnFile jre;
        public List<CdnFile> files;
        
        public Arch(final JsonObject json) {
            this.starter = new CdnFile(json.get("starter").asObject());
            this.jre = new CdnFile(json.get("jre").asObject());
            this.files = readFilesList(json.get("files").asArray());
        }
    }
}
