package cn.geofound.technology.service;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.common.utils.redis.base.JedisHash;
import cn.geofound.common.utils.redis.base.JedisKeys;
import cn.geofound.common.utils.redis.base.JedisLists;
import cn.geofound.common.utils.redis.base.JedisSets;
import cn.geofound.common.utils.redis.base.JedisSortSet;
import cn.geofound.common.utils.redis.base.JedisStrings;

/**
 * reidis缓存工具服务
 * @author zhangjialu
 * @date 2018-9-14上午10:10:44
 */
@IocBean
public class BaseRedisService {

	
	/**
	 * 1、String </br> 常用命令： </br> 除了get、set、incr、decr mget等操作外，Redis还提供了下面一些操作：
	 * </br> 获取字符串长度 </br> 往字符串append内容 </br> 设置和获取字符串的某一段内容 </br>
	 * 设置及获取字符串的某一位（bit）</br> 批量设置一系列字符串的内容 </br> </br> 应用场景： </br>
	 * String是最常用的一种数据类型，普通的key/value存储都可以归为此类，value其实不仅是String， </br>
	 * 也可以是数字：比如想知道什么时候封锁一个IP地址(访问超过几次)。INCRBY命令让这些变得很容易，通过原子递增保持计数。</br> </br>
	 * 实现方式： </br> m,decr等操作时会转成数值型进行计算，此时redisObject的encoding字段为int</br>
	 */
	@Inject
	public JedisStrings jedisStrings;

	/**
	 * 常用命令： hget,hset,hgetall 等。 </br> 应用场景： </br>
	 * 我们简单举个实例来描述下Hash的应用场景，比如我们要存储一个用户信息对象数据，包含以下信息：</br> </br> 用户ID，为查找的key，
	 * </br> 存储的value用户对象包含姓名name，年龄age，生日birthday 等信息， </br> </br>
	 * 如果用普通的key/value结构来存储，主要有以下2种存储方式： </br>
	 * 第一种方式将用户ID作为查找key,把其他信息封装成一个对象以序列化的方式存储， </br> 如：set u001
	 * "李三,18,20010101" </br>
	 * 这种方式的缺点是，增加了序列化/反序列化的开销，并且在需要修改其中一项信息时，需要把整个对象取回，并且修改操作需要对并发进行保护
	 * ，引入CAS等复杂问题。 </br>
	 * 第二种方法是这个用户信息对象有多少成员就存成多少个key-value对儿，用用户ID+对应属性的名称作为唯一标识来取得对应属性的值， </br>
	 * 如：mset user:001:name "李三 "user:001:age18 user:001:birthday "20010101"
	 * </br> 虽然省去了序列化开销和并发问题，但是用户ID为重复存储，如果存在大量这样的数据，内存浪费还是非常可观的。 </br>
	 * 那么Redis提供的Hash很好的解决了这个问题，Redis的Hash实际是内部存储的Value为一个HashMap， </br>
	 * 并提供了直接存取这个Map成员的接口， </br> 如：hmset user:001 name "李三" age 18 birthday
	 * "20010101"</br> 也就是说，Key仍然是用户ID,value是一个Map，这个Map的key是成员的属性名，value是属性值，
	 * </br> 这样对数据的修改和存取都可以直接通过其内部Map的Key(Redis里称内部Map的key为field), 也就是通过 </br>
	 * </br> key(用户ID) + field(属性标签)
	 * 操作对应属性数据了，既不需要重复存储数据，也不会带来序列化和并发修改控制的问题。很好的解决了问题。 </br>
	 * 这里同时需要注意，Redis提供了接口(hgetall)可以直接取到全部的属性数据,但是如果内部Map的成员很多，</br>
	 * 那么涉及到遍历整个内部Map的操作，由于Redis单线程模型的缘故，这个遍历操作可能会比较耗时，</br>
	 * 而另其它客户端的请求完全不响应，这点需要格外注意。 </br> </br> 实现方式： </br> 上面已经说到Redis
	 * Hash对应Value内部实际就是一个HashMap，实际这里会有2种不同实现，</br>
	 * 这个Hash的成员比较少时Redis为了节省内存会采用类似一维数组的方式来紧凑存储，而不会采用真正的HashMap结构，</br>
	 * 对应的value redisObject的encoding为zipmap,当成员数量增大时会自动转成真正的HashMap,</br>
	 * 此时encoding为ht。 </br>
	 */
	@Inject
	public JedisHash jedisHash;

