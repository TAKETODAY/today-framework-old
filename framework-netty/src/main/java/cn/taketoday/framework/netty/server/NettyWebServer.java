/**
 * Original Author -> 杨海健 (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2019 All Rights Reserved.
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
package cn.taketoday.framework.netty.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.PreDestroy;

import cn.taketoday.context.annotation.Autowired;
import cn.taketoday.context.annotation.Singleton;
import cn.taketoday.context.env.Environment;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TODAY <br>
 *         2019-07-02 21:15
 */
@Slf4j
@Setter
@Getter
@Singleton
public class NettyWebServer {

    @Autowired
    private Environment environment;

    private EventLoopGroup childGroup;
    private EventLoopGroup parentGroup;

    private EventLoop scheduleEventLoop;

    private Channel channel;

    private Class<? extends ServerSocketChannel> socketChannel; // NioServerSocketChannel

    public static boolean epollIsAvailable() {
        try {
            Object obj = Class.forName("io.netty.channel.epoll.Epoll").getMethod("isAvailable").invoke(null);
            return null != obj && Boolean.valueOf(obj.toString()) && System.getProperty("os.name").toLowerCase().contains("linux");
        }
        catch (Exception e) {
            return false;
        }
    }

    public void start() {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        final ServerBootstrap bootstrap = new ServerBootstrap();

        int acceptThreadCount = 2;
        int threadCount = 2;

        // enable epoll
        if (epollIsAvailable()) {

            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);

            socketChannel = EpollServerSocketChannel.class;

            this.parentGroup = new EpollEventLoopGroup(threadCount, new NamedThreadFactory("epoll-parent@"));
            this.childGroup = new EpollEventLoopGroup(acceptThreadCount, new NamedThreadFactory("epoll-child@"));
        }
        else {

            this.parentGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("boss@"));
            this.childGroup = new NioEventLoopGroup(threadCount, new NamedThreadFactory("worker@"));

            socketChannel = NioServerSocketChannel.class;
        }

        bootstrap.group(getParentGroup(), getChildGroup()).channel(getSocketChannel());

        scheduleEventLoop = new DefaultEventLoop();

        bootstrap.childHandler(new NettyServerInitializer());

        String address = environment.getProperty("server.host", "localhost");
        int port = environment.getProperty("server.port", int.class, 8080);

        try {
            channel = bootstrap.bind(address, port).sync().channel();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @PreDestroy
    public void stop() {

        log.info("shutdown");

        if (this.parentGroup != null) {
            this.parentGroup.shutdownGracefully();
        }
        if (this.childGroup != null) {
            this.childGroup.shutdownGracefully();
        }
    }

    public class NamedThreadFactory implements ThreadFactory {

        private final String prefix;
        private final LongAdder threadNumber = new LongAdder();

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            threadNumber.add(1);
            return new Thread(runnable, prefix + "thread-" + threadNumber.intValue());
        }
    }
}