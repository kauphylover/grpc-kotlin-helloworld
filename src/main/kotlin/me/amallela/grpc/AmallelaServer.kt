package me.amallela.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

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

        override fun reverseStream(message: Message): Flow<Message> = getShuffled(message.msg, 5).asFlow()

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
    }
}

fun main() {
    val server = AmallelaServer()
    server.start()
    server.blockUntilShutdown()
}
