package me.xiaozhangup.ping;

import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Ping {

    public static void main(String[] args) throws IOException, InterruptedException {
        final InetAddress address = InetAddress.getByName("tcat.ncgs.top");
        final int port = 22139;
        while (true) {
            ServerPing ping = new ServerPing(new InetSocketAddress(address, port));
            StatusResponse response = ping.fetchData();
            double online = response.getPlayers().getOnline();
            double max = response.getPlayers().getMax();
            double point = (online / max) * 100;
            StringBuilder line = new StringBuilder("|");
            for (int i = 0; i < point; i++) {
                line.append("|");
            }
            System.out.println((int) online + "/" + (int) max + " " + line);
            Thread.sleep(1000);
        }
    }

}
