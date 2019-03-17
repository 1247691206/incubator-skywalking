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

package org.apache.skywalking.apm.plugin.thrift.client.async;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.thrift.transport.TNonblockingTransport;

import java.net.SocketAddress;


/**
 * @author HyunGil Jeong
 */
public class TAsyncMethodCallConstructInterceptor implements InstanceConstructorInterceptor {
    
    // TAsyncMethodCall(TAsyncClient, TProtocolFactory, TNonblockingTransport, AsyncMethodCallback<T>, boolean)
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        if (validate(objInst)) {
            TNonblockingTransport transport = (TNonblockingTransport) objInst.getSkyWalkingDynamicField();
            if (validateTransport(transport)) {
                SocketAddress socketAddress = (SocketAddress) ((EnhancedInstance) transport).getSkyWalkingDynamicField();
                objInst.setSkyWalkingDynamicField(socketAddress);
            }
        }
    }
    
    private boolean validate(Object target) {
        if (!(target instanceof EnhancedInstance)) {
            return false;
        }
        return true;
    }
    
    private boolean validateTransport(Object nonblockingTransportObj) {
        if (!(nonblockingTransportObj instanceof EnhancedInstance)) {
            return false;
        }
        return true;
    }
}
