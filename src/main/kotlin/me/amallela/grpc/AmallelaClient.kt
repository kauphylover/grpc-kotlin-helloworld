package me.amallela.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collect

class AmallelaClient(private val channel: ManagedChannel) : Closeable {
    private val stub: AmallelaGrpcKt.AmallelaCoroutineStub = AmallelaGrpcKt.AmallelaCoroutineStub(channel)

    suspend fun reverse(name: String) {
        val message = Message.newBuilder().setMsg(name).build()
        val resp1 = stub.reverse(message)
        println("Received: ${resp1.msg}")

        val resp2 = stub.shuffle(message)
        resp2.collect {
            value -> println("Received: ${value.msg}")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

suspend fun main(args: Array<String>) {
    val port = 5050

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    val client = AmallelaClient(channel)

    client.reverse("amallela")
}
