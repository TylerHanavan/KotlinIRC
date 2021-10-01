package com.tylerhanavan.kotlinirc

import java.net.Socket


class KotlinIRC {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val irc = KotlinIRC("localhost", 6667)
            irc.setNickname("tyler")
            irc.setUsername("tylerh", "localhost", "localhost", "Tyler H")
            irc.setPassword("password123!")
            irc.listen {
                println("LAMBDA: $it")
            }
        }
    }
    class KotlinIRC constructor(val hostname: String, val port: Int) {
        private val socket = Socket(hostname, port)
        private val outputStream = socket.getOutputStream()
        private var authenticated = false
        private val channels: MutableList<String> = mutableListOf<String>()
        private var buffer: String = ""

        private fun send(text: String) {
            outputStream.write("$text\r\n".toByteArray())
        }
        fun close() {
            outputStream.close()
            socket.close()
        }
        fun setUsername(username: String, hostname: String, serverName: String, realName: String) {
            send("USER $username $hostname $serverName :$realName")
        }
        fun setNickname(nickname: String) {
            send("NICK $nickname")
        }
        fun setPassword(password: String) {
            send("PASS $password")
        }
        fun joinChannel(channel: String) {
            if(!this.authenticated)
                this.channels.add(channel)
            else
                send("JOIN $channel")
        }
        fun authenticate() {
            for(channel in this.channels)
                joinChannel(channel)
        }
        fun listen(callback: (String) -> Unit) {
            val stream = this.socket.getInputStream()
            val tempBuffer = ByteArray(0)
            var count = 0;
            while (true) {
                count = stream.read(tempBuffer)
                if(count == -1)
                    break
                this.buffer += tempBuffer
                while (this.buffer.contains("\r\n")) {
                    val index = this.buffer.indexOf("\r\n")
                    val message = this.buffer.substring(0, index)
                    this.buffer.substring(index + 2)
                    println(message)
                    callback(message)
                }
            }
        }
        fun getMessage(msg: String) : Message {
            return Message("", "", "", "")
        }
        class Message constructor(val origin: String, val nickname: String, val command: String, val content: String) {

        }
    }
}