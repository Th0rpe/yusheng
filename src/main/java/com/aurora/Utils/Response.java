package com.aurora.Utils;

import java.util.List;
import java.util.Map;

public class Response {
    public int statusCode;
    public String responseBody ;
    public Map<String, List<String>> responseHeader;
    public Map<String,List<String>> requestHeader;
    public Response(int statusCode, String responseBody,Map<String, List<String>> responseHeader){
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.responseHeader = responseHeader;
    }
    public void setRequestHeader(Map<String,List<String>> map){
        this.requestHeader = map;
    }

}
