/*
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skywalking.apm.plugin.thrift.tprotocol.client;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;

import java.lang.reflect.Method;
import java.util.Map;

import static org.apache.skywalking.apm.plugin.thrift.utils.Tools.stringToByteBuffer;


public class TProtocolWriteFieldStopInterceptor implements InstanceMethodsAroundInterceptor {
    
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInterceptResult result) throws Throwable {
        if (objInst instanceof TProtocol) {
            TProtocol oprot = (TProtocol) objInst;
            try {
                appendParentTraceInfo(oprot);
            } catch (Throwable t) {
                
            }
        }
    }
    
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret)
        throws Throwable {
        return ret;
    }
    
    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Throwable t) {
        
    }
    
    private void appendParentTraceInfo(TProtocol oprot) throws TException {
        // todo 通过现有的attachement 看是否可以动态字段获取过来
        // ThriftRequestProperty parentTraceInfo = new ThriftRequestProperty();
        // if (parentTraceInfo == null) {
        // return;
        // }
        //
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_TRACE_ID, oprot);
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_SPAN_ID, oprot);
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_PARENT_SPAN_ID, oprot);
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_FLAGS, oprot);
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_PARENT_APPLICATION_NAME, oprot);
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_PARENT_APPLICATION_TYPE, oprot);
        // parentTraceInfo.writeTraceHeader(ThriftHeader.THRIFT_HOST, oprot);
        Map<String, String> attachment = null;
        if (oprot instanceof EnhancedInstance) {
            attachment = (Map<String, String>) ((EnhancedInstance) oprot).getSkyWalkingDynamicField();
        }
        if (attachment == null) {
            return;
        }
        short i=0;
        
        for(Map.Entry<String,String> entry:attachment.entrySet()){
            TField traceField = new TField(entry.getKey(), TType.STRING, (short) (Short.MIN_VALUE+(i++)));
            oprot.writeFieldBegin(traceField);
            try {
                    // these will be read as byte buffer although it's probably safe to just use writeString here.
                    // see org.apache.thrift.protocol.TProtocolUtil.skip(TProtocol, byte, int)
                    oprot.writeBinary(stringToByteBuffer((String)entry.getValue()));}
                    finally {
                oprot.writeFieldEnd();
            }
        }
    }
}
