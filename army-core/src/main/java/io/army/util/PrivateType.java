package io.army.util;

/**
 * ipv4
 */
public enum PrivateType {
    A(new String[]{"10."}),//10.0.0.0 ~ 10.255.255.255
    B(new String[]{"172.16.", "172.17.", "172.18.", "172.19.",
            "172.20.", "172.21.", "172.22.", "172.23.",
            "172.24.", "172.25.", "172.26.", "172.27.",
            "172.28.", "172.29.", "172.30.", "172.31."}),// 172.16.0.0 ~ 172.31.255.255
    C(new String[]{"192.168."}) // 192.168.0.0 ~ 192.168.255.255
    ;
    private final String[] start;

    PrivateType(String[] start) {
        this.start = start;
    }

    public boolean isMatch(String ipv4Address) {
        boolean match = false;
        for (String s : start) {
            if (ipv4Address.startsWith(s)) {
                match = true;
                break;
            }
        }
        return match;
    }


}
