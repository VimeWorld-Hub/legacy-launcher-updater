// 
// Decompiled by Procyon v0.5.36
// 

package ru.vimeworld.updater;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import com.twmacinta.util.MD5;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.io.File;

public class Utils
{
    private static File cachedSelf;
    private static String cachedArch;
    
    public static File getSelf() {
        if (Utils.cachedSelf == null) {
            Utils.cachedSelf = new File(URI.create(Main.class.getProtectionDomain().getCodeSource().getLocation().toString()));
        }
        return Utils.cachedSelf;
    }
    
    public static String getArch() {
        if (Utils.cachedArch == null) {
            if (Main._ARCH_OVERRIDE != null) {
                Utils.cachedArch = (Main._ARCH_OVERRIDE.contains("64") ? "x64" : "x32");
            }
            else {
                final String arch = System.getenv("PROCESSOR_ARCHITECTURE");
                final String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
                Utils.cachedArch = ((arch.endsWith("64") || (wow64Arch != null && wow64Arch.endsWith("64"))) ? "x64" : "x32");
            }
        }
        return Utils.cachedArch;
    }
    
    public static void writeFileFull(final File file, final String data) {
        try {
            file.delete();
            Files.write(file.toPath(), data.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        }
        catch (Exception ex) {}
    }
    
    public static String readFileFull(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static String getHttpContent(String url) throws IOException {
        HttpURLConnection conn = null;
        try {
            int i = 0;
        Label_0133:
            while (i < 3) {
                conn = (HttpURLConnection)new URL(url).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("User-Agent", "VimeWorld Updater");
                conn.setRequestProperty("Cache-Control", "max-age=0");
                switch (conn.getResponseCode()) {
                    case 301:
                    case 302: {
                        url = new URL(new URL(url), conn.getHeaderField("Location")).toExternalForm();
                        conn.disconnect();
                        ++i;
                        continue;
                    }
                    default: {
                        break Label_0133;
                    }
                }
            }
            final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    public static String md5(final File file) {
        try {
            return MD5.asHex(MD5.getHash(file));
        }
        catch (Exception e) {
            return "NO";
        }
    }
    
    public static void delete(final File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (final File f : files) {
                    delete(f);
                }
            }
        }
        file.delete();
    }
    
    public static void createFile(final File file) throws IOException {
        if (file.exists()) {
            return;
        }
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        file.createNewFile();
    }
    
    public static void unzip(final File archive, final File output) throws IOException {
        if (!output.exists()) {
            output.mkdir();
        }
        try (final ZipInputStream zis = new ZipInputStream(new FileInputStream(archive))) {
            final byte[] buffer = new byte[65536];
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                final File newFile = new File(output, ze.getName());
                if (!ze.isDirectory()) {
                    createFile(newFile);
                    try (final OutputStream fos = new FileOutputStream(newFile)) {
                        int read;
                        while ((read = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, read);
                        }
                    }
                }
            }
        }
    }
    
    public static void loadAllClassesFromJar() {
        try (final JarFile jar = new JarFile(getSelf())) {
            final ClassLoader classLoader = Main.class.getClassLoader();
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    final String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
                    classLoader.loadClass(className);
                }
            }
        }
        catch (Exception ex) {}
    }
}
