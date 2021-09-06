package me.amallela.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.asFlow
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collect

class AmallelaClient(private val channel: ManagedChannel) : Closeable {
    private val stub: AmallelaGrpcKt.AmallelaCoroutineStub = AmallelaGrpcKt.AmallelaCoroutineStub(channel)

    suspend fun makeGrpcCalls(msg: String) {
        println("### reverse ###")
        val message = Message.newBuilder().setMsg(msg).build()
        val resp1 = stub.reverse(message)
        println("Received: ${resp1.msg}")

        println("\n### shuffle ###")
        val resp2 = stub.shuffle(message)
        resp2.collect {
            value -> println("Received: ${value.msg}")
        }

        println("\n### concat ###")
        val parts = listOf<String>("amallela", "is", "a", "cloudstack", "engineer")
        val msgs = mutableListOf<Message>()
        parts.forEach { part -> msgs.add(Message.newBuilder().setMsg(part).build()) }
        val resp3 = stub.concat(msgs.asFlow())
        println("Received: ${resp3.msg}")

        println("\n### concatAndSplit ###")
        val msgs2 = mutableListOf<Message>()
        parts.forEach { part -> msgs2.add(Message.newBuilder().setMsg(part).build()) }
        val resp4 = stub.concatAndSplit(msgs2.asFlow())
        resp4.collect {
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

    client.makeGrpcCalls("amallela")
}
