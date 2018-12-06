package co.s4n.main

import co.s4n.server.WebServer

object Main extends App {
    WebServer.startServer("0.0.0.0", 8080)    
}