package me.xiaozhangup.ping;

import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Base64;

public class Ping implements WindowListener,ActionListener {

    public static JFrame fr = new JFrame("Sevrer Status");

    public static StatusResponse statusResponse;

    public static InetAddress address;

    static {
        try {
            address = InetAddress.getByName("tcat.ncgs.top");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static int port = 22139;

    public static void main(String[] args) throws IOException, InterruptedException {

        new Thread(() -> {
            while (true) {
                try {
                    ServerPing ping = new ServerPing(new InetSocketAddress(address, port));
                    statusResponse = ping.fetchData();
                    System.out.println(statusResponse);
                    update();
                    Thread.sleep(1000);
                } catch (Exception ignored) {}
            }
        }).start();

        new Ping();
    }

    public Ping() {

        Button button = new Button("刷新");
        button.addActionListener(this);

        fr.setSize(260,160);
        fr.setBackground(Color.white);
        fr.add(button,"North");
        fr.addWindowListener(this);
        fr.setAlwaysOnTop(true);
        fr.setResizable(false);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    public static void update() {
        fr.add(new TextRender(), BorderLayout.CENTER);
        fr.setVisible(true);
    }

    public static class TextRender extends JComponent {

        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawString("Time: " + statusResponse.getTime(), 110, 30);
            g.drawString("Online: " + statusResponse.getPlayers().getOnline() + "/" + statusResponse.getPlayers().getMax(), 110, 50);
            g.drawString("Serevr: " + statusResponse.getVersion().getName(), 110, 70);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(statusResponse.getFavicon().replace("data:image/png;base64,", "")));
            try {
                BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                g.drawImage(bufferedImage, 30, 15, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
