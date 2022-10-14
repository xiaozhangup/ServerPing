package me.xiaozhangup.ping;

import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Base64;

public class Ping implements WindowListener {

    public static JFrame fr = new JFrame("Sevrer Status");

    public static StatusResponse statusResponse;

    public static InetAddress address;
    public static BufferedImage bufferedImage;
    public static int port = 23001;
    int xOld = 0;
    int yOld = 0;

    static {
        try {
            address = InetAddress.getByName("k1.dimc.link");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public Ping() {

        Button button = new Button("手动刷新");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(Color.LIGHT_GRAY);

        Button close = new Button("退出程序");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setBackground(Color.LIGHT_GRAY);

        fr.setSize(260, 140);
        fr.setBackground(Color.white);
        fr.add(button, "North");
        fr.add(close, "South");
        fr.addWindowListener(this);
        fr.setResizable(false);
        fr.setUndecorated(true);

        fr.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xOld = e.getX();//记录鼠标按下时的坐标
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
                fr.setLocation(xx, yy);//设置拖拽后，窗口的位置
            }
        });

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        statusResponse = new ServerPing(new InetSocketAddress(address, port)).fetchData();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(statusResponse.getFavicon().replace("data:image/png;base64,", "")));
        bufferedImage = ImageIO.read(byteArrayInputStream);
        fr.setIconImage(bufferedImage);

        new Ping();

        new Thread(() -> {
            while (true) {
                try {
                    ServerPing ping = new ServerPing(new InetSocketAddress(address, port));
                    statusResponse = ping.fetchData();
                    update();
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    public static void update() {
        fr.add(new TextRender(), BorderLayout.CENTER);
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
            g.drawString("用时: " + statusResponse.getTime(), 110, 30);
            g.drawString("在线: " + statusResponse.getPlayers().getOnline() + "/" + statusResponse.getPlayers().getMax(), 110, 50);
            g.drawString("服务端: " + statusResponse.getVersion().getName(), 110, 70);
            g.drawImage(bufferedImage, 30, 15, this);

        }

    }
}
