package com.sangupta.dryredis;

import java.util.HashMap;
import java.util.Map;

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
    
    private static final Map<String, DryRedis> INSTANCES = new HashMap<String, DryRedis>();
    
    /**
     * The default database
     */
    private static final DryRedis DEFAULT_DATABASE = new DryRedis();
    
    /**
     * Private constructor - needed to make sure that people
     * use the {@link #getDatabase(String)} method to obtain instance.
     */
    private DryRedis() {
        
    }
    
    public static DryRedis getDatabase() {
        return DEFAULT_DATABASE;
    }
    
    /**
     * Get the {@link DryRedis} instance for the given database. For a given name
     * the same database instance is returned always.
     * 
     * @param dbName
     * @return
     */
    public static DryRedis getDatabase(String dbName) {
        if(dbName == null || dbName.trim().isEmpty()) {
            return DEFAULT_DATABASE;
        }
        
        DryRedis instance = INSTANCES.get(dbName);
        if(instance != null) {
            return instance;
        }
        
        instance = new DryRedis();
        INSTANCES.put(dbName, instance);
        return instance;
    }
    
}
