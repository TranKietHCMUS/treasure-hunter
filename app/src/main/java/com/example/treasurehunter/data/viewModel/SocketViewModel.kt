package com.example.treasurehunter.data.viewModel


class SocketViewModel {
    companion object {
        var room = RoomViewModel()
        val playerID = (100000..999999).random().toString()
    }
}