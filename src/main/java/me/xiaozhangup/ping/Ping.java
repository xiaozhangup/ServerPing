package me.xiaozhangup.ping;

import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;

public class Ping implements WindowListener {

    public static final TextRender COMP = new TextRender();
    private static final JFrame fr = new JFrame("Sevrer Status");
    private static StatusResponse statusResponse;
    private static BufferedImage bufferedImage;

    private int xOld;
    private int yOld;


    public Ping() {

        Button button = new Button("手动刷新");
        button.addActionListener(e -> update());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(Color.LIGHT_GRAY);

        Button close = new Button("退出程序");
        close.addActionListener(e -> System.exit(0));
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setBackground(Color.LIGHT_GRAY);

        Button top = new Button("置顶");
        top.addActionListener(e -> {
            if (fr.isAlwaysOnTop()) {
                fr.setAlwaysOnTop(false);
                top.setLabel("普通");
            } else {
                fr.setAlwaysOnTop(true);
                top.setLabel("置顶");
            }
        });
        top.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setBackground(Color.LIGHT_GRAY);

        fr.setSize(260, 140);
        fr.setBackground(Color.white);
        fr.add(button, "North");
        fr.add(close, "South");
        fr.add(top, "West");
        fr.addWindowListener(this);
        fr.setResizable(false);
        fr.setUndecorated(true);
        fr.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        fr.setAlwaysOnTop(true);
        fr.setLocation(100, 100);

        fr.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xOld = e.getX();
                yOld = e.getY();
            }
        });

        fr.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int xOnScreen = e.getXOnScreen();
                int yOnScreen = e.getYOnScreen();
                int xx = xOnScreen - xOld;
                int yy = yOnScreen - yOld;
                fr.setLocation(xx, yy);
            }
        });

    }

    public static void main(String[] args) throws IOException {
        ServerPing ping = new ServerPing(new InetSocketAddress("k1.dimc.link", 23001));
        statusResponse = ping.fetchData();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(statusResponse.getFavicon().replace("data:image/png;base64,", "")));
        bufferedImage = ImageIO.read(byteArrayInputStream);
        fr.setIconImage(bufferedImage);

        new Ping();

        new Thread(() -> {
            while (true) {
                try {
                    statusResponse = ping.fetchData();
                    update();
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    public static void update() {
        fr.add(COMP, BorderLayout.CENTER);
        fr.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(1);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public static class TextRender extends JComponent {

        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawString("用时: " + statusResponse.getTime(), 90, 30);
            g.drawString("在线: " + statusResponse.getPlayers().getOnline() + "/" + statusResponse.getPlayers().getMax(), 90, 50);
            g.drawString("服务端: " + statusResponse.getVersion().getName(), 90, 70);
            g.drawImage(bufferedImage, 10, 15, this);

        }

    }
}
