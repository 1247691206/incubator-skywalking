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

import org.apache.thrift.transport.TTransport;

/**
 * @author HyunGil Jeong
 */
// wrapper TTransports
// TFramedTransport(TTransport), TFramedTransport(TTransport, int)
public class TFramedTransportConstructInterceptor extends WrappedTTransportConstructInterceptor {

    @Override
    protected TTransport getWrappedTransport(Object[] args) {
        TTransport wrappedTransport = null;
        if ((args.length == 1 || args.length == 2) && (args[0] instanceof TTransport)) {
            wrappedTransport = (TTransport)args[0];
        }
        return wrappedTransport;
    }
}
