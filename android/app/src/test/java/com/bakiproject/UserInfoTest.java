package com.bakiproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class UserInfoTest {
    @Test
    void testSerialise() throws IOException {
        UserInfo info = new UserInfo("asd", null, "127.0.0.14");

        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);


        ObjectOutputStream oos = new ObjectOutputStream(out);


    }
}