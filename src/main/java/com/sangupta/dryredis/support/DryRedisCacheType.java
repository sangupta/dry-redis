package com.sangupta.dryredis.support;

public enum DryRedisCacheType {

	LIST(1),
	
	SET(2),
	
	SORTED_SET(3),
	
	STRING(4),
	
	HASH(5),
	
	GEO(6),
	
	HYPER_LOG_LOG(7);
    
    private byte code;
    
    private DryRedisCacheType(int code) {
        this.code = (byte) code;
    }
    
    public byte getCode() {
        return this.code;
    }
	
}
