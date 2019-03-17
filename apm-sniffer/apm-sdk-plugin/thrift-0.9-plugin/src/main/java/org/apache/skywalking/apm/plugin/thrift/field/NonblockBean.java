package org.apache.skywalking.apm.plugin.thrift.field;

import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by pengfeining on 2019/3/14 0014.
 */
public class NonblockBean {
    
    private Socket socket;
    
    private SocketAddress socketAddress;
    
    public Socket getSocket() {
        return socket;
    }
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
    
    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
}
