package com.aurora.Templates;

import com.aurora.Utils.Cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class myClassTemplate implements Template{
    private String className;
    private byte[] bytes;
    public myClassTemplate(String className){

        this.className = className;
        generate();
    }
    public void cache(){
        Cache.set(className, bytes);
    }
    public String getClassName(){
        return className;
    }
    public byte[] getBytes(){
        return bytes;
    }
    public void generate() {

        try{

            String path = System.getProperty("user.dir") + File.separator + "lib" + File.separator + className+".class";

            FileInputStream fis = new FileInputStream(path);
            byte[] buf = new byte[fis.available()];
            System.out.println("[*]In Your path:"+path);
            System.out.println("[* File Size :"+String.valueOf(fis.available()) );
            fis.read(buf);
            fis.close();

            bytes = buf;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void fileOutputStreamMethod(String filepath, byte[] content) throws IOException {
        try  {
            FileOutputStream fileOutputStream = new FileOutputStream(filepath);
            byte[] bytes = content;
            fileOutputStream.write(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
