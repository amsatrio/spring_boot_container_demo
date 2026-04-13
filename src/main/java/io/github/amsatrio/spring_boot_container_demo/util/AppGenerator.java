package io.github.amsatrio.spring_boot_container_demo.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

public class AppGenerator {
    public static UUID generateUUIDv7() {
        final SecureRandom random = new SecureRandom();

        byte[] value = new byte[16];
        random.nextBytes(value);
        
        long timestamp = System.currentTimeMillis();
        
        // Fill first 48 bits with timestamp
        value[0] = (byte) (timestamp >> 40);
        value[1] = (byte) (timestamp >> 32);
        value[2] = (byte) (timestamp >> 24);
        value[3] = (byte) (timestamp >> 16);
        value[4] = (byte) (timestamp >> 8);
        value[5] = (byte) timestamp;
        
        // Set version (0111 = 7) and variant (10 = RFC 4122)
        value[6] = (byte) ((value[6] & 0x0F) | 0x70);
        value[8] = (byte) ((value[8] & 0x3F) | 0x80);
        
        ByteBuffer buffer = ByteBuffer.wrap(value);
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
