package org.harry;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class DNSHandler {

    static byte[] handleDNS_Request(DatagramPacket packet) throws Exception {
        DNSMessage message = DNSParser.parse(packet);
        System.out.println("Parsed the packet" + message.toString());
        byte[] responseData = createDNSResponse(message, resolveIPAddresses(message));
        return responseData;
    }

    static byte[] createDNSResponse(DNSMessage message, List<byte[]> ipAddresses){

        ByteArrayBuilder builder = new ByteArrayBuilder();

        // HEADER
        int requestFlags = message.flags & 0xFFFF;
        int qr = 1;
        int opcode = (requestFlags >> 11) & 0b1111;
        int aa = 0;
        int tc = 0;
        int rd = (requestFlags >> 8) & 0b1;
        int ra = 0;
        int z = 0;
        int rcode = (opcode == 0) ? 0 : 4;

        int flags = (qr << 15) | (opcode << 11) | (aa << 10) | (tc << 9)
                | (rd << 8) | (ra << 7) | (z << 4) | rcode;


        builder.writeShort(message.id);                    // ID
        builder.writeShort((short) flags);                // Flags
        builder.writeShort((short) message.qdCount);                     // QDCOUNT
        builder.writeShort((short) ipAddresses.size());                     // ANCOUNT
        builder.writeShort((short) 0);                     // NSCOUNT
        builder.writeShort((short) 0);                     // ARCOUNT

        System.out.println("built header section");
        // QUESTION SECTION (same as request)
        List<Integer> qNameOffsets = new ArrayList<>();
        int currentOffset = 12; // Header is 12 bytes

        for (DNSQuestion q : message.questions) {
            qNameOffsets.add(currentOffset);

            String[] labels = q.qName.split("\\.");
            for (String label : labels) {
                builder.writeByte((byte) label.length());
                builder.writeBytes(label.getBytes());
                currentOffset += 1 + label.length();
            }
            builder.writeByte((byte) 0);                       // Terminate QNAME
            builder.writeShort(q.qType);                       // QTYPE
            builder.writeShort(q.qClass);                      // QCLASS
            currentOffset += 1 + 2 + 2;
        }

        System.out.println("Questions written");

        int answerCount = 0;

        for (int i = 0; i < ipAddresses.size(); i++) {
            byte[] ip = ipAddresses.get(i);

            // Get pointer to corresponding QNAME
            int pointerOffset = qNameOffsets.get(Math.min(i, qNameOffsets.size() - 1));
            int pointer = 0xC000 | pointerOffset;

            builder.writeShort((short) pointer);               // NAME (pointer to QNAME)
            builder.writeShort((short) (ip.length == 4 ? 1 : 28)); // TYPE = A or AAAA
            builder.writeShort((short) 1);                     // CLASS = IN
            builder.writeInt(60);                              // TTL
            builder.writeShort((short) ip.length);             // RDLENGTH
            builder.writeBytes(ip);                            // RDATA = IP

            answerCount++;
        }

        System.out.println("Answers written: " + answerCount);

        return builder.toByteArray();

    }

    public static List<byte[]> resolveIPAddresses(DNSMessage message) {
        List<byte[]> result = new ArrayList<>();
        System.out.println("Resolving IP addresses (using dummy IPs)");

        for (DNSQuestion question : message.questions) {
            if (question.qType == 1) { // A record (IPv4)
                result.add(new byte[]{1, 2, 3, 4});  // Dummy IP for each A record
            } else if (question.qType == 28) { // AAAA record (IPv6)
                result.add(new byte[16]); // Dummy IPv6 (all 0s)
            } else {
                System.out.println("Unsupported qType: " + question.qType);
            }
        }

        System.out.println("IpAddress size : " + result.size());
        return result;
    }


}

