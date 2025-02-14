package com.example.treasurehunter.data.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treasurehunter.data.model.ScreenMode
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoomViewModel @Inject constructor() : ViewModel() {
    private var socket: Socket? = null
    private var input: ByteReadChannel? = null
    private var output: ByteWriteChannel? = null

    val roomId = mutableStateOf("")
    val message = mutableStateOf("")
    val joinedRoom = mutableStateOf("")
    val members = mutableStateOf("Members: ")

    fun connectToServer(host: String, port: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i("SOCKET", "connect to server: $host:$port")
                socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(host, port.toInt())
                input = socket?.openReadChannel()
                output = socket?.openWriteChannel(autoFlush = true)

                Log.i("SOCKET", "Connected to server $host:$port")

                val response = input?.readUTF8Line()
                message.value = response ?: "No response from server"
            } catch (e: Exception) {
                message.value = "Error: ${e.message}"
            }
        }
    }

    fun fetchMembers(roomCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                output?.writeStringUtf8("FETCH_MEMBERS, ROOM_ID:$roomCode\n")

                val response = input?.readUTF8Line()
                response?.let {
                    if (it.startsWith("MEMBERS:")) {
                        members.value = it.split(":")[1]
                    }
                }
            } catch (e: Exception) {
                Log.e("SOCKET", "fetchMembers: ${e.message}")
            }
        }
    }

    fun startGame(roomCode: String, radius: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                output?.writeStringUtf8("START_GAME, ROOM_ID:$roomCode, RADIUS:$radius\n")
            } catch (e: Exception) {
                Log.e("SOCKET", "startGame: ${e.message}")
            }
        }
    }

    fun endGame(roomCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                output?.writeStringUtf8("END_GAME, ROOM_ID:$roomCode\n")
            } catch (e: Exception) {
                Log.e("SOCKET", "endGame: ${e.message}")
            }
        }
    }

    fun inGame() {
        viewModelScope.launch(Dispatchers.IO) {
            var running = true
            while (running) {
                try {
                    val response = input?.readUTF8Line()
                    response?.let {
                        if (it.startsWith("END_GAME")) {
                            PuzzleViewModel.isSolved = true
                            GameViewModel.screenMode = ScreenMode.PUZZLE
                            running = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SOCKET", "waitingGame: ${e.message}")
                }
            }
        }
    }

    fun waitingMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            var running = true
            while (running) {
                try {
                    val response = input?.readUTF8Line()
                    response?.let {
                        if (it.startsWith("MEMBER_JOINED:")) {
                            members.value += "${it.split(":")[1]},"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SOCKET", "waitingMembers: ${e.message}")
                }
                
                delay(100)
            }
        }
    }

    fun waitingGame() {
        viewModelScope.launch(Dispatchers.IO) {
            var running = true
            while (running) {
                try {
                    val response = input?.readUTF8Line()
                    response?.let {
                        if (it.startsWith("GAME_STARTED")) {
                            val radiusMessage = it.split(",")[1]
                            val radius = radiusMessage.split(":")[1]

                            GameViewModel.setGameRadius(radius.toDouble())

                            message.value = "Game started!"
                            running = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SOCKET", "waitingGame: ${e.message}")
                }
            }
        }
    }

    fun createRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            var running = true
            while (running) {
                try {
                    output?.writeStringUtf8("CREATE_ROOM, PLAYER_ID:${SocketViewModel.playerID}\n")
                    Log.i("SOCKET", "create room:")
                    running = false
                } catch (e: Exception) {
                    Log.e("SOCKET", "joinRoom: ${e.message}")
                }
            }

            running = true
            while (running) {
                try {
                    val response = input?.readUTF8Line()
                    Log.i("SOCKET", "create room response: $response")
                    response?.let {
                        if (it.startsWith("ROOM_CREATED:")) {
                            running = false
                            roomId.value = it.split(":")[1]
                        }
                    }

                } catch (e: Exception) {
                    Log.e("SOCKET", "createRoom: ${e.message}")
                }

                delay(100)
            }
        }
    }

    fun joinRoom(roomCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var running = true
            while (running) {
                try {
                    output?.writeStringUtf8("JOIN_ROOM, PLAYER_ID:${SocketViewModel.playerID}, ROOM_ID:$roomCode\n")
                    Log.i("SOCKET", "join room:")
                    running = false
                } catch (e: Exception) {
                    Log.e("SOCKET", "joinRoom: ${e.message}")
                }
            }

            running = true
            while (running) {
                try {
                    val response = input?.readUTF8Line()
                    Log.i("SOCKET", "join room response: $response")
                    response?.let {
                        if (it.startsWith("JOIN_SUCCESS:")) {
                            joinedRoom.value = it
                            SocketViewModel.room.roomId.value = it.split(':')[1]
                            running = false
                        } else if (it.startsWith("MEMBERS:")) {
                            members.value = it.split(":")[1]
                        } else {
                            message.value = "Room not found!"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SOCKET", "joinRoom: ${e.message}")
                }

                delay(100)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        socket?.close()
    }
}
