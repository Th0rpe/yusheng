package com.aurora.Utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class HttpRequest {
    public  LinkedHashMap<String,String> headers = new LinkedHashMap<String, String>();
    public URL realUrl;
    public URLConnection conn;
    public HttpURLConnection httpconn;
    public HttpsURLConnection Httpsconn;
    public Proxy proxy = null;
    public String target;
    public boolean IsHttps = false;
    public HttpRequest(String url) throws MalformedURLException {
        target = url;
        realUrl = new URL(url);
        if (url.startsWith("https")){
            IsHttps = true;
        }
        if (Cache.uiController.currentProxy.get("proxy")!=null){
            proxy = (Proxy) Cache.uiController.currentProxy.get("proxy");
        }
        String host = url.substring(url.indexOf("/")+1);
        InitHeaders();
    }
    public Boolean setProxy(String proxyAddr,int port){
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddr,port));
            return true;
    }
    public Response Connect(String Method,Object body,Boolean IsHttps) {

        try {
            if (IsHttps) {
                if (proxy!=null){
                    Httpsconn = (HttpsURLConnection) realUrl.openConnection(proxy);

                }else {
                    Httpsconn = (HttpsURLConnection) realUrl.openConnection();

                }
                Httpsconn.setRequestMethod(Method);
                //信任所有ssl证书和主机
                TrustManager[] trustManagers = {new HttpsTrustManager()};
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustManagers, new SecureRandom());
                Httpsconn.setSSLSocketFactory(context.getSocketFactory());
                Httpsconn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                return SendRequest(Httpsconn,body);
            } else {
                if (proxy!=null){
                    httpconn = (HttpURLConnection) realUrl.openConnection(proxy);
                }else {
                    httpconn = (HttpURLConnection) realUrl.openConnection();
                }
                // 打开和URL之间的连接
                httpconn.setRequestMethod(Method);
                return SendRequest(httpconn,body);
            }
        }catch (Exception e){
            return null;
        }
    }
    public Response SendRequest(HttpURLConnection Conn,Object body) throws IOException {
        BufferedReader in = null;
        String result = "";
        int statuscode = 0;
        OutputStreamWriter wr;
        Map<String, List<String>> headermap = null;
        Map<String,List<String>> requestMap = null;
        try {

            // 设置通用的请求属性
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                Conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            requestMap = Conn.getRequestProperties();
            // 发送POST请求必须设置如下两行
            if (Conn.getRequestMethod().equals("POST")){
                Conn.setDoOutput(true);
                Conn.setDoInput(true);
            }
            if (Cache.uiController.httpTimeout.getText().equals("")){
                Conn.setConnectTimeout(3000);
            }else {
                try {
                    int miao = Integer.parseInt(Cache.uiController.httpTimeout.getText());
                    Conn.setConnectTimeout(miao*1000);
                }catch (Exception e){
                    Cache.uiController.logTextArea.appendText("超时时间转换异常。");
                }

            }
            Conn.setDoOutput(true);
            Conn.setUseCaches(true);
            Conn.setReadTimeout(4000);

            Conn.connect();


            sendBody(Conn,body);

            statuscode = Conn.getResponseCode();
            headermap = Conn.getHeaderFields();
            result = getResponseBodyAsString(Conn);


        }catch (Exception e){
            System.out.println(e);
        }
        Response response = new Response(statuscode,result,headermap);
        response.setRequestHeader(requestMap);
        return response;
    }
    public void sendBody(HttpURLConnection connection,Object body) throws IOException {
        if (body instanceof String){
            if (body.equals("")){
                return;
            }
            OutputStream  out = connection.getOutputStream();
            System.out.println();
            String data = (String)body;
            out.write(data.getBytes());
            out.flush();
            out.close();
        }if (body instanceof Byte || body.toString().startsWith("[B@")){
            OutputStream  out = connection.getOutputStream();
            out.write((byte[])body);
            out.flush();
            out.close();
        }if (body instanceof ByteArrayOutputStream){
            ByteArrayOutputStream bout = (ByteArrayOutputStream)body;
            OutputStream  out = connection.getOutputStream();
            nc.bs.framework.comn.NetObjectOutputStream.writeInt(out, bout.size());
            bout.writeTo(out);
            out.flush();
            out.close();
        }
        else{
            OutputStream  out = connection.getOutputStream();
            ByteArrayOutputStream baous = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baous);
            oos.writeObject(body);
            byte[] bytes = baous.toByteArray();
            out.write(bytes);
            out.flush();
            out.close();
        }
    }
    public String getResponseBodyAsString(HttpURLConnection connection) throws Exception {
        BufferedReader reader = null;
        if (connection.getResponseCode() == 200) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        StringBuffer buffer = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        return buffer.toString();
    }

    public Response Get(Object body){
        Response result =  Connect("GET",body,IsHttps);
        return result;
    }
    public Response Post(Object body ){
        try{
            Response result = Connect("POST",body,IsHttps);
            return result;
        }catch (Exception e){
            return new Response(0,"",null);
        }

    }
    public void SetHeaders(LinkedHashMap<String,String> header){
        headers = header;
    }
    public void InitHeaders(){
        headers.put("Accept","*");
        headers.put("Connection","Keep-Alive");
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
    }
    public void addHeaders(String key,String value){
        headers.put(key,value);
    }
}

class HttpsTrustManager implements X509TrustManager {

    private static TrustManager[] trustManagers = {new HttpsTrustManager()};

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }

    public static void allowAllSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
