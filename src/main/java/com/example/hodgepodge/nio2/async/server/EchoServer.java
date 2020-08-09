package com.example.hodgepodge.nio2.async.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class EchoServer implements Runnable {

    public EchoServer() {
        log.info("Async server started");
        this.run();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(newFixedThreadPool);
            final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open(group);
            String host = InetAddress.getLocalHost().getHostAddress();
            int port = 8090;
            InetSocketAddress address = new InetSocketAddress(host, port);
            listener.bind(address);
            log.info("Server bind: " + host + ":" + port);
            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener);
            Object state = new Object();
            listener.accept(state, acceptCompletionHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
