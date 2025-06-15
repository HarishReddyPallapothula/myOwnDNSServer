package org.harry;

public class DNSUtil {
    public static int readUnsignedByte(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }

    public static int readUnsignedShort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}


