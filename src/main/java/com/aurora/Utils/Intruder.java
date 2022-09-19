package com.aurora.Utils;

import java.net.MalformedURLException;

public class Intruder extends Thread{
    public String method;
    public String target;
    public String args = "";

    public void setMethod(String method) {
        this.method = method;
    }
    public void setTarget(String target){
        this.target = target;
    }
    public void setArgs(String args){
        this.args = args;
    }
    @Override
    public void run()  {
        if (method.equals("GET")){
            HttpRequest request = null;
            try {
                request = new HttpRequest(target);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Response result = request.Get(args);
            if (result.statusCode==200){
                Cache.uiController.logTextArea.appendText("\n[*] 存在 "+ target);
            }
        }if (method.equals("POST")){

        }
    }
}
