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

package org.apache.skywalking.apm.plugin.thrift.transport;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;


/**
 * @author HyunGil Jeong
 */
// injector TTranports
// TSocket(Socket), TSocket(String, int, int)
public class TSocketConstructInterceptor implements InstanceConstructorInterceptor {
    
    Logger logger = LoggerFactory.getLogger(TSocketConstructInterceptor.class);
    
    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        if (validate(objInst)) {
            Socket socket = ((TSocket) objInst).getSocket();
            objInst.setSkyWalkingDynamicField(socket);
        }
    }
    
    private boolean validate(Object target) {
        if (!(target instanceof TSocket)) {
            return false;
        }
        if (!(target instanceof EnhancedInstance)) {
            logger.debug("Invalid target object. Need field enhance.");
            return false;
        }
        return true;
    }
}
