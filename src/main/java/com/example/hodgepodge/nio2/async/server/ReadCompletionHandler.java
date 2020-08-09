package com.example.hodgepodge.nio2.async.server;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ReadCompletionHandler implements CompletionHandler<Integer, Object> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    public ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Object sessionState) {

        byte[] buffer = new byte[bytesRead];
        inputBuffer.rewind();

        inputBuffer.get(buffer);
        String message = new String(buffer);

        log.info("Received message from client : " + message);

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
        ByteBuffer outputBuffer = ByteBuffer.wrap(dateFormat.format(new Date()).getBytes());
        socketChannel.write(outputBuffer, sessionState, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        // Handle read failure...
        log.warn(exc.getMessage());
    }
}
