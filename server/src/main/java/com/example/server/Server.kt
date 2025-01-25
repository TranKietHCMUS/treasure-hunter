package com.example.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

data class Cico(val input : ByteReadChannel, val output : ByteWriteChannel)
data class Room(val id: String, val clients: MutableList<Socket>)

fun main() = runBlocking {
    val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind("192.168.1.8", 8080)
    println("Server is running at ${server.localAddress}")

    // Quản lý danh sách phòng
    val rooms = ConcurrentHashMap<String, Room>()
    val ID2Player = ConcurrentHashMap<String, Socket>()
    val socket2cico = ConcurrentHashMap<Socket, Cico>()

    while (true) {
        val client = server.accept()
        println("Client connected: ${client.remoteAddress}")

        launch {
            val cico = Cico(client.openReadChannel(), client.openWriteChannel(autoFlush = true))
            socket2cico[client] = cico

            while (true) {
                val message = cico.input.readUTF8Line() ?: break
                println("Message received: $message")

                when {
                    message.startsWith("CREATE_ROOM") -> {
                        val playerMessage = message.split(",")[1]
                        println("   Player message: $playerMessage")
                        val playerId = playerMessage.split(":")[1]

                        val roomId = (100000..999999).random().toString()
                        println("   Room id: $roomId")
                        rooms[roomId] = Room(roomId, mutableListOf(client))

                        cico.output.writeStringUtf8("ROOM_CREATED:$roomId\n")

                        println("   Created successfully")

                        ID2Player[playerId] = client
                    }

                    message.startsWith("JOIN_ROOM") -> {
                        val playerMessage = message.split(",")[1]
                        val playerId = playerMessage.split(":")[1]
                        val roomMessage = message.split(",")[2]
                        val roomId = roomMessage.split(":")[1]


                        println("   playerId: $playerId")
                        println("   roomId: $roomId")
                        println("   existing rooms: ${rooms.containsKey(roomId)}")
                        if (rooms.containsKey(roomId)) {
                            val room = rooms[roomId]
                            room?.clients?.add(client)

                            cico.output.writeStringUtf8("JOIN_SUCCESS:$roomId\n")
                            println("   Player $playerId joined room $roomId")

                            ID2Player[playerId] = client
                        } else {
                            cico.output.writeStringUtf8("ERROR:ROOM_NOT_FOUND\n")
                        }
                    }

                    message.startsWith("FETCH_MEMBERS") -> {
                        val roomMessage = message.split(",")[1]
                        val roomId = roomMessage.split(":")[1]
                        val room = rooms[roomId]

                        if (rooms.containsKey(roomId)) {
                            var memberMessage = "MEMBERS:"
                            room?.clients?.forEach {
                                memberMessage += it.remoteAddress.toString() + ","
                            }
                            println("   Members: $memberMessage")
//                            room?.clients?.forEach {
//                                it.openWriteChannel(autoFlush = true).writeStringUtf8(memberMessage)
//                            }
                        } else {
                            cico.output.writeStringUtf8("ERROR:ROOM_NOT_FOUND\n")
                        }
                    }

                    message.startsWith("START_GAME") -> {
                        val roomMessage = message.split(",")[1]
                        val roomId = roomMessage.split(":")[1]
                        val room = rooms[roomId]

                        if (rooms.containsKey(roomId)) {
                            room?.clients?.forEach {
                                if (it != client) {
                                    val clientCico = socket2cico[it]
                                    println("   Sending GAME_STARTED to ${it.remoteAddress}")
                                    println("   Client cico: $clientCico")
                                    clientCico?.output?.writeStringUtf8("GAME_STARTED\n")
                                }
                            }
                        } else {
                            cico.output.writeStringUtf8("ERROR:ROOM_NOT_FOUND\n")
                        }
                    }
                }
            }

            println("Client disconnected: ${client.remoteAddress}")

        }
    }
}
