package com.sangupta.dryredis;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisGeoUnit;

public class DryRedisGeo implements DryRedisCache {
	
	// redis commands
	
	public void geoadd(String key, double latitude, double longitude, String member) {
		throw new RuntimeException("not yet implemented - command in Redis beta");
	}
	
	public void geohash(String key, String member) {
		throw new RuntimeException("not yet implemented - command in Redis beta");
	}
	
	public double[] geopos(String key, String member) {
		throw new RuntimeException("not yet implemented - command in Redis beta");
	}
	
	public double geodist(String key, String member1, String member2, DryRedisGeoUnit unit) {
		throw new RuntimeException("not yet implemented - command in Redis beta");
	}
	
	public void georadius() {
		throw new RuntimeException("not yet implemented - command in Redis beta");
	}
	
	public void georadiusbymember() {
		throw new RuntimeException("not yet implemented - command in Redis beta");
	}
	
	// from interface

	@Override
	public int del(String key) {
		return 0;
	}

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.Geo;
	}

}
