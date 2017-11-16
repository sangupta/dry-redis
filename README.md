# Dry Redis

[![Build Status](https://travis-ci.org/sangupta/dry-redis.svg?branch=master)](https://travis-ci.org/sangupta/dry-redis)
[![Coverage Status](https://coveralls.io/repos/github/sangupta/dry-redis/badge.svg?branch=master)](https://coveralls.io/github/sangupta/dry-redis?branch=master)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/com.sangupta/dryredis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sangupta/dryredis)

Pure-Java in-memory implementation to `Redis` that makes testing with `Redis` super-easy via
the use of [dry-run](https://github.com/sangupta/dry-run) library project. The objective is 
to at-par with all commands that `Redis` supports. `dry-redis` can also be used to replace
`Redis` during development phase, to be an embedded in-memory implementation so that you need
not run `Redis` everytime. 

The library is tested on the following JDK versions:

* Oracle JDK 8
* Oracle JDK 7
* Open JDK 7

`dry-redis` was born out of frustration to test my own Redis code, as all other implementations
either were too stale/old, or required me to run an embedded server connecting over TCP.
Other notable implementations available include:

* [Mock Jedis](https://github.com/50onRed/mock-jedis) - Last updated 3 Mar 2015
* [Redis Mock Java](https://github.com/wilkenstein/redis-mock-java) - Last updated 22 Sep 2015
* [Embedded-Redis](https://github.com/kstyrc/embedded-redis) - Runs a server to connect to Redis for unit testing

## Table of Contents

* [Introduction](#dry-redis)
* [Table of Contens=ts](#table-of-contents)
* [Usage](#usage)
* [Downloads](#downloads)
* [Changelog](#changelog)
* [TODO items](#todo)
* [Supported commands](#commands-not-yet-supported)
* [Differences from Redis](#behaviour-differences-from-redis)
* [Versioning](#versioning)
* [License](#license)

## Usage

To fetch a `Redis` database instance you may use one of the following methods:

```java
DryRedis redis = DryRedis.getDatabase(); // gets the default singleton instance

// fetch a new singleton instance represented by "myDatabase"
redis = DryRedis.getDatabase("myDatabase");

// fetching an instance for "myDatabase" again will return the same instance
redis =  DryRedis.getDatabase("myDatabase"); // the instance is same as before

// All `Redis` commands are available on this instance:

redis.setnx("dryredis-version", "1"); // succeeds
redis.setnx("dryredis-version", "2"); // fails
```

For a list of all Redis commands, refer http://redis.io/commands.

## Download

The latest released binaries are available from Maven Central using:

```xml
<dependency>
    <groupId>com.sangupta</groupId>
    <artifactId>dry-redis</artifactId>
    <version>0.8.0</version>
</dependency>
```

## Changelog

**Version 0.8.0** - 12 Apr 2017

* 8 type of command-groups are now implemented
* initial release

## TODO

* Thread-safety is currently not supported
* Multiple redis databases and moving between databases is currently not supported
* Increase unit-test code-coverage
* Implement missing Redis commands (see below for details)
* Remove differences from actual Redis implementations (see below for details)

## Commands not yet supported:

DryRedis supports all `Redis` commands as of version 3.2 (stable) except the following ones:

* Cluster
  * none of the commands is supported
* Connection
  * none of the commands is supported
* Geo
* Hashes
  * [hscan](https://redis.io/commands/hscan)
* HyperLogLog
* Keys
  * [sort](https://redis.io/commands/hscan)
  * [scan](https://redis.io/commands/scan)
  * [migrate](https://redis.io/commands/migrate)
  * [move](https://redis.io/commands/move)
  * [object](https://redis.io/commands/object)
  * [persist](https://redis.io/commands/persist)
  * [randomkey](https://redis.io/commands/randomkey)
  * [restore](https://redis.io/commands/restore)
  * [sort](https://redis.io/commands/sort)
  * [ttl](https://redis.io/commands/ttl)
  * [pttl](https://redis.io/commands/pttl)
* Lists
  * all commands supported
* Pub/Sub
  * none of the commands is supported
* Scripting
  * none of the commands is supported
* Server
  * none of the commands is supported
* Sets
  * [sscan](https://redis.io/commands/sscan)
* Sorted Set
  * [zscan](https://redis.io/commands/zscan)
* Strings
  * [setbit](https://redis.io/commands/setbit)
* Transactions
  * none of the commands is supported
  
## Behaviour differences from Redis

* Geo algorithms currently use a brute-force method, a O(n) search and thus will be slower than `Redis`
* Geo methods use `haversine` method to compute distance between two points but return a slightly different distance than Redis
* HyperLogLog implementation uses https://github.com/addthis/stream-lib libraries implementation and may thus slightly differ from `Redis`

## Versioning

For transparency and insight into our release cycle, and for striving to maintain 
backward compatibility, `dry-redis` will be maintained under the Semantic 
Versioning guidelines as much as possible.

Releases will be numbered with the follow format:

```
<major>.<minor>.<patch>
```

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major
* New additions without breaking backward compatibility bumps the minor
* Bug fixes and misc changes bump the patch

For more information on SemVer, please visit http://semver.org/.

## License

```
 dry-redis: In-memory pure java implementation to Redis
 Copyright (c) 2016-2017, Sandeep Gupta
 
 http://sangupta.com/projects/dry-redis
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
```
