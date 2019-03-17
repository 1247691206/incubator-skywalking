package org.apache.skywalking.apm.plugin.thrift.client;

import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;
import org.apache.skywalking.apm.plugin.thrift.utils.HostAndPort;
import org.apache.skywalking.apm.plugin.thrift.utils.StringUtils;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftConstants;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftUtils;
import org.apache.thrift.TBase;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


// TServiceClient.sendBase(String, TBase)
public class TServiceClientSendBaseInterceptor implements InstanceMethodsAroundInterceptor {
    
    // send Base
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInterceptResult result) throws Throwable {
        // 从 发起请求方获取信息写入span 中
        if (objInst instanceof TServiceClient) {
            TServiceClient client = (TServiceClient) objInst;
            TProtocol oprot = client.getOutputProtocol();
            TTransport transport = oprot.getTransport();
            // 获取字段
            // Map<String, Object> fields_client = Tools.getObjectFields(objInst);
            // Map<String, Object> fields_oprot = Tools.getObjectFields(oprot);
            // Map<String, Object> fields_trans = Tools.getObjectFields(transport);
            String remoteAddress = ThriftConstants.UNKNOWN_ADDRESS;
            
            if (transport instanceof THttpClient) {
                // THRIFT_CLIENT_INTERNAL
                remoteAddress = getRemoteAddressForTHttpClient((THttpClient) transport);
            } else {
                // THRIFT_CLIENT
                remoteAddress = getRemoteAddress(transport);
            }
            
            String methodName = ThriftConstants.UNKNOWN_METHOD_NAME;
            if (allArguments[0] instanceof String) {
                methodName = (String) allArguments[0];
            }
            String serviceName = ThriftUtils.getClientServiceName(client);
            String thriftUrl = getServiceUrl(remoteAddress, serviceName, methodName);
            String methodArgs = getMethodArgs((TBase<?, ?>) allArguments[1]);
            final ContextCarrier contextCarrier = new ContextCarrier();
            AbstractSpan span = ContextManager.createExitSpan(methodArgs, contextCarrier, remoteAddress);
            span.setComponent(ComponentsDefine.THRIFT);
            Tags.URL.set(span, thriftUrl);
            SpanLayer.asRPCFramework(span);
            CarrierItem next = contextCarrier.items();
            Map<String, String> attachment = new HashMap<String, String>();
            while (next.hasNext()) {
                next = next.next();
                // 采用string类型存储
                // these will be read as byte buffer although it's probably safe to just use writeString here.
                // see org.apache.thrift.protocol.TProtocolUtil.skip(TProtocol, byte, int)
                attachment.put(next.getHeadKey(), next.getHeadValue());
            }
            
            // 直接在此处添加字段 存在问题 故添加到子transport中进行传输 通过动态字段进行传输
            // TField RTRACE_ATTACHMENT = new TField("rtraceAttachment", TType.MAP, (short) 0);
            
            // oprot.writeFieldBegin(RTRACE_ATTACHMENT);
            // {
            // oprot.writeMapBegin(new TMap(TType.STRING, TType.STRING, attachment.size()));
            // for (Map.Entry<String, String> entry : attachment.entrySet()) {
            // oprot.writeString(entry.getKey());
            // oprot.writeString(entry.getValue());
            // }
            // oprot.writeMapEnd();
            // }
            // oprot.writeFieldEnd();
            ((EnhancedInstance) oprot).setSkyWalkingDynamicField(attachment);
            
        }
        System.out.println("sendBase消息成功");
        
    }
    
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret)
        throws Throwable {
        // 由于返回值为空
        ContextManager.stopSpan();
        return ret;
    }
    
    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Throwable t) {
        dealException(t);
    }
    
    private String getRemoteAddressForTHttpClient(THttpClient tHttpClient) {
        if (tHttpClient instanceof EnhancedInstance) {
            URL url = (URL) ((EnhancedInstance) tHttpClient).getSkyWalkingDynamicField();
            if (url == null) {
                return ThriftConstants.UNKNOWN_ADDRESS;
            }
            return HostAndPort.toHostAndPortString(url.getHost(), url.getPort());
        }
        return ThriftConstants.UNKNOWN_ADDRESS;
    }
    
    // skywalking 动态字段中获取
    private String getRemoteAddress(TTransport transport) {
        if (transport instanceof EnhancedInstance) {
            
            Socket socket = (Socket) ((EnhancedInstance) transport).getSkyWalkingDynamicField();
            if (socket == null) {
                return ThriftConstants.UNKNOWN_ADDRESS;
            }
            return ThriftUtils.getHostPort(socket.getRemoteSocketAddress());
        } else {
        }
        return ThriftConstants.UNKNOWN_ADDRESS;
    }
    
    private String getServiceUrl(String url, String serviceName, String methodName) {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("/").append(serviceName).append("/").append(methodName);
        return sb.toString();
    }
    
    private String getMethodArgs(TBase<?, ?> args) {
        return StringUtils.abbreviate(args.toString(), 256);
    }
    
    /**
     * Log the throwable, which occurs in thrift RPC service.
     */
    private void dealException(Throwable throwable) {
        AbstractSpan span = ContextManager.activeSpan();
        span.errorOccurred();
        span.log(throwable);
    }
}
