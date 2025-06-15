package org.harry;

import java.io.ByteArrayOutputStream;

public class ByteArrayBuilder {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public void writeByte(byte b) {
        out.write(b);
    }

    public void writeBytes(byte[] b) {
        out.write(b, 0, b.length);
    }

    public void writeShort(short value) {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    public void writeInt(int value) {
        out.write((value >> 24) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    public byte[] toByteArray() {
        return out.toByteArray();
    }
}

