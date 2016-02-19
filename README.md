Dry Redis
=========

Pure-Java in-memory implementation to `Redis`. The objective is to at-par with all commands
that `Redis` supports. There are many other implementation available like:

* [Mock Jedis](https://github.com/50onRed/mock-jedis) - updated over an year ago
* [Redis Mock Java](https://github.com/wilkenstein/redis-mock-java)

Commands not yet supported:

* List
  * blpop
  * brpop
  * brpoplpush
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
* Geo
  * All: geoadd, geohash, geopos, geodist, georadius, georadiusbymember. All methods are in redis-beta

TODO
----

Change all methods to be `synchronized` so that they are thread-safe as `Redis` is.


