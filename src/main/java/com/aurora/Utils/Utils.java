package com.aurora.Utils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;
public class Utils {

    public static String getRandomString(int length) {
        String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(new Random().nextInt(str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }
    public static byte[] readFile(String path) throws IOException {
        try {
            FileInputStream file = new FileInputStream(path);
            byte[] bytes = new byte[file.available()];
            file.read(bytes);
            return bytes;
        }catch (IOException e){
            System.out.println("文件读取失败");
            return null;
        }

    }

    public static String getClassCode(Class clazz) throws Exception {
        byte[] bytes = getClassBytes(clazz);
        String result = Utils.base64Encode(bytes);

        return result;
    }

    public static byte[] getClassBytes(Class clazz) throws Exception {
        String className = clazz.getName();
        String resoucePath = className.replaceAll("\\.", "/") + ".class";
        InputStream in = Utils.class.getProtectionDomain().getClassLoader().getResourceAsStream(resoucePath);
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        int len = 0;
        while((len = in.read(bytes)) != -1){
            baous.write(bytes, 0 , len);
        }

        in.close();
        baous.close();

        return baous.toByteArray();
    }

    public static String base64Encode(byte[] bytes) throws Exception{
        String result;

        try{
            Class clazz = Class.forName("java.util.Base64");
            Method method = clazz.getDeclaredMethod("getEncoder");
            Object obj = method.invoke(null);
            method = obj.getClass().getDeclaredMethod("encodeToString", byte[].class);
            obj = method.invoke(obj, bytes);
            result = (String)obj;
        }catch(ClassNotFoundException e){
            Class clazz = Class.forName("sun.misc.BASE64Encoder");
            Method method = clazz.getMethod("encodeBuffer", byte[].class);
            Object obj = method.invoke(clazz.newInstance(), bytes);
            result = (String)obj;
            result = result.replaceAll("\r|\n|\r\n", "");
        }

        return result;
    }

    public static byte[] base64Decode(String str) throws Exception{
        byte[] bytes;

        try{
            Class clazz = Class.forName("java.util.Base64");
            Method method = clazz.getDeclaredMethod("getDecoder");
            Object obj = method.invoke(null);
            method = obj.getClass().getDeclaredMethod("decode", String.class);
            obj = method.invoke(obj, str);
            bytes = (byte[]) obj;
        }catch(ClassNotFoundException e){
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            Method method = clazz.getMethod("decodeBuffer", String.class);
            Object obj = method.invoke(clazz.newInstance(), str);
            bytes = (byte[]) obj;
        }

        return bytes;
    }

    public static byte[] serialize(Object ref) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(ref);
        return out.toByteArray();
    }

    public static String getCmdFromBase(String base) throws Exception {
        int firstIndex = base.lastIndexOf("/");
        String cmd = base.substring(firstIndex + 1);

        int secondIndex = base.lastIndexOf("/", firstIndex - 1);
        if(secondIndex < 0){
            secondIndex = 0;
        }

        if(base.substring(secondIndex + 1, firstIndex).equalsIgnoreCase("base64")){
            byte[] bytes = Utils.base64Decode(cmd);
            cmd = new String(bytes);
        }

        return cmd;
    }

    public static String[] getIPAndPortFromBase(String base) throws NumberFormatException{
        int firstIndex = base.lastIndexOf("/");
        String port = base.substring(firstIndex + 1);

        int secondIndex = base.lastIndexOf("/", firstIndex - 1);
        if(secondIndex < 0){
            secondIndex = 0;
        }

        String ip = base.substring(secondIndex + 1, firstIndex);
        return new String[]{ip, Integer.parseInt(port) + ""};
    }
    public static String[] getPathAndContent(String base){

        int firstIndex = base.lastIndexOf("/");
        String Content = base.substring(firstIndex + 1);
        Content = Content.replaceAll("_]","/");
        Content = Content.replaceAll("_@_","+");
        System.out.println(Content);
        int secondIndex = base.lastIndexOf("/", firstIndex - 1);
        if(secondIndex < 0){
            secondIndex = 0;
        }

        String path = base.substring(secondIndex + 1, firstIndex);
        return new String[]{path, Content};
    }
    public static String Base64Encodebytes2Str(byte[] bytes){
        String result="";
        Class Base64er = null;
        try {
            System.out.println();
            Base64er = Class.forName("javax.xml.bind.DatatypeConverter");
            Constructor con = Base64er.getDeclaredConstructor();
            con.setAccessible(true);
            Object ObjBase64er = con.newInstance();
            Method method = ObjBase64er.getClass().getDeclaredMethod("printBase64Binary", byte[].class);
            method.setAccessible(true);
            result = (String) method.invoke(ObjBase64er,bytes);
        } catch (Exception e) {
            try {
                Base64er = Class.forName("java.util.Base64$Encoder");
                Constructor con = Base64er.getDeclaredConstructor(boolean.class,byte[].class,int.class,boolean.class);
                con.setAccessible(true);
                Object ObjBase64er = con.newInstance(true, null, -1, true);
                Method a = ObjBase64er.getClass().getMethod("encodeToString",byte[].class);
                a.setAccessible(true);
                result = (String) a.invoke(ObjBase64er,bytes);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
        return result;
    }
    public static byte[] Base64DecodeStr2bytes(String str){
        byte[] result = null;
        Class Base64er = null;
        try {
            Base64er = Class.forName("javax.xml.bind.DatatypeConverter");
            Constructor con = Base64er.getDeclaredConstructor();
            con.setAccessible(true);
            Object ObjBase64er = con.newInstance();
            Method method = ObjBase64er.getClass().getDeclaredMethod("parseBase64Binary", byte[].class);
            method.setAccessible(true);
            result =(byte[])method.invoke(ObjBase64er,str.getBytes());
            return result;
        } catch (Exception e) {
            try {
                Base64er = Class.forName("java.util.Base64$Decoder");
                Constructor con = Base64er.getDeclaredConstructor(boolean.class,boolean.class);
                con.setAccessible(true);
                Object ObjBase64er = con.newInstance(false, false);
                Method a = ObjBase64er.getClass().getMethod("decode",byte[].class);
                a.setAccessible(true);
                result =(byte[]) a.invoke(ObjBase64er,str.getBytes());
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }

        return result;
    }
    public static String[] getcommandechoArgs(String base){
        int firstIndex = base.lastIndexOf("/");
        String cmd = base.substring(firstIndex + 1);

        int secondIndex = base.lastIndexOf("/", firstIndex - 1);
        if(secondIndex < 0){
            secondIndex = 0;
        }

        String uri = base.substring(secondIndex + 1, firstIndex);
        return new String[]{uri, cmd};
    }
}
