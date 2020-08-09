package com.example.hodgepodge.nio2;

import org.junit.jupiter.api.Test;

public class AsyncClientTest {

    @Test
    void testClient() {
        AsyncClient client = new AsyncClient();
        client.doRequest();
    }
}
