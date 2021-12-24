package io.army.util;

import io.army.lang.Nullable;

import java.net.*;
import java.util.*;

public abstract class NetUtils {

    public static String getPrivateAsString() {
        return getPrivate().getHostAddress();
    }

    public static InetAddress getPrivate() {
        InetAddress target = doGetIp(null, null);
        _Assert.state(target != null, "network state error");
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

            InetAddress target = doGetPrivateAddr(obtainDefaultInterfaceList(), precedenceVersion, precedenceType);

            if (precedenceVersion != null && !precedenceVersion.isInstance(target)) {
                target = null;
            }
            return target;
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }


    @Nullable
    private static InetAddress doGetPrivateAddr(List<NetworkInterface> nfs
            , @Nullable Class<? extends InetAddress> precedenceVersion
            , @Nullable PrivateType precedenceType) {

        List<InetAddress> addressList = new ArrayList<>(6);

        for (NetworkInterface nf : nfs) {
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

    private static List<NetworkInterface> obtainDefaultInterfaceList() throws SocketException {
        Map<String, NetworkInterface> map = obtainNetworkInterfaces();
        List<String> nameList = new ArrayList<>(map.keySet());
        nameList.sort(null);
        final String namePrefix = obtainDefaultInterfaceNamePrefix();
        List<NetworkInterface> interfaceList = new ArrayList<>();
        for (String name : nameList) {
            if (name.startsWith(namePrefix)) {
                interfaceList.add(map.get(name));
            }
        }
        return Collections.unmodifiableList(interfaceList);
    }

    public static String obtainDefaultInterfaceNamePrefix() {
        String osName = System.getProperty("os.name").toUpperCase();
        String namePrefix;
        if (osName.contains("WINDOWS")) {
            namePrefix = "eth";
        } else {
            namePrefix = "en";
        }
        return namePrefix;
    }


    public static Map<String, NetworkInterface> obtainNetworkInterfaces() throws SocketException {
        Enumeration<NetworkInterface> nfs = NetworkInterface.getNetworkInterfaces();
        Map<String, NetworkInterface> map = new HashMap<>();
        while (nfs.hasMoreElements()) {
            NetworkInterface nf = nfs.nextElement();
            if (!nf.isUp() || nf.isLoopback() || nf.isVirtual()) {
                continue;
            }
            map.put(nf.getName(), nf);
        }
        return Collections.unmodifiableMap(map);
    }


}
