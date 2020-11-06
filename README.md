# distributed-lock
different way to implement distributed lock

## mysql 方式一
```sql
create table mylock(
`id` int(11) not null auto_increment primary key,
`lock_key` varchar(64) not null,
'expire_time' datetime not null,
union key `index_lock_key`
)
``` 
 多个线程通过insert插入记录，lock_key排他，成功了就获得了锁，释放锁时删除记录
 
 优点：
 * 实现简单，一般项目都有用数据库，不需要单独引入其他组件
 
 缺点：
 * 依赖于数据库，性能差
 * 数据库单点，锁的可用性低
 * 锁过期，需要自己写一个线程轮询去实现
 * 锁获取失败，需要轮询（不能阻塞）
 * 非公平
 
 
## mysql 方式二 行锁

数据库排它锁

select * from mylock where lock_key=? for update;
commit;

## mysql 方式三 乐观锁
```sql
create table mylock(
`id` int(11) not null auto_increment primary key,
`lock_key` varchar(64) not null,
`state` int(1) not null comment '1未分配，2已分配',
'expire_time' datetime not null,
`version` int(11) not null comment '乐观锁版本号'
)
``` 
1. 先执行 
select id, resource, state,version from t_resource  where state=1 and id=5780;
如果存在说明可以竞争锁
2. 通过乐观锁更新
update t_resoure set state=2, version=27, update_time=now() where resource=xxxxxx and state=1 and version=26
如果成功就获得了锁，其他线程就查询不到记录了
3. 释放锁的时候，在把state更新回来

* 获取锁需要两步操作，性能更差


## redis 单机模式
不能保证高可用，具体评估业务需要持有锁的大概时间

加锁(原子操作)
```
SET anyLock unique_value NX PX 30000
```

释放锁(lua脚本)
```
if redis.call("get",KEYS[1]) == ARGV[1] then
return redis.call("del",KEYS[1])
else
return 0
end
```

## master-slave + sentinel选举模式
主从切换时，锁会丢失，不能保证锁高可用

## redis cluster模式
RedLock 步骤：
* 获取当前时间戳，单位是毫秒
* 轮流尝试在每个master节点上创建锁，过期时间设置较短，一般就几十毫秒
* 尝试在大多数节点上建立一个锁，比如5个节点就要求是3个节点（n / 2 +1）
* 客户端计算建立好锁的时间，如果建立锁的时间小于超时时间，就算建立成功了
* 要是锁建立失败了，那么就依次删除这个锁
* 只要别人建立了一把分布式锁，你就得不断轮询去尝试获取锁

## redis Redisson
* API简单
* 所有指令通过lua脚本执行，保证原子性 
* watchdog(看门狗)，每隔10s把锁超时时间重设为30s，一直持有锁不会过期
* 提供redlock算法支持

## zookeeper Curator
* 使用zk的临时节点和有序节点，每个线程获取锁就是在zk创建一个临时有序的节点，比如在/lock/目录下。
* 创建节点成功后，获取/lock目录下的所有临时节点，再判断当前线程创建的节点是否是所有的节点的序号最小的节点
* 如果当前线程创建的节点是所有节点序号最小的节点，则认为获取锁成功。
* 如果当前线程创建的节点不是所有节点序号最小的节点，则对节点序号的前一个节点添加一个事件监听。
比如当前线程获取到的节点序号为/lock/003,然后所有的节点列表为[/lock/001,/lock/002,/lock/003],则对/lock/002这个节点添加一个事件监听器。
* 如果锁释放了，会唤醒下一个序号的节点，然后重新执行第3步，判断是否自己的节点序号是最小。比如/lock/001释放了，/lock/002监听到时间，此时节点集合为[/lock/002,/lock/003],则/lock/002为最小序号节点，获取到锁。

## 总结：
* redis内存操作，性能高，获取不到锁需要轮询，不是强一致性，可用性会低一些
* zookeeper强一致性，获取不到锁可以监听，但是多客户端频繁操作（加锁、释放锁），集群压力比较大


参考：
https://zhuanlan.zhihu.com/p/163224180?utm_source=wechat_timeline