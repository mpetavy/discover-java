package org.mpetavy.discover;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.HashMap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
@EnableConfigurationProperties(DiscoverConfiguration.class)
public class Discover {
    private DiscoverConfiguration properties;

    public static HashMap<InetAddress,String> FindPeers(DiscoverConfiguration properties) throws Exception {
        log.info(String.format("disconver port: %d", properties.port));
        log.info(String.format("disconver uid: %s", properties.uid));
        log.info(String.format("disconver timeout: %d", properties.timeout));

        HashMap<InetAddress,String> result = new HashMap<>();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            Enumeration allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (allNetworkInterfaces.hasMoreElements()) {
                NetworkInterface anInterface = (NetworkInterface) allNetworkInterfaces.nextElement();

                if (anInterface.isLoopback() || !anInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : anInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    try {
                        DatagramPacket packet = new DatagramPacket(properties.uid.getBytes(), properties.uid.getBytes().length, broadcast, properties.port);
                        socket.send(packet);

                        log.info("broadcast search request on " + broadcast + " port " + properties.port);
                    } catch (Exception e) {
                        log.debug("unable to send datagramm", e);
                    }
                }
            }

            boolean running;
            byte[] buf = new byte[1024];

            socket.setBroadcast(false);
            socket.setSoTimeout(properties.timeout);

            try {
                for (; ; ) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    String received = new String(packet.getData(), 0, packet.getLength());

                    log.info(String.format("received from %s: %s",packet.getAddress(),received));

                    result.put(packet.getAddress(),received);
                }
            } catch (SocketTimeoutException e) {
                //suppress
            }
        }

        return result;
    }
}
