package com.sangupta.dryredis.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import com.sangupta.dryredis.DryRedis;

public class DryRedisUtils {
    
    private static final Charset UTF_8 = Charset.forName("UTF-8"); 

	public static <V> List<V> subList(List<V> list, int start, int stop) {
		return list.subList(start, stop);
	}

	public static byte[] createDump(DryRedisCacheType cacheType, String key, Object value) {
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    
	    outStream.write(DryRedis.DRY_REDIS_DUMP_VERSION);
	    outStream.write(cacheType.getCode());
	    
	    // write the key
	    outStream.write(key.length());
	    try {
            outStream.write(key.getBytes(UTF_8));
        } catch (IOException e) {
            // eat up
        }
	    
	    // write a dummy length for now
	    final int pointer = outStream.size();
	    outStream.write(0);
	    
	    // write the object itself
	    try {
            ObjectOutputStream objectStream = new ObjectOutputStream(outStream);
            objectStream.writeObject(value);
            objectStream.close();
        } catch (IOException e) {
            // eat up
        }
	    
	    final int bytesWrittenForObject = outStream.size() - pointer;
	    
	    byte[] bytes = outStream.toByteArray();
	    
	    // TODO: write the bytes written for object to the
	    
	    // TODO: write and compute checksum
	    
	    return bytes;
	}
	
}
