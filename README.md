一个单独的延时服务,延时服务器支持集群(无状态),通过消息队列解耦和增加可靠性

数据存储的实现基于levelDB,可配置其他存储方式
LevelDB是Google开源的持久化KV单机数据库，具有很高的随机写，顺序读/写性能，但是随机读的性能很一般。而延时服务器的查询是顺序读
iterator.seek(BinaryUtil.getBytes(position));
当定位到当前时间之后,顺序读取

服务之间的调度,通过消息队列解耦,其实现基于RocketMQ,可配置,继承AbstractSchedule即可
