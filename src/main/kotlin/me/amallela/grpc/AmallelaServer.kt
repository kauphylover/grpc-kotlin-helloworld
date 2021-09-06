package me.amallela.grpc

import com.google.protobuf.value
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class AmallelaServer {
    private val server: Server = ServerBuilder
        .forPort(5050)
        .addService(AmallelaService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on 5050")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@AmallelaServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class AmallelaService : AmallelaGrpcKt.AmallelaCoroutineImplBase() {
        override suspend fun reverse(message: Message): Message = Message
            .newBuilder()
            .setMsg(message.msg.reversed())
            .build()

        override fun shuffle(message: Message): Flow<Message> = getShuffled(message.msg, 5).asFlow()

        private fun getShuffled(msg: String, n: Int): Collection<Message> {
            val msgs = mutableListOf<Message>()

            for (i in 1..n) {
                val tmp = msg.toCharArray()
                tmp.shuffle()
                msgs.add(
                    Message.newBuilder().setMsg(String(tmp)).build()
                )
            }
            return msgs
        }

        override suspend fun concat(messages: Flow<Message>) : Message {
            var concatenated = "";
            messages.collect {
                msg -> concatenated += msg.msg
            }
            return Message.newBuilder().setMsg(concatenated).build()
        }

        override fun concatAndSplit(messages: Flow<Message>) : Flow<Message> = runBlocking<Flow<Message>> {
            var concatenated = "";
            messages.collect {
                msg -> concatenated += msg.msg
            }

            val indexes = mutableListOf<Int>(0)
            for (i in 1..4) {
                indexes.add(Random.nextInt(concatenated.length))
            }
            indexes.add(concatenated.length)
            indexes.sort()
            val msgs = mutableListOf<Message>()
            for (i in 1..5) {
                msgs.add(Message.newBuilder().setMsg(concatenated.substring(indexes.get(i-1), indexes.get(i))).build())
            }
            msgs.asFlow()
        }
    }
}

fun main() {
    val server = AmallelaServer()
    server.start()
    server.blockUntilShutdown()
}
