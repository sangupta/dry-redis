Dry Redis
=========


[![Build Status](https://travis-ci.org/sangupta/dry-redis.svg?branch=master)](https://travis-ci.org/sangupta/dry-redis)
[![Coverage Status](https://coveralls.io/repos/github/sangupta/dry-redis/badge.svg?branch=master)](https://coveralls.io/github/sangupta/dry-redis?branch=master)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/com.sangupta/dry-redis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sangupta/dry-redis)

Pure-Java in-memory implementation to `Redis`. The objective is to at-par with all commands
that `Redis` supports. There are many other implementation available like:

* [Mock Jedis](https://github.com/50onRed/mock-jedis) - updated over an year ago
* [Redis Mock Java](https://github.com/wilkenstein/redis-mock-java)

Commands not yet supported:

* List
* Set
  * sscan
* Strings
  * bitop
  * bitpos
  * getbit
  * mset
  * msetnx
  * psetex
  * setbit
  * setex
* Hash
  * hscan
* HyperLogLog
* Geo



Important Notes
---------------

* Geo algorithms currently use a brute-force method, a O(n) search and thus will be slower than `Redis`
* Geo methods use `haversine` method to compute distance between two points
* HyperLogLog implementation uses https://github.com/addthis/stream-lib libraries implementation and may thus slightly differ from `Redis`

TODO
----

Change all methods to be `synchronized` so that they are thread-safe as `Redis` is.


