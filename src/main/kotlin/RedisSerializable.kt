interface RedisSerializable


abstract class RedisDao(private val redis:Class<out RedisSerializable>){


    private val _redis:Redis
    get() = redis.getAnnotation(Redis::class.java)


    suspend fun flush(entity:RedisSerializable){

        KRedis.launch { client->
            val tk = redis.getDeclaredField(_redis.key)
            tk.isAccessible=true
            redis.declaredFields.forEach {
                it.isAccessible=true
                val v = it[entity]
                val p = it.name to serialize(v)

                client.hset("${_redis.module}-${tk[entity]}",p)
            }
        }

    }

    suspend fun flush(vararg entities:RedisSerializable){
        entities.forEach {
            flush(it)
        }
    }

    suspend fun flush(entities:List<RedisSerializable>){
        entities.forEach {
            flush(it)
        }
    }

//    suspend fun entity(entity: RedisSerializable){
//        Kredis.launch {client->
//            val tk = redis.getDeclaredField(_redis.key)
//            tk.isAccessible=true
//            redis.declaredFields.forEach {
//                it.isAccessible=true
//                if (it.name!=_redis.key){
//                    it.set(entity,it.type.cast(client.hget("${_redis.module}-${tk[entity]}",it.name)))
//                }
//            }
//        }
//    }

    @Suppress("Unchecked_Cast")
    suspend fun<T> entity(key: String):T{
        val list = mutableListOf<Any>()
        KRedis.launch { client->
            redis.declaredFields.forEach {
                it.isAccessible=true
                list.add(deSerialize(client.hget("${_redis.module}-${key}",it.name)?:"",it.type))
            }
        }
        return redis.getConstructor(*(redis.declaredFields.map { it.type }.toTypedArray())).newInstance(*(list.toTypedArray())) as T
    }

}




fun<T> serialize(value:T):String{
    return value.toString()
}

fun deSerialize(value: String,type:Class<*>):Any{
    return when(type.name){
        "int"->value.toInt()
        "double" ->value.toDouble()
        "float"->value.toFloat()
        "boolean"->value.toBoolean()
        "long"->value.toLong()
        "short"->value.toShort()
        else -> {value}
    }
}

/**
 * @param module 模块/表名
 * @param key 主键名
 */
annotation class Redis(val module:String,val key:String)

