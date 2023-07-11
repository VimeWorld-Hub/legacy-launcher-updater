// 
// Decompiled by Procyon v0.5.36
// 

package ru.vimeworld.updater;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Image;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.Font;

public class Error
{
    public static Font FONT;
    private static boolean isSystemLaf;
    
    public static void load() {
        try {
            Error.class.getClassLoader().loadClass(Frame.class.getName());
        }
        catch (ClassNotFoundException ex) {}
    }
    
    public static void show(final String title, final String... msg) {
        setLAF();
        new Frame(title, implode(msg)).setVisible(true);
    }
    
    public static void show(final String title, final Throwable th, final String... msg) {
        setLAF();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        String header;
        if (msg.length == 0) {
            header = "\n      Что-то пошло не так...\n";
        }
        else {
            header = implode(msg);
        }
        new Frame(title, header + "\n\n\nИнформация для разработчиков:\n\n" + sw.toString()).setVisible(true);
    }
    
    private static String implode(final String... msg) {
        final StringBuilder sb = new StringBuilder();
        for (final String s : msg) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
    
    private static void setLAF() {
        if (Error.isSystemLaf) {
            return;
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {}
        Error.isSystemLaf = true;
    }
    
    static {
        Error.FONT = new Font("Verdana", 0, 13);
        Error.isSystemLaf = false;
    }
    
    private static class Frame extends JFrame
    {
        public Frame(final String title, final String message) {
            super(title);
            this.setDefaultCloseOperation(3);
            this.getRootPane().setLayout(new BorderLayout());
            this.setSize(800, 500);
            this.setLocationRelativeTo(null);
            this.setIconImage(Main.FAVICON);
            final JTextArea text = new JTextArea();
            text.setTabSize(2);
            text.setEditable(false);
            text.setText(message);
            text.setFont(Error.FONT);
            text.setWrapStyleWord(true);
            text.setLineWrap(true);
            final JScrollPane scroll = new JScrollPane(text);
            this.getRootPane().add(scroll, "Center");
        }
    }
}
