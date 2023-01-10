import kotlinx.coroutines.runBlocking

fun main(){

    val u=User("123",987.0,false)
    runBlocking {
        KRedis.init("127.0.0.1:6379")
        UserDao.flush(u)
        KRedis.launch {
            println(it.hget("login-123","password"))
            println(u.isCheck)
            u.password=789.0
            u.isCheck=true
            UserDao.flush(u)
//            it.hset("login-123","password" to "789")
//            println(it.hget("login-123","password"))
//            val tu = User("123","???")
            val tu:User=UserDao.entity("123")
            println(tu.isCheck)
        }

    }

}

@Redis("login","uid")
data class User(val uid:String,var password:Double,var isCheck:Boolean): RedisSerializable

object UserDao:RedisDao(redis = User::class.java)