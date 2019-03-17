package org.apache.skywalking.apm.plugin.thrift.transport;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.plugin.thrift.field.NonblockBean;
import org.apache.thrift.transport.TNonblockingSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by pengfeining on 2019/3/13 0013.
 */
// TNonblockingSocket(SocketChannel, int, SocketAddress)
// 问题在于对于两个字段的取出的问题，pinpoint 单独字段不存在要明确数据如何变动
// 注意这里是两个字段nonblockBean socket SocketAddress 取出字段的时候需要判断是否为NonBlockBean 还是单独的Socket 还是SocketAdr

public class TNonblockingSocketConstructInterceptor implements InstanceConstructorInterceptor {
    
    Logger logger = LoggerFactory.getLogger(TNonblockingSocketConstructInterceptor.class);
    
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        if (validate(objInst, allArguments)) {
            NonblockBean nonblockBean = new NonblockBean();
            Socket socket = ((TNonblockingSocket) objInst).getSocketChannel().socket();
            nonblockBean.setSocket(socket);
            if (allArguments[2] instanceof SocketAddress) {// todo 目前测试的阶段 SocketAddress 都为空 暂时传为空
                SocketAddress socketAddress = (SocketAddress) allArguments[2];
                nonblockBean.setSocketAddress(socketAddress);
            }
            if (nonblockBean.getSocketAddress() == null) {
                objInst.setSkyWalkingDynamicField(nonblockBean.getSocket());
            }
            // objInst.setSkyWalkingDynamicField(nonblockBean);
        }
    }
    
    private boolean validate(Object target, Object[] args) {
        if (!(target instanceof TNonblockingSocket)) {
            return false;
        }
        if (args.length != 3) {
            return false;
        }
        if (!(target instanceof EnhancedInstance)) {
            logger.debug("Invalid target object. Need field enhance.");
            return false;
        }
        return true;
    }
}
