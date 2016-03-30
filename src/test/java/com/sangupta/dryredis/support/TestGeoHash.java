package com.sangupta.dryredis.support;

import org.junit.Test;

public class TestGeoHash {
    
    @Test
    public void testGeoHash() {
        String hash;
        for(int index = 0; index < 5000; index++) {
            try {
                hash = RedisGeoHash.encodeHash(38.115556395496299d, 13.361389338970184d, index);
                System.out.println(index + ": " + hash);
            } catch(Exception e) {
                continue;
            }
            
            if("sqc8b49rnyte".equals(hash)) {
                System.out.println("hash match at length: " + index);
                break;
            }
        }
    }

}
