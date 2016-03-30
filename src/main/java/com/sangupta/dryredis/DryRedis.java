package com.sangupta.dryredis;

/**
 * Central class to create multiple instances of {@link DryRedis}.
 * This allows to simulate test cases between data migration between two
 * different REDIS databases or two different REDIS connections.
 * 
 * @author sangupta
 *
 */
public class DryRedis extends DryRedisOperationFacade {
    
    public static final int DRY_REDIS_DUMP_VERSION = 1;
    
}