	/**
	 * 常用命令： lpush,rpush,lpop,rpop,lrange,BLPOP(阻塞版)等。 </br>
	 * 
	 * 应用场景： </br> Redis list的应用场景非常多，也是Redis最重要的数据结构之一。 </br>
	 * 我们可以轻松地实现最新消息排行等功能。 </br>
	 * Lists的另一个应用就是消息队列，可以利用Lists的PUSH操作，将任务存在Lists中，</br>
	 * 然后工作线程再用POP操作将任务取出进行执行。 </br> </br> 实现方式： </br> Redis
	 * list的实现为一个双向链表，即可以支持反向查找和遍历，更方便操作，不过带来了部分额外的内存开销，</br>
	 * Redis内部的很多实现，包括发送缓冲队列等也都是用的这个数据结构。 </br> </br> RPOPLPUSH source
	 * destination </br> </br> 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作： </br> 将列表 source
	 * 中的最后一个元素(尾元素)弹出，并返回给客户端。 </br> 将 source 弹出的元素插入到列表 destination ，作为
	 * destination 列表的的头元素。 </br> 如果 source 和 destination
	 * 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。 </br>
	 * 一个典型的例子就是服务器的监控程序：它们需要在尽可能短的时间内，并行地检查一组网站，确保它们的可访问性。 </br> redis.lpush
	 * "downstream_ips", "192.168.0.10" </br> redis.lpush "downstream_ips",
	 * "192.168.0.11" </br> redis.lpush "downstream_ips", "192.168.0.12" </br>
	 * redis.lpush "downstream_ips", "192.168.0.13" </br> Then: </br> next_ip =
	 * redis.rpoplpush "downstream_ips", "downstream_ips" </br> </br> BLPOP
	 * </br> </br> 假设现在有 job 、 command 和 request 三个列表，其中 job 不存在， command 和
	 * request 都持有非空列表。考虑以下命令： </br> BLPOP job command request 30
	 * #阻塞30秒，0的话就是无限期阻塞,job列表为空,被跳过,紧接着command 列表的第一个元素被弹出。 </br> 1) "command"
	 * # 弹出元素所属的列表 </br> 2) "update system..." # 弹出元素所属的值 </br>
	 * 为什么要阻塞版本的pop呢，主要是为了避免轮询。举个简单的例子如果我们用list来实现一个工作队列。</br>
	 * 执行任务的thread可以调用阻塞版本的pop去获取任务这样就可以避免轮询去检查是否有任务存在。当任务来时候工作线程可以立即返回，</br>
	 * 也可以避免轮询带来的延迟</br>
	 */
	@Inject
	public JedisLists jedisLists;

	/**
	 * 4、Set </br> </br> 常用命令： </br> sadd,srem,spop,sdiff ,smembers,sunion 等。
	 * </br> </br> 应用场景： </br> Redis
	 * set对外提供的功能与list类似是一个列表的功能，特殊之处在于set是可以自动排重的，当你需要存储一个列表数据，</br>
	 * 又不希望出现重复数据时，set是一个很好的选择，并且set提供了判断某个成员是否在一个set集合内的重要接口，这个也是list所不能提供的。
	 * </br> 比如在微博应用中，每个人的好友存在一个集合（set）中，这样求两个人的共同好友的操作，可能就只需要用求交集命令即可。 </br>
	 * Redis还为集合提供了求交集、并集、差集等操作，可以非常方便的实 </br> </br> 实现方式： </br> set 的内部实现是一个
	 * value永远为null的HashMap，实际就是通过计算hash的方式来快速排重的，</br>
	 * 这也是set能提供判断一个成员是否在集合内的原因。 </br>
	 */
	@Inject
	public JedisSets jedisSets;

	/**
	 * 5、Sorted set </br> </br> 常用命令： </br> zadd,zrange,zrem,zcard等 </br> </br>
	 * 使用场景： </br> 以某个条件为权重，比如按顶的次数排序. </br>
	 * ZREVRANGE命令可以用来按照得分来获取前100名的用户，ZRANK可以用来获取用户排名，非常直接而且操作容易。 </br> Redis
	 * sorted set的使用场景与set类似，区别是set不是自动有序的，</br> 而sorted
	 * set可以通过用户额外提供一个优先级(score)的参数来为成员排序，</br> 并且是插入有序的，即自动排序。 </br> 比如:twitter
	 * 的public timeline可以以发表时间作为score来存储，</br> 这样获取时就是自动按时间排好序的。 </br>
	 * 比如:全班同学成绩的SortedSets，value可以是同学的学号，而score就可以是其考试得分，</br>
	 * 这样数据插入集合的，就已经进行了天然的排序。 </br> 另外还可以用Sorted
	 * Sets来做带权重的队列，比如普通消息的score为1，重要消息的score为2，</br>
	 * 然后工作线程可以选择按score的倒序来获取工作任务。让重要的任务优先执行。 </br>
	 * 
	 * 需要精准设定过期时间的应用 </br> 比如你可以把上面说到的sorted
	 * set的score值设置成过期时间的时间戳，那么就可以简单地通过过期时间排序，</br>
	 * 定时清除过期数据了，不仅是清除Redis中的过期数据，你完全可以把Redis里这个过期时间当成是对数据库中数据的索引，</br>
	 * 用Redis来找出哪些数据需要过期删除，然后再精准地从数据库中删除相应的记录。 </br> </br> </br> 实现方式：
	 * </br></br> Redis sorted set的内部使用HashMap和跳跃表(SkipList)来保证数据的存储和有序，</br>
	 * HashMap里放的是成员到score的映射，</br> 而跳跃表里存放的是所有的成员，排序依据是HashMap里存的score,</br>
	 * 使用跳跃表的结构可以获得比较高的查找效率，并且在实现上比较简单。</br>
	 */
	@Inject
	public JedisSortSet jedisSortSet;

	@Inject
	public JedisKeys jedisKeys;
	
}
