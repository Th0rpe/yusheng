package com.aurora.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;


/*
* 会初始化可以token ，这个token 在 result 不为空时刷新。
*
* */
public class DNSLOG {
    public static String DnslogDomain = "https://dig.pm";
    public static String domain="";
    public static String token = "";
    public static String result = "";
    public DNSLOG(){}
    /*
    * 获取结果
    * */
    public static Response getDnslogResult() throws MalformedURLException {
        String data =  "token="+token;
        Response response = new HttpRequest("https://dig.pm/get_results").Post(data);
        System.out.println(response.statusCode);
        System.out.println(response.responseBody);
        return response;
    }
    /*
    * 获取新的 域名
    * */
    public static Response getDnslogDomain() throws MalformedURLException {
        String data =  "domain=dns.1433.eu.org.";
        Response response = new HttpRequest("https://dig.pm/new_gen").Post(data);
        JSONObject json = (JSONObject) JSON.parse(response.responseBody);
        token = json.getString("token");
        domain = json.getString("domain");
        System.out.println(response.statusCode);
        System.out.println(response.responseBody);
        return response;
    }
    public static String getRandomDomain(){
        return Utils.getRandomString(4) +"."+domain;
    }
    public static void setDomain(String value){
        domain = value;
    }

    public static void main(String[] args) throws MalformedURLException {

    }
}
