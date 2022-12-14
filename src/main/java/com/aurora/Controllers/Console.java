package com.aurora.Controllers;

import com.aurora.Exceptions.NullMethodArgsException;
import com.aurora.SupportType.Poc_Exp;
import com.aurora.Utils.Cache;
import com.aurora.Utils.Utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Console extends Thread {

    public static String MethodName ;
    public static String Product;
    public Object[] args;
    @Override
    public void run()  {
        try {

            Method runMethod = this.getClass().getMethod(MethodName,String.class);
            runMethod.invoke(this,Product);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void setMethodName(String methodName){
        MethodName = methodName;
    }
    public void setProduct(String product){
        Product = product;
    }
    public void setArgs(Object... objects){
        args = objects;
    }

    public  void GoPoc(String product) throws InvocationTargetException, IllegalAccessException, NullMethodArgsException, MalformedURLException {
        BasicController controller = Cache.routes.get(product);
        String target;
        if (this.args!=null){
            target =(String) args[0];
            String id =String.valueOf(Thread.currentThread().getId()) ;

            String log_name = Cache.uiController.LogDirPath+ File.separator+ new URL(target).getHost().toString()+"_"+ Utils.getRandomString(4) +"_log.log";
            Cache.ThreadIdForLog.put(id,log_name);
            System.out.println("//->>开始扫描 "+target);

        }else {
            target = Cache.uiController.targetAddress.getText();
            Cache.uiController.logTextArea.setText("");
            Cache.uiController.logTextArea.appendText("//->>开始扫描 "+ Cache.uiController.SupportType.getValue()+"的 所有POC\n");
        }


        String VulName = Cache.uiController.SupportVul.getValue();
        String Vultype = Cache.uiController.SupportType.getValue();
        if(Vultype.equals("Spring")){
            if (target.endsWith("/")){
                target = target.substring(0,target.lastIndexOf("/"));
            }
        }else {
            target = getUrl(target);
        }
        if (VulName.equals("All")){
            List<Method> methodList = Cache.getVulRoutesValue(product);
            for (Method method: methodList){
                try {
                    System.out.println(method.getName());
                    method.invoke(controller, Poc_Exp.POC,target,args);
                }catch (Exception e){
                    System.out.println("invoke methos:"+method.getName()+"时出现错误");
                    // Controller.WriteLog("\n[*] invoke method:"+method.getName()+"时出现错误");
                }

            }
        }else {
            List<Method> methodList = Cache.getVulRoutesValue(product);
            for (Method method: methodList){
                if (method.getName().equals(VulName)){
                    try {
                        Cache.uiController.logTextArea.appendText("\n[*]开始检测 "+method.getName()+":");
                        method.invoke(controller, Poc_Exp.POC,target,args);
                        break;
                    }catch (Exception e){
                        System.out.println("invoke methos:"+method.getName()+"时出现错误");
                        Controller.WriteLog("\n[*] invoke method:"+method.getName()+"时出现错误");
                    }
                }
            }
        }

    }

    public  void GoExp(String product) throws MalformedURLException {
        Cache.uiController.PublicArea.setText("");
        Cache.uiController.PublicArea.appendText("[*]开始:\n");
        BasicController controller = Cache.routes.get(Product);
        String target = Cache.uiController.targetAddress.getText();
        target = getUrl(target);
        String VulName = Cache.uiController.SupportVul.getValue();
        if (VulName.equals("All")){
            Controller.WriteLog("\n[*] 指定利用漏洞");
            return;
        }
        List<Method> methodList = Cache.getVulRoutesValue(product);
        for (Method method: methodList){
            if (method.getName().equals(VulName)){
                try {
                    Cache.uiController.logTextArea.appendText("\n[*]开始检测 "+method.getName()+":");
                    method.invoke(controller, Poc_Exp.EXP,target,args);
                    break;
                }catch (Exception e){
                    System.out.println("[*]invoke methos:"+method.getName()+"时出现错误");
                    Controller.WriteLog("\n[*] invoke method:"+method.getName()+"时出现错误");
                }
            }
        }
    }


    public static String getUrl(String target) throws MalformedURLException {
        URL url = new URL(target);
        String result = "";
        String port = "";
        result = url.getProtocol()+"://"+url.getHost();
        if (url.getPort()<=-1){

        }else {
            result +=":"+url.getPort();
        }
        return result;
    }
    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://127.0.0.1/index");
        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());
    }
}
