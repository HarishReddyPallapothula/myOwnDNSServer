package org.harry;

public class DNSQuestion {
    public String qName;
    public short qType;
    public short qClass;

    public DNSQuestion(String qName, short qType, short qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }
}
