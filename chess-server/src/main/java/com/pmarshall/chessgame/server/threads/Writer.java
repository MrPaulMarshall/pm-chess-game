package com.pmarshall.chessgame.server.threads;

import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.outcome.GameOutcome;
import com.pmarshall.chessgame.model.properties.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Writer extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Writer.class);

    private final Color color;
    private final String id;
    private final OutputStream out;
    private final Master masterThread;


    /* COMMUNICATION CHANNELS */
    private final Semaphore semaphore = new Semaphore(0);
    private final BlockingQueue<GameOutcome> outcomeQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public Writer(Color color, String id, OutputStream out, Master masterThread) {
        super("Writer-" + id + "-" + color);

        this.id = id;
        this.color = color;
        this.out = out;
        this.masterThread = masterThread;
    }

    @Override
    public void run() {
        boolean gameEnded = false;

        while (!Thread.interrupted()) {
            try {
                semaphore.acquire();

                Message message = outcomeQueue.poll();
                if (message != null) {
                    gameEnded = true;
                } else {
                    message = queue.take();
                }

                byte[] buffer = Parser.serialize(message);
                byte[] lengthHeaderBuffer = Parser.serializeLength(buffer.length);

                out.write(lengthHeaderBuffer);
                out.write(buffer);

                if (gameEnded)
                    break;
            } catch (IOException ex) {
                log.error("Connection to user id={} was lost", id);
                masterThread.notifyConnectionLost(color);
                break;
            } catch (InterruptedException ignored) {
                log.info("Thread {} interrupted", Thread.currentThread().getName());
            }
        }

        log.info("Thread {} is shutting down...", Thread.currentThread().getName());
    }

    public void pushMessage(Message msg) throws InterruptedException {
        queue.put(msg);
        semaphore.release();
    }

    /**
     * Inform the client about the end of the game.
     * This is considered the final message and the thread will finish after sending it.
     */
    public void pushGameOutcome(GameOutcome msg) throws InterruptedException {
        outcomeQueue.put(msg);
        semaphore.release();
    }
}
