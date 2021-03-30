/**
 * Original Author -> 杨海健 (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2021 All Rights Reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/]
 */
package cn.taketoday.framework.reactive;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import cn.taketoday.context.annotation.Import;
import cn.taketoday.context.annotation.MissingBean;
import cn.taketoday.context.factory.BeanDefinition;
import cn.taketoday.context.factory.BeanDefinitionRegistry;
import cn.taketoday.context.loader.AnnotationBeanDefinitionRegistrar;
import cn.taketoday.framework.reactive.server.NettyServerInitializer;
import cn.taketoday.framework.reactive.server.NettyWebServer;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author TODAY <br>
 * 2019-11-22 00:30
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Import(NettyConfig.class)
public @interface EnableDefaultNetty {

  boolean async() default true;
}

class NettyConfig extends AnnotationBeanDefinitionRegistrar<EnableDefaultNetty> {

  @MissingBean(type = ReactiveChannelHandler.class)
  @Import({ NettyWebServer.class, NettyServerInitializer.class })
  ReactiveChannelHandler reactiveChannelHandler(NettyDispatcher nettyDispatcher, NettyRequestContextConfig contextConfig) {
    return new ReactiveChannelHandler(nettyDispatcher, contextConfig);
  }

  @MissingBean
  NettyRequestContextConfig nettyRequestContextConfig() {
    return new NettyRequestContextConfig();
  }

  @Override
  public void registerBeanDefinitions(EnableDefaultNetty target, BeanDefinition annotatedMetadata, BeanDefinitionRegistry registry) {
    if (!registry.containsBeanDefinition(NettyDispatcher.class)) {
      final boolean async = target.async();
      if (async) {
        registry.registerBean(AsyncNettyDispatcherHandler.class);
      }
      else {
        registry.registerBean(SyncNettyDispatcherHandler.class);
      }
    }
  }
}
