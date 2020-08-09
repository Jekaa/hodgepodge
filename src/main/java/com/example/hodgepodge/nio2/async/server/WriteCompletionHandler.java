package com.example.hodgepodge.nio2.async.server;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
public class WriteCompletionHandler implements CompletionHandler<Integer, Object> {

    private final AsynchronousSocketChannel socketChannel;

    public WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, Object sessionState) {
        try {
            log.info("try close socketChannel");
            socketChannel.close();
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            log.error(ex.getMessage());
        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        // Handle write failure...
        log.warn(exc.getMessage());
    }
}
