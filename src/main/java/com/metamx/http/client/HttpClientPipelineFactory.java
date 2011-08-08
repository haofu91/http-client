/*
 * Copyright 2011 Metamarkets Group Inc.
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

package com.metamx.http.client;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;

/**
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory
{
  private static final Logger log = Logger.getLogger(HttpClientPipelineFactory.class);

  @Override
  public ChannelPipeline getPipeline() throws Exception
  {
    ChannelPipeline pipeline = new DefaultChannelPipeline();

    pipeline.addLast("codec", new HttpClientCodec());
    pipeline.addLast("inflater",
        new HttpContentDecompressor()
        {
          @Override
          public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception
          {
            log.warn("Exception in inflater, sending upstream: " + event.getCause(), event.getCause());
            context.sendUpstream(event);
          }
        }
    );

    return pipeline;
  }
}
