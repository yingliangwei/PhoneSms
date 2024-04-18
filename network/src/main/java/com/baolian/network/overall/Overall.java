package com.baolian.network.overall;

import java.util.ArrayList;
import java.util.List;

public class Overall {
    public static String host = "tingdao.cc";//只能使用ip地址，域名在vpn情况下获取不到ip
    public static String ip = "60.247.153.224";
    public static boolean isProxy;//全局代理开启
    public static java.net.Proxy proxy;//全局代理地址
    public static boolean intercept;//拦截地址
    public static List<String> intercepts = new ArrayList<>();

    public static void setProxy(java.net.Proxy proxy) {
        Overall.proxy = proxy;
    }

    public static void setIsProxy(boolean isProxy) {
        Overall.isProxy = isProxy;
    }

    public static void setIntercept(boolean intercept) {
        Overall.intercept = intercept;
    }

    public static void addIntercept(String address) {
        intercepts.add(address);
    }

    public static boolean isIntercept(String address) {
        return intercepts.contains(address);
    }

    public static boolean isVpn() {
        return isProxy || intercept;
    }

}
