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

package org.apache.skywalking.apm.plugin.thrift.server.nonblocking;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.plugin.thrift.utils.ThriftConstants;
import org.apache.skywalking.apm.plugin.thrift.utils.Tools;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransport;

import java.net.Socket;
import java.util.Map;


// target.addField(ThriftConstants.FIELD_ACCESSOR_SOCKET);
// target.addGetter(ThriftConstants.FIELD_GETTER_T_NON_BLOCKING_TRANSPORT, ThriftConstants.FRAME_BUFFER_FIELD_TRANS_);
//AbstractNonblockingServer$FrameBuffer(TNonblockingTransport, SelectionKey, AbstractSelectThread)
public class FrameBufferConstructInterceptor implements InstanceConstructorInterceptor {


    public boolean validate(Object target, Object[] args, Object result) {
        if (!(target instanceof EnhancedInstance)) {
            return false;
        }
        return true;
    }
    

    protected TTransport getInjectionTarget(Object target, Object[] args, Object result) {
        return (TTransport) ((EnhancedInstance) target).getSkyWalkingDynamicField();
    }

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        if (validate0(objInst, allArguments, null)) {
            Socket rootSocket = getRootSocket(objInst);
            if (rootSocket != null) {
                TTransport injectionTarget = getInjectionTarget(objInst, allArguments, null);
                injectSocket(injectionTarget, rootSocket);
            }
        }
    }

    private boolean validate0(Object target, Object[] args, Object result) {
        if (target instanceof EnhancedInstance) {
            return validate(target, args, result);
        }
        return false;
    }


    // Retrieve the socket information from the trans_ field of the given instance.
    protected final Socket getRootSocket(Object target) {
        Map<String, Object> fields = Tools.getObjectFields(target);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            // inTrans 找到字段值 提取socket信息
            Object inTransObject = ThriftConstants.FRAME_BUFFER_FIELD_IN_TRANS_.equalsIgnoreCase(entry.getKey());
            if (inTransObject != null && inTransObject instanceof TNonblockingTransport) {
                TNonblockingTransport inTrans = (TNonblockingTransport) inTransObject;
                if (inTrans instanceof EnhancedInstance) {
                    return (Socket) ((EnhancedInstance) inTrans).getSkyWalkingDynamicField();
                }
            }
        }
        return null;
    }

    // Inject the socket information into the given memory-based transport
    protected final void injectSocket(TTransport inTrans, Socket rootSocket) {
        if (inTrans instanceof EnhancedInstance) {
            ((EnhancedInstance) inTrans).setSkyWalkingDynamicField(rootSocket);
        }

    }
}
