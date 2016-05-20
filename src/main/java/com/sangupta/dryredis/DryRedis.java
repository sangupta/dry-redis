/**
 *
 * dry-redis: In-memory pure java implementation to Redis
 * Copyright (c) 2016, Sandeep Gupta
 * 
 * http://sangupta.com/projects/dry-redis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
        // do nothing
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
