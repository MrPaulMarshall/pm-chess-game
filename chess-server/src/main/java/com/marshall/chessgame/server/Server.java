package com.marshall.chessgame.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Server {

    @GetMapping("/server/test/{number}")
    public String getHandler(@PathVariable(required = true) Long number) {
        return "Hi from the server, " + number;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new CleanUpHook());

        // TODO: remove, as the application won't be using Spring Boot
        SpringApplication.run(Server.class, args);
    }

    /**
     * Shutdown hook that will terminate open games, interrupt threads, close opened sockets etc.
     */
    private static class CleanUpHook extends Thread {
        @Override
        public void run() {
            // TODO: implement me :)
        }
    }
}
