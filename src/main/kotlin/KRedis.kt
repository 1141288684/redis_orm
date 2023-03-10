import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient

object KRedis {

    private lateinit var client: KredsClient

    fun init(url:String){
        //"127.0.0.1:6379"
        client=newClient(Endpoint.from(url))
    }

    suspend fun launch(func:suspend (KredsClient)->Unit){
        func(client)
    }


}