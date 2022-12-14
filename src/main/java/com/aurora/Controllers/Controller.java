package com.aurora.Controllers;

import com.aurora.Utils.Cache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Controller {
    public static Boolean FileOrLogArea = false;
    public static void WriteLog(String text){
        if (!text.startsWith("\n[")){
            text = text.replace("\n","");
            text ="\n[*]" +text;
        }

        if (Cache.uiController.targetAddress.getText().startsWith("file:")){
            System.out.println(Thread.currentThread().getId());
            String id = String.valueOf(Thread.currentThread().getId()) ;
            String path = Cache.ThreadIdForLog.get(id);
            WriteLog2File(path,text);
        }else {
            Cache.uiController.logTextArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 20px;");
            Cache.uiController.logTextArea.appendText(text);
        }
    }
    public static void WriteExpLog(String text){
        if (!text.startsWith("\n[")){
            text = text.replace("\n","");
            text ="\n[*]" +text;
        }

        Cache.uiController.PublicArea.appendText(text);
    }
    public static void WriteLog2File(String filePath,String Content) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            filePath = java.net.URLDecoder.decode(filePath,"UTF-8");
            if (System.getProperties().getProperty("os.name").toLowerCase().contains("mac os")){
                filePath = "/" + filePath;
            }
            File f=new File(filePath);
            if (!f.exists()){
                f.createNewFile();
            }
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(Content);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteSuccessLog(String text){
        if (text.startsWith("\n[*]")||text.startsWith("\n[+]")){
            text = text.substring(4);
            text = "\n[+]"+ text;
            Cache.uiController.logTextArea.appendText(text);
        }else {
            text = "\n[+]"+ text;
            Cache.uiController.logTextArea.appendText(text);
        }
    }
    public static void WriteFailLog(String text){
        if (text.startsWith("\n[*]")||text.startsWith("\n[-]")){
            text = text.substring(4);
            text = "\n[-]"+ text;
            Cache.uiController.logTextArea.appendText(text);
        }else {
            text = "\n[-]"+ text;
            Cache.uiController.logTextArea.appendText(text);
        }
    }
}
