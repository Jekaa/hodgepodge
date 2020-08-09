package com.example.hodgepodge.nio2.async.server;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

    private final AsynchronousServerSocketChannel listener;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener) {
        this.listener = listener;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Object sessionState) {
        // accept the next connection
        Object newSessionState = new Object();
        listener.accept(newSessionState, this);

        // handle this connection
        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, inputBuffer);
        socketChannel.read(inputBuffer, sessionState, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Object sessionState) {
        // Handle connection failure...
        log.warn(exc.getMessage());
    }
}
