package io.army.util;

import io.army.lang.Nullable;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class NetUtils {

    public static String getPrivateAsString(){
        return getPrivate().getHostAddress();
    }

    public static InetAddress getPrivate() {
        InetAddress target = doGetIp(null, null);
        Assert.state(target != null, "network state error");
        return target;
    }

    @Nullable
    public static InetAddress getPrivateIp4() {
        return doGetIp(Inet4Address.class, null);
    }


    @Nullable
    public static InetAddress getPrivateIp4(@Nullable PrivateType precedenceType) {
        return doGetIp(Inet4Address.class, precedenceType);
    }


    /*################################## blow private method ##################################*/

    @Nullable
    private static InetAddress doGetIp(@Nullable Class<? extends InetAddress> precedenceVersion
            , @Nullable PrivateType precedenceType) {
        try {
            Enumeration<NetworkInterface> nfs = NetworkInterface.getNetworkInterfaces();
            InetAddress target = doGetPrivateAddr(nfs, precedenceVersion, precedenceType);

            if (precedenceVersion != null && !precedenceVersion.isInstance(target)) {
                target = null;
            }
            return target;
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }


    @Nullable
    private static InetAddress doGetPrivateAddr(Enumeration<NetworkInterface> nfs
            , @Nullable Class<? extends InetAddress> precedenceVersion
            , @Nullable PrivateType precedenceType) throws SocketException {

        List<InetAddress> addressList = new ArrayList<>(6);

        for (NetworkInterface nf; nfs.hasMoreElements(); ) {
            nf = nfs.nextElement();
            if(nf.isLoopback() || !nf.isUp()){
                 continue;
            }
            for (Enumeration<InetAddress> addrs = nf.getInetAddresses(); addrs.hasMoreElements(); ) {
                InetAddress addr = addrs.nextElement();

                if (!addr.isSiteLocalAddress()) {
                    continue;
                }

                if (precedenceVersion == null) {
                    // no precedence
                    return addr;
                } else if (Inet6Address.class == precedenceVersion) {
                    if (addr instanceof Inet6Address) {
                        return addr;
                    }

                } else if (Inet4Address.class == precedenceVersion) {
                    if (precedenceType == null || precedenceType.isMatch(addr.getHostAddress())) {
                        return addr;
                    }
                }
                addressList.add(addr);
            }
        }

        InetAddress target = null;
        if (addressList.size() == 1 || precedenceType == null) {
            target = addressList.get(0);
        } else {
            for (InetAddress address : addressList) {
                if (precedenceType.isMatch(address.getHostAddress())) {
                    target = address;
                    break;
                }
            }
        }
        return target;
    }


}
