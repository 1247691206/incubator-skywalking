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

package org.apache.skywalking.apm.plugin.thrift.tprotocol.server;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.thrift.protocol.TField;

import java.lang.reflect.Method;


public class TProtocolReadFieldBeginInterceptor implements InstanceMethodsAroundInterceptor {
    
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInterceptResult result) throws Throwable {
        System.out.println(result);
    }
    
    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret)
        throws Throwable {
        if (!validate(objInst)) {
            return ret;
        }
        Boolean shouldTrace = (Boolean) ((EnhancedInstance) objInst).getSkyWalkingDynamicField();
        if (shouldTrace != null && shouldTrace) {
            if (ret instanceof TField) {
                handleClientRequest((TField) ret);
            }
        } else if (shouldTrace == null) {
            handleClientRequest((TField) ret);
        } else {
            
        }
        return ret;
    }
    
    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Throwable t) {
        
    }
    
    private boolean validate(Object target) {
        // 获取是否是server端
        if (!(target instanceof EnhancedInstance)) {
            return false;
        }
        return true;
    }
    
    private void handleClientRequest(TField field) {
        //
        System.out.println(field);
    }
    
}
