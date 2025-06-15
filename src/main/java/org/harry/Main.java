package org.harry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main {
    public static void main(String[] args) {

        try(DatagramSocket serverSocket = new DatagramSocket(2053)) {
            while(true) {
                final byte[] buf = new byte[512];
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSocket.receive(packet);
                System.out.println("Received data");
                byte[] responseData = DNSHandler.handleDNS_Request(packet);
                final DatagramPacket packetResponse = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
                serverSocket.send(packetResponse);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}