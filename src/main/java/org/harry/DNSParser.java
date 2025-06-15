package org.harry;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
public class DNSParser {

    public static DNSMessage parse(DatagramPacket packet) throws Exception {

        byte[] rawPacket = packet.getData();
        int length = packet.getLength();
        if (rawPacket.length < 12) {
            throw new IllegalArgumentException("DNS packet too short");
        }

        DNSMessage message = new DNSMessage();

        // Header
        message.id      = (short) DNSUtil.readUnsignedShort(rawPacket, 0);
        message.flags   = (short) DNSUtil.readUnsignedShort(rawPacket, 2);
        message.qdCount = (short) DNSUtil.readUnsignedShort(rawPacket, 4);
        message.anCount = (short) DNSUtil.readUnsignedShort(rawPacket, 6);
        message.nsCount = (short) DNSUtil.readUnsignedShort(rawPacket, 8);
        message.arCount = (short) DNSUtil.readUnsignedShort(rawPacket, 10);

        int offset = 12;  // after header

        for (int i = 0; i < message.qdCount; i++) {
            // Parse QNAME
            int[] offsetHolder = new int[]{offset};
            String qName = parseName(rawPacket, offsetHolder);
            offset = offsetHolder[0];

            // QTYPE and QCLASS
            if (offset + 4 > rawPacket.length) {
                throw new IllegalArgumentException("Malformed question section");
            }

            int qType  = DNSUtil.readUnsignedShort(rawPacket, offset);
            int qClass = DNSUtil.readUnsignedShort(rawPacket, offset + 2);
            offset += 4;

            message.questions.add(new DNSQuestion(qName, (short) qType, (short) qClass));

            System.out.println("Parsed QNAME: " + qName + ", QTYPE=" + qType + ", QCLASS=" + qClass);
        }

        return message;
    }

    private static String parseName(byte[] data, int[] offsetHolder) {
        StringBuilder name = new StringBuilder();
        int offset = offsetHolder[0];
        boolean jumped = false;
        int jumpOffset = -1;

        while (true) {
            int len = DNSUtil.readUnsignedByte(data, offset);

            // Pointer check: 11xxxxxx
            if ((len & 0xC0) == 0xC0) {
                if (!jumped) {
                    jumpOffset = offset + 2;
                }

                int pointer = ((len & 0x3F) << 8) | DNSUtil.readUnsignedByte(data, offset + 1);
                offset = pointer;
                jumped = true;
                continue;
            }

            if (len == 0) {
                offset++;
                break;
            }

            offset++;

            if (name.length() > 0) {
                name.append(".");
            }

            name.append(new String(data, offset, len));
            offset += len;
        }

        offsetHolder[0] = jumped ? jumpOffset : offset;
        return name.toString();
    }

}

