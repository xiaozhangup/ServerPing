package me.xiaozhangup.test;

import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class TestPing extends Frame{

    @Test
    public void ping() throws IOException {
        InetAddress address = InetAddress.getByName("tcat.ncgs.top");
        int port = 22139;
        ServerPing ping = new ServerPing(new InetSocketAddress(address, port));
        StatusResponse response = ping.fetchData();
        System.out.println(response.getPlayers().getOnline());
    }

    @Test
    public void ui() {
        Frame fr = new Frame("Hello");
        fr.setSize(240,240);
        fr.setBackground(Color.white);
        MenuBar bar = new MenuBar();
        Menu menu = new Menu("刷新");
        bar.add(menu);
        fr.setMenuBar(bar);
        fr.setVisible(true);
    }

}
