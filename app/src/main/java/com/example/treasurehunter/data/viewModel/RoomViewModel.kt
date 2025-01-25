package com.example.treasurehunter.data.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treasurehunter.LocalNavController
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
    val members = mutableStateOf("")

    fun connectToServer(host: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i("SOCKET", "connect to server: $host:$port")
                socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(host, port)
                input = socket?.openReadChannel()
                output = socket?.openWriteChannel(autoFlush = true)

                Log.i("SOCKET", "Connected to server $host:$port")

                // Nhận thông điệp từ server
                delay(50)
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
                delay(50)
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

    fun startGame(roomCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                output?.writeStringUtf8("START_GAME, ROOM_ID:$roomCode\n")
            } catch (e: Exception) {
                Log.e("SOCKET", "startGame: ${e.message}")
            }
        }
    }

    fun waitingGame() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    delay(50)
                    val response = input?.readUTF8Line()
                    response?.let {
                        if (it.startsWith("GAME_STARTED")) {
                            message.value = "Game started!"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SOCKET", "waitingGame: ${e.message}")
                }
            }

            if (message.value == "Game started!") {

            }
        }
    }

    fun createRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                output?.writeStringUtf8("CREATE_ROOM, PLAYER_ID:${SocketViewModel.playerID}\n")
                Log.i("SOCKET", "create room:")
                delay(50)
                val response = input?.readUTF8Line()
                Log.i("SOCKET", "create room response: $response")
                response?.let {
                    if (it.startsWith("ROOM_CREATED:")) {
                        roomId.value = it.split(":")[1]
                    }
                }
            } catch (e: Exception) {
                Log.e("SOCKET", "createRoom: ${e.message}")
            }
        }
    }

    fun joinRoom(roomCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                output?.writeStringUtf8("JOIN_ROOM, PLAYER_ID:${SocketViewModel.playerID}, ROOM_ID:$roomCode\n")
                Log.i("SOCKET", "join room:")
                delay(50)
                val response = input?.readUTF8Line()
                Log.i("SOCKET", "join room response: $response")
                response?.let {
                    if (it.startsWith("JOIN_SUCCESS:")) {
                        joinedRoom.value = it
                    } else if (it.startsWith("MEMBERS:")) {
                        members.value = it.split(":")[1]
                    } else {
                        message.value = "Room not found!"
                    }
                }
            } catch (e: Exception) {
                Log.e("SOCKET", "joinRoom: ${e.message}")
            }

            if (joinedRoom.value.startsWith("JOIN_SUCCESS:")) {
                waitingGame()
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        socket?.close()
    }
}
