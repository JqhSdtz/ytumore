package util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author JQH
 * @since 上午 10:52 19/10/07
 */
public class RequestUtil {
    private static Set<String> ipAddressBlackSet = new HashSet();//IP地址黑名单
    private static ReentrantReadWriteLock ipAddressWRLock = new ReentrantReadWriteLock();//IP地址黑名单读写锁
    private static boolean initialized = false;

    public static void readIpBlackListFromFile() {
        if(initialized)
            return;
        FileUtil.readSetFromFile(ipAddressBlackSet, "ipBlackList.txt");
        System.out.println(ipAddressBlackSet.size() + " black ip have been loaded");
        initialized = true;
    }

    public static void writeIpBlackListToFile() {
        FileUtil.writeSetToFile(ipAddressBlackSet, "ipBlackList.txt");
        System.out.println(ipAddressBlackSet.size() + " black ip have been written to file");
    }

    public static String[] getUidAndPwdFromCookie(HttpServletRequest request) {
        String str[] = new String[2];
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("ymuid"))
                    str[0] = cookie.getValue();
                else if (cookie.getName().equals("ympwd"))
                    str[1] = cookie.getValue();
            }
        }
        return str;
    }

    public static String getRequestIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    public static boolean isOnBlackList(HttpServletRequest request) {
        String ip = RequestUtil.getRequestIpAddress(request);
        ipAddressWRLock.readLock().lock();
        boolean res = ipAddressBlackSet.contains(ip);
        ipAddressWRLock.readLock().unlock();
        return res;
    }
    
    public static int addToBlackList(String ip) {
        int res;
        ipAddressWRLock.writeLock().lock();
        if(ipAddressBlackSet.contains(ip))
            res = 1;//ip已在黑名单中
        else {
            ipAddressBlackSet.add(ip);
            res = 0;
        }
        ipAddressWRLock.writeLock().unlock();
        return res;
    }
    
    public static int removeFromBlackList(String ip) {
        int res;
        ipAddressWRLock.writeLock().lock();
        if(!ipAddressBlackSet.contains(ip))
            res = 1;//ip不在黑名单中
        else {
            ipAddressBlackSet.remove(ip);
            res = 0;
        }
        ipAddressWRLock.writeLock().unlock();
        return res;
    }

    public static String getBlackList() {
        StringBuffer buffer = new StringBuffer();
        ipAddressWRLock.readLock().lock();
        for(String str: ipAddressBlackSet)
            buffer.append(str + '\n');
        ipAddressWRLock.readLock().unlock();
        return buffer.toString();
    }
}
