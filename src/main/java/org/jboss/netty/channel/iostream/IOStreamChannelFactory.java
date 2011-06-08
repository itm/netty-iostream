/*
 * Copyright 2011 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.channel.iostream;


import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.util.internal.ExecutorUtil;

import java.util.concurrent.ExecutorService;

/**
 * A {@link org.jboss.netty.channel.ChannelFactory} for creating {@link IOStreamChannel} instances.
 *
 * @author Daniel Bimschas
 * @author Dennis Pfisterer
 */
public class IOStreamChannelFactory implements ChannelFactory {

    private final ChannelGroup channels = new DefaultChannelGroup("IOStreamChannelFactory-ChannelGroup");

    private final ExecutorService executorService;

    public IOStreamChannelFactory(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Channel newChannel(final ChannelPipeline pipeline) {
        IOStreamChannelSink sink = new IOStreamChannelSink(executorService);
        IOStreamChannel channel = new IOStreamChannel(this, pipeline, sink);
        sink.setChannel(channel);
        channels.add(channel);
        return channel;
    }

    @Override
    public void releaseExternalResources() {
        ChannelGroupFuture close = channels.close();
        close.awaitUninterruptibly();
        ExecutorUtil.terminate(executorService);
    }
}
