package com.sangupta.dryredis.support;

import java.util.List;

public class DryRedisUtils {

	public static <V> List<V> subList(List<V> list, int start, int stop) {
		// TODO: fix this - remove problems with sublist
		return list.subList(start, stop);
	}

}
