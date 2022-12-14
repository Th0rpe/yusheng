package com.aurora.Utils;

import com.aurora.Controllers.BasicController;
import com.aurora.Controllers.BasicMapping;
import com.aurora.Controllers.VulnerabilityDescriptionMapping;
import com.aurora.SupportType.SupportVul;
import com.aurora.Templates.*;
import com.aurora.ui.UIController;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class Cache {
    public static TreeMap<String, BasicController> routes = new TreeMap<String, BasicController>(); // 路由map, 存储key+路由
    public static TreeMap<String, List<Method>> VulRoutes = new TreeMap<String, List<Method>>(); // 漏洞产品对用漏洞集合
    public static List SupprotType = new ArrayList(); // 支持的产品 用于前端展示
    public static HashMap<String,HashMap<String, SupportVul>> VulDescriptions = new HashMap<String, HashMap<String, SupportVul>>(); // 用于存储漏洞名称+map(漏洞描述，漏洞类型)
    public static UIController uiController;
    public static HashMap<String,String> ThreadIdForLog = new HashMap<>();


    private static ExpiringMap<String, byte[]> map = ExpiringMap.builder()
            .maxSize(1000)
            .expiration(30, TimeUnit.SECONDS)
            .variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    static{
        try {
            //过期时间100年，永不过期的简单方法
            map.put("TomcatEchoTemplate", Utils.getClassBytes(TomcatEchoTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("TomcatMemshellTemplate1", Utils.getClassBytes(TomcatMemshellTemplate1.class), 365 * 100, TimeUnit.DAYS);
            map.put("TomcatMemshellTemplate2", Utils.getClassBytes(TomcatMemshellTemplate2.class), 365 * 100, TimeUnit.DAYS);
            map.put("JettyMemshellTemplate", Utils.getClassBytes(JettyMemshellTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("NettyMemshellTemplate", Utils.getClassBytes(NettyMemshellTemplate.class), 365 * 100, TimeUnit.DAYS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InitSupportType(UIController obj) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        uiController = obj;
        Set<Class<?>> controllers = new Reflections(BasicController.class.getPackage().getName())
                .getTypesAnnotatedWith(BasicMapping.class);
        for(Class<?> controller : controllers) {
            Constructor<?> cons = controller.getConstructor();
            BasicController instance = (BasicController) cons.newInstance();
            String mapping = controller.getAnnotation(BasicMapping.class).uri();
            routes.put(mapping, instance);
            System.out.println(mapping);
            SupprotType.add(mapping); //# 加入类对应的产品名称
            Set<Method> methods = new Reflections(controller.getPackage().getName()+"."+controller.getSimpleName(),new MethodAnnotationsScanner()).getMethodsAnnotatedWith(VulnerabilityDescriptionMapping.class);

            List list = new ArrayList();
            for (Method method : methods){
                System.out.println(method.getClass());
                if (method.getName().startsWith("vul_")){
                    list.add(method);
                    HashMap<String, SupportVul> hashMap =new HashMap();
                    hashMap.put(method.getAnnotation(VulnerabilityDescriptionMapping.class).Description(),
                            method.getAnnotation(VulnerabilityDescriptionMapping.class).SupportVulType());
                    VulDescriptions.put(method.getName(),hashMap);
                }
            }
            VulRoutes.put(mapping,list);
        }
    }
    public static List getVulRoutesValue(String key){
        return VulRoutes.get(key);
    }
    public static HashMap<String, SupportVul> getVulDescriptions(String key){

        return  VulDescriptions.get(key);
    }
    public static void set(String key,byte[] value){
        map.put(key,value);
    }
    public static byte[] get(String key){
        return map.get(key);
    }
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {


    }
}
