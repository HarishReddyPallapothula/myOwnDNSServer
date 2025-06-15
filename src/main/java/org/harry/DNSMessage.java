package org.harry;

import java.util.ArrayList;
import java.util.List;

public class DNSMessage {

    public short id;
    public short flags;
    public short qdCount;
    public short anCount;
    public short nsCount;
    public short arCount;

    @Override
    public String toString() {
        return "DNSMessage{" +
                "id=" + id +
                ", flags=" + flags +
                ", qdCount=" + qdCount +
                ", anCount=" + anCount +
                ", nsCount=" + nsCount +
                ", arCount=" + arCount +
                ", questions=" + questions +
                '}';
    }

    public List<DNSQuestion> questions = new ArrayList<>();
}

