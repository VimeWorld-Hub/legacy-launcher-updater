// 
// Decompiled by Procyon v0.5.36
// 

package ru.vimeworld.updater;

import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import javax.swing.JLabel;
import java.awt.Container;
import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.Timer;
import java.io.File;
import java.awt.Color;
import java.awt.event.ActionListener;

public class Downloader implements ActionListener
{
    private static final Color TOP_COLOR;
    private static final Color BOT_COLOR;
    private final String url;
    private final File output;
    private final String displayFile;
    private Encoding encoding;
    private Frame frame;
    private long total;
    private long lastDownloaded;
    private long downloaded;
    
    public Downloader(final String url, final File output) {
        this(url, output, output.getName());
    }
    
    public Downloader(final String url, final File output, final String displayFile) {
        this.encoding = Encoding.PLAIN;
        this.url = url;
        this.output = output;
        this.displayFile = displayFile;
    }
    
    public Downloader setEncoding(final Encoding encoding) {
        this.encoding = encoding;
        return this;
    }
    
    public void download() throws Exception {
        this.frame = new Frame(this.displayFile);
        final Timer timer = new Timer(50, this);
        timer.setRepeats(true);
        timer.setInitialDelay(50);
        timer.start();
        HttpURLConnection conn = null;
        try {
            if (!this.output.exists()) {
                if (this.output.getParentFile() != null && !this.output.getParentFile().exists()) {
                    this.output.getParentFile().mkdirs();
                }
                this.output.createNewFile();
            }
            URL url0 = new URL(this.url);
            int i = 0;
        Label_0207:
            while (i < 3) {
                conn = (HttpURLConnection)url0.openConnection();
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("User-Agent", "VimeWorld Updater");
                switch (conn.getResponseCode()) {
                    case 301:
                    case 302: {
                        url0 = new URL(url0, conn.getHeaderField("Location"));
                        conn.disconnect();
                        ++i;
                        continue;
                    }
                    default: {
                        break Label_0207;
                    }
                }
            }
            try (final OutputStream os = new FileOutputStream(this.output)) {
                InputStream is = conn.getInputStream();
                if (this.encoding == Encoding.DEFLATE) {
                    is = new InflaterInputStream(is, new Inflater(), 65536);
                }
                final byte[] buf = new byte[65536];
                this.total = conn.getContentLengthLong();
                int size;
                while ((size = is.read(buf)) != -1) {
                    os.write(buf, 0, size);
                    this.downloaded += size;
                }
            }
        }
        catch (Exception ex) {
            throw new Exception("Could not load file from URL: " + this.url, ex);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            timer.stop();
            this.frame.setVisible(false);
            this.frame.dispose();
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (this.total == 0L || this.downloaded == this.lastDownloaded) {
            return;
        }
        this.lastDownloaded = this.downloaded;
        this.frame.progress.setValue(Math.min(100, (int)(100.0 * (this.downloaded / (double)this.total))));
        this.frame.progress.setString(this.downloaded / 1024L + "/" + this.total / 1024L + " Кб.");
    }
    
    static {
        TOP_COLOR = new Color(68, 163, 226);
        BOT_COLOR = new Color(59, 143, 199);
    }
    
    private static class Frame extends JFrame
    {
        public JProgressBar progress;
        
        public Frame(final String displayText) {
            super("VimeWorld Updater");
            this.setUndecorated(true);
            this.setSize(300, 115);
            this.setDefaultCloseOperation(3);
            this.setLocationRelativeTo(null);
            this.setAlwaysOnTop(true);
            this.setContentPane(new Panel());
            this.getContentPane().add(this.progress = new ProgressBar());
            this.getContentPane().add(new VimeWorldLabel());
            final JLabel label = new JLabel(displayText);
            label.setFont(label.getFont().deriveFont(13.0f));
            label.setSize(label.getPreferredSize());
            label.setLocation(150 - label.getSize().width / 2, 90);
            label.setForeground(Color.WHITE);
            this.getContentPane().add(label);
            final MouseAdapter ma = new MouseAdapter() {
                private Point startMouseLoc = new Point(0, 0);
                private final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                
                @Override
                public void mouseDragged(final MouseEvent me) {
                    final Point currLoc = me.getLocationOnScreen();
                    int x = currLoc.x - this.startMouseLoc.x;
                    int y = currLoc.y - this.startMouseLoc.y;
                    if (x <= 0) {
                        x = 0;
                    }
                    if (Frame.this.getWidth() + x >= this.screen.width) {
                        x = this.screen.width - Frame.this.getWidth();
                    }
                    if (y <= 0) {
                        y = 0;
                    }
                    if (y + Frame.this.getHeight() >= this.screen.height) {
                        y = this.screen.height - Frame.this.getHeight();
                    }
                    Frame.this.setLocation(x, y);
                }
                
                @Override
                public void mousePressed(final MouseEvent e) {
                    this.startMouseLoc = e.getPoint();
                }
            };
            this.addMouseMotionListener(ma);
            this.addMouseListener(ma);
            this.setIconImage(Main.FAVICON);
            this.setVisible(true);
        }
    }
    
    private static class VimeWorldLabel extends JLabel
    {
        public VimeWorldLabel() {
            super("VimeWorld");
            this.setFont(this.getFont().deriveFont(45.0f));
            this.setSize(this.getPreferredSize());
            this.setLocation(150 - this.getSize().width / 2, 0);
            this.setForeground(Color.WHITE);
        }
    }
    
    private static class Panel extends JPanel
    {
        public Panel() {
            this.setLayout(null);
            this.setBorder(BorderFactory.createLineBorder(Downloader.TOP_COLOR.darker()));
        }
        
        @Override
        protected void paintComponent(final Graphics grphcs) {
            super.paintComponent(grphcs);
            final Graphics2D g2d = (Graphics2D)grphcs;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            final GradientPaint gp = new GradientPaint(0.0f, 0.0f, Downloader.TOP_COLOR, 0.0f, (float)this.getHeight(), Downloader.BOT_COLOR);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
    
    private static class ProgressBar extends JProgressBar
    {
        public ProgressBar() {
            this.setMaximum(100);
            this.setMinimum(0);
            this.setSize(250, 25);
            this.setLocation(25, 60);
            this.setBackground(Color.WHITE);
            this.setForeground(new Color(0, 200, 255));
            this.setBorderPainted(false);
            this.setStringPainted(true);
            this.setFont(this.getFont().deriveFont(13.0f));
            this.setString("Соединение...");
            this.setUI(new BasicProgressBarUI() {
                @Override
                protected Color getSelectionBackground() {
                    return Downloader.BOT_COLOR;
                }
                
                @Override
                protected Color getSelectionForeground() {
                    return Color.WHITE;
                }
            });
        }
    }
    
    public enum Encoding
    {
        PLAIN, 
        DEFLATE;
    }
}
