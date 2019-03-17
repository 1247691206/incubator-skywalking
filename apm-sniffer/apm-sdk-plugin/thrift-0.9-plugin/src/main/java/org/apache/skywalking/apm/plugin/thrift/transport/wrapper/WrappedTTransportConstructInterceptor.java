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

package org.apache.skywalking.apm.plugin.thrift.transport.wrapper;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.thrift.transport.TTransport;

import java.net.Socket;


/**
 * @author HyunGil Jeong
 */
public abstract class WrappedTTransportConstructInterceptor implements InstanceConstructorInterceptor {
    
    protected abstract TTransport getWrappedTransport(Object[] args);
    
    private boolean validateTransport(Object transport) {
        if (transport instanceof TTransport) {
            return true;
        }
        return false;
    }


    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        if (validateTransport(objInst)) {
            TTransport wrappedTransport = getWrappedTransport(allArguments);
            if (validateTransport(wrappedTransport)) {
                Socket socket = (Socket) ((EnhancedInstance)wrappedTransport).getSkyWalkingDynamicField();
                objInst.setSkyWalkingDynamicField(socket);
            }
        }
    }
}
