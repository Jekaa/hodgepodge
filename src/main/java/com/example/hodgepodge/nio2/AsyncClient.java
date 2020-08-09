package com.example.hodgepodge.nio2;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
public class AsyncClient {

    private static final String RQ = "what time is it now?";
    
    private AsynchronousSocketChannel client;
    private Future<Void> future;

    public AsyncClient() {
        try {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(
                    Executors.newCachedThreadPool(new NamedThreadFactory("async_client_channels")));
            client = AsynchronousSocketChannel.open(group);
            String host = InetAddress.getLocalHost().getHostAddress();
            int port = 8090;
            future = client.connect(new InetSocketAddress(host, port));
            log.info("Client connect to " + host + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }

    public void doRequest() {
        try {
            future.get();
            ByteBuffer buffer = ByteBuffer.wrap(RQ.getBytes());
            Future<Integer> requestFuture = client.write(buffer);
            log.info("Send to server: " + RQ);
            requestFuture.get();
            buffer.flip();
            Future<Integer> responseFuture = client.read(buffer);
            log.info("Received from server: " + new String(buffer.array()).trim());
            responseFuture.get();
            buffer.clear();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}

