package ru.vimeworld.updater;

import com.eclipsesource.json.Json;
import ru.vimeworld.updater.struct.CdnFile;
import ru.vimeworld.updater.struct.UpdaterResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.jar.Manifest;

public class Main {
    public static String _ARCH_OVERRIDE;
    public static boolean OFFLINE;
    public static boolean USE_CMD;
    private static File HOME_DIR;
    private static File FILE_LAUNCHER;
    private static File FILE_STARTER;
    private static File JRE_DIR;
    public static BufferedImage FAVICON;

    public static void main(final String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Error.load();

        final String appData = System.getenv("AppData");

        if (appData != null) {
            Main.HOME_DIR = new File(appData, ".vimeworld/");
        } else {
            Main.HOME_DIR = new File(System.getProperty("user.home", "."), ".vimeworld/");
        }
        for (int i = 0; i < args.length; ++i) {
            final String s = args[i];
            switch (s) {
                case "-a":
                case "--arch": {
                    Main._ARCH_OVERRIDE = args[++i];
                    break;
                }
                case "-d":
                case "--dir": {
                    Main.HOME_DIR = new File(args[++i], ".vimeworld/");
                    break;
                }
                case "--offline": {
                    Main.OFFLINE = true;
                    break;
                }
                case "--disable-cmd": {
                    Main.USE_CMD = false;
                    break;
                }
                case "-h":
                case "--help": {
                    final List<String> message = Arrays.asList("VimeWorld Updater", "", "Доступные параметры:", "  -h --help              Показать это сообщение", "  -a ARCH, --arch ARCH   Принудительно установить разрядность для запуска (x32 или x64)", "  -d DIR, --dir DIR      Установить папку для лаунчера ('.' для текущей папки). По умолчанию: %AppData%", "  --offline              Работать в режиме оффлаун, не загружать обновления", "  --disable-cmd          Запускать лаунчер напрямую, без cmd прослойки (нужна для запроса прав администратора)", "", "Примеры:", "  VimeWorld.exe -d D:\\test     - Запустить лаунчер из папки D:\\test", "  VimeWorld.exe -a x32         - Запустить 32-битную версию лаунчера");
                    if (System.getProperty("vw-l4j", "false").equals("false")) {
                        for (final String line : message) {
                            System.out.println(line);
                        }
                    } else {
                        Error.FONT = new Font("Monospaced", Font.PLAIN, 13);
                        Error.show("VimeWorld Updater | Помощь", message.toArray(new String[0]));
                    }
                    return;
                }
            }
        }
        Main.FILE_LAUNCHER = new File(Main.HOME_DIR, "launcher.jar");
        Main.JRE_DIR = new File(Main.HOME_DIR, "jre-" + Utils.getArch());
        if (new File(Main.HOME_DIR, "VimeWorld.exe").equals(Utils.getSelf())) {
            Main.FILE_STARTER = new File(Main.HOME_DIR, "VimeWorld_starter.exe");
        } else {
            Main.FILE_STARTER = new File(Main.HOME_DIR, "VimeWorld.exe");
        }
        String updaterResponseString = "";
        UpdaterResponse updaterResponse;
        try {
            updaterResponse = new UpdaterResponse(Json.parse(updaterResponseString = Utils.getHttpContent("https://cdn.vimeworld.com/2/updater")).asObject());
        } catch (Exception ex) {
            if (Main.FILE_LAUNCHER.exists() && Main.FILE_STARTER.exists() && Main.JRE_DIR.exists()) {
                launch(true);
            } else {
                Error.show("Ошибка", ex, "", "      Невозможно загрузить лаунчер", "", "Проверьте подключение к интернету. Если интернет работает хорошо, значит выключить антивирус и запустить лаунчер снова.", "Причиной этой ошибки также могут быть программы, влияющие на работу интернета (торренты, скайп, браузеры и т.д.). Попробуйте выключить их и запустить лаунчер.", "", "Если ничего не помогло, то скачайте новую версию с сайта https://vimeworld.com", "", "Ответ сервера: " + updaterResponseString);
            }
            return;
        }
        try {
            final File self = Utils.getSelf();
            if (!self.exists()) {
                Error.show("Ошибка", "", "      Невозможно определить путь лаунчера", "", "Попробуйте переместить этот файл куда-нибудь в другое место и запустить еще раз.");
                return;
            }
            if (!self.isDirectory() && !updaterResponse.updater.md5.equals(Utils.md5(self))) {
                final ClassLoader classLoader = Main.class.getClassLoader();
                Utils.loadAllClassesFromJar();
                if (classLoader instanceof Closeable) {
                    ((Closeable) classLoader).close();
                }
                try {
                    new DownloadableFile(self, updaterResponse.updater, "Обновление загрузчика").download();
                } catch (Throwable ex2) {
                    Error.show("Ошибка", ex2, "", "      Не удалось загрузить обновление апдейтера.", "", "В большинстве случаев в этой проблеме виноват антивирус. Попробуйте выключить его и попробовать снова.", "Ошибка также может возникнуть при плохом подключении к интернету. Выключите торренты, скайпы, браузеры, другие игры и попробуйте еще раз.");
                    return;
                }
                final URLClassLoader cl = new URLClassLoader(new URL[]{self.toURI().toURL()}, null, null);
                String mainClass;
                try {
                    mainClass = new Manifest(cl.getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes().getValue("Main-Class");
                } catch (Exception ex5) {
                    mainClass = Main.class.getName();
                }
                cl.loadClass(mainClass).getDeclaredMethod("main", String[].class).invoke(null, args);
                cl.close();
                return;
            }
            final UpdaterResponse.Arch arch = updaterResponse.getArch(Utils.getArch());
            final File zipMd5File = new File(Main.JRE_DIR, "zip-md5");
            final String zipMd5 = Utils.readFileFull(zipMd5File);
            if (!Main.JRE_DIR.exists() || !arch.jre.md5.equals(zipMd5)) {
                final File jreZip = new File(Main.JRE_DIR, "jre.zip");
                try {
                    if (Main.JRE_DIR.exists()) {
                        Utils.delete(Main.JRE_DIR);
                    }
                    new DownloadableFile(jreZip, arch.jre, "Загрузка Java " + Utils.getArch()).download();
                } catch (Exception ex3) {
                    Error.show("Ошибка", ex3, "", "      Не удалось загрузить Java Runtime.", "", "В большинстве случаев в этой проблеме виноват антивирус. Попробуйте выключить его и попробовать снова.", "Ошибка также может возникнуть при плохом подключении к интернету. Выключите торренты, скайпы, браузеры, другие игры и попробуйте еще раз.");
                    return;
                }
                try {
                    Utils.unzip(jreZip, Main.JRE_DIR);
                    Utils.delete(jreZip);
                } catch (Exception ex3) {
                    Error.show("Ошибка", ex3, "", "      Не удалось распаковать Java Runtime.", "", "В большинстве случаев в этой проблеме виноват антивирус. Попробуйте выключить его и попробовать снова.");
                    return;
                }
                Utils.writeFileFull(zipMd5File, arch.jre.md5);
            }
            final List<DownloadableFile> downloadList = new ArrayList<>();
            downloadList.add(new DownloadableFile(Main.FILE_STARTER, arch.starter, "Загрузка запускатора"));
            for (final CdnFile cdnFile : updaterResponse.files) {
                downloadList.add(new DownloadableFile(new File(Main.HOME_DIR, cdnFile.path), cdnFile));
            }
            for (final CdnFile cdnFile : arch.files) {
                downloadList.add(new DownloadableFile(new File(Main.HOME_DIR, cdnFile.path), cdnFile));
            }
            for (final DownloadableFile f : downloadList) {
                if (f.isOutdated()) {
                    try {
                        f.download();
                    } catch (Exception ex4) {
                        Error.show("Ошибка", ex4, "", "      Не удалось загрузить обновление лаунчера.", "", "В большинстве случаев в этой проблеме виноват антивирус. Попробуйте выключить его и попробовать снова.", "Ошибка также может возникнуть при плохом подключении к интернету. Выключите торренты, скайпы, браузеры, другие игры и попробуйте еще раз.");
                        return;
                    }
                }
            }
            launch(Main.USE_CMD);
        } catch (Exception e) {
            Error.show("Ошибка", e, "", "Если не знаете что с этим делать, то поищите ответ на форуме https://forum.vimeworld.com/forum/38- или напишите в сообщения группы https://vk.com/vimeworld c небольшим описанием вашей ситуации и скриншотом.");
        }
    }

    private static List<String> getJvmArgs() {
        final List<String> args = new ArrayList<>();
        try {
            final File confFile = new File(Main.HOME_DIR, "config");
            if (confFile.exists()) {
                try {
                    for (final String line : Files.readAllLines(confFile.toPath(), StandardCharsets.UTF_8)) {
                        if (line.startsWith("memory:")) {
                            int megs = Integer.parseInt(line.substring(7));
                            if (megs < 300) {
                                megs = 300;
                            }
                            if (Utils.getArch().equals("x32")) {
                                if (megs > 1300) {
                                    megs = 1300;
                                }
                            } else if (megs > 2000) {
                                megs = 2000;
                            }
                            args.add("-Xmx" + megs + "M");
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (args.isEmpty()) {
                if (Utils.getArch().equals("x32")) {
                    args.add("-Xmx768M");
                } else {
                    args.add("-Xmx1G");
                }
            }
            args.add("-XX:+UnlockExperimentalVMOptions");
            args.add("-XX:+UseG1GC");
            final File argsFile = new File(Main.HOME_DIR, "jvmargs.txt");
            if (argsFile.exists()) {
                args.addAll(Files.readAllLines(argsFile.toPath(), StandardCharsets.UTF_8));
            }
        } catch (Exception ignored) {
        }
        return args;
    }

    private static void launch(final boolean useCmd) {
        try {
            final ProcessBuilder pb = new ProcessBuilder();
            final String appdata = Main.HOME_DIR.getParentFile().getAbsolutePath();
            pb.directory(Main.HOME_DIR);
            pb.environment().put("APPDATA", appdata);
            final List<String> args = new ArrayList<>();
            if (useCmd) {
                args.add("cmd");
                args.add("/d");
                args.add("/c");
                args.add("start");
                args.add("\"\"");
                args.add("/D");
                args.add("\"" + Main.HOME_DIR.getAbsolutePath() + "\"");
            }
            args.add("\"" + Main.FILE_STARTER.getAbsolutePath() + "\"");
            args.add("-home");
            args.add("\"" + Main.HOME_DIR.getAbsolutePath() + "\\\"");
            args.add("-jre");
            args.add("\"" + Main.JRE_DIR.getAbsolutePath() + "\"");
            args.add("-jvmargs");
            args.addAll(getJvmArgs());
            args.add("-jar");
            args.add("\"" + Main.FILE_LAUNCHER.getName() + "\"");
            args.add("-updater");
            //args.add(Base64.encodeToString(Utils.getSelf().getAbsolutePath().getBytes(StandardCharsets.UTF_8), 2));
            args.add(Base64.encodeToString("C:\\Program Files (x86)\\xtrafrancyz\\VimeWorld.exe".getBytes(StandardCharsets.UTF_8), 2));
            args.add("-appdata");
            args.add(Base64.encodeToString(appdata.getBytes(StandardCharsets.UTF_8), 2));
            pb.command(args);
            pb.start();
        } catch (Exception ex) {
            if (useCmd && ex.getMessage().contains("Cannot run program \"cmd\"")) {
                launch(false);
            } else if (ex.getMessage().contains("error=740")) {
                Error.show("Недостаточно прав", "", "      Запустите лаунчер от имени администратора");
            } else {
                Error.show("Ошибка", ex, "", "      Не удалось запустить лаунчер", "", "Данная ошибка может возникать из-за антивируса или других программ, влияющих на работу системы. Попробуйте выключить антивирус и запустить лаунчер снова.", "Если не помогло, перезагрузите компьютер.", "", "Если всё равно появляется эта ошибка, то поищите ответ на форуме https://forum.vimeworld.com/forum/38- или напишите в сообщения группы https://vk.com/vimeworld c небольшим описанием вашей ситуации и скриншотом.");
            }
        }
    }

    static {
        Main._ARCH_OVERRIDE = null;
        Main.OFFLINE = false;
        Main.USE_CMD = true;
        Main.FAVICON = null;
        try {
            Main.FAVICON = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("favicon.png")));
        } catch (Exception ignored) {
        }
    }

    private static class DownloadableFile {
        public File file;
        public CdnFile cdnFile;
        public String displayName;

        public DownloadableFile(final File file, final CdnFile cdnFile) {
            this(file, cdnFile, "Загрузка: " + cdnFile.path);
        }

        public DownloadableFile(final File file, final CdnFile cdnFile, final String displayName) {
            this.file = file;
            this.cdnFile = cdnFile;
            this.displayName = displayName;
        }

        public boolean isOutdated() {
            return !this.cdnFile.md5.equals(Utils.md5(this.file));
        }

        public void download() throws Exception {
            if (this.cdnFile.versions != null && this.cdnFile.versions.deflate != null) {
                new Downloader(this.cdnFile.versions.deflate.url, this.file, this.displayName).setEncoding(Downloader.Encoding.DEFLATE).download();
            } else {
                new Downloader(this.cdnFile.url, this.file, this.displayName).download();
            }
        }
    }
}
