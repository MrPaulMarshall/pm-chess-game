package com.marshall.chessgame.server.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pmarshall.chessgame.api.ChatMessage;
import com.pmarshall.chessgame.api.Message;
import com.pmarshall.chessgame.api.Parser;
import com.pmarshall.chessgame.api.endrequest.DrawProposition;
import com.pmarshall.chessgame.api.endrequest.DrawResponse;
import com.pmarshall.chessgame.api.endrequest.Surrender;
import com.pmarshall.chessgame.api.move.request.MoveRequest;
import com.pmarshall.chessgame.model.properties.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class Reader extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Reader.class);

    private final Color color;
    private final String id;
    private final InputStream in;

    private final Master masterThread;
    private final Writer opponentWriter;

    public Reader(Color color, String id, InputStream in, Master masterThread, Writer opponentWriter) {
        super("Reader-" + id + "-" + color);

        this.color = color;
        this.id = id;
        this.in = in;
        this.masterThread = masterThread;
        this.opponentWriter = opponentWriter;
    }

    @Override
    public void run() {
        byte[] lengthHeaderBuffer = new byte[2];
        byte[] buffer = new byte[64];
        int read;

        while (!Thread.interrupted()) {
            try {
                read = in.readNBytes(lengthHeaderBuffer, 0, 2);
                if (read < 2)
                    throw new IOException("Could not read length header");

                int msgLength = Parser.deserializeLength(lengthHeaderBuffer);
                if (buffer.length < msgLength)
                    buffer = new byte[msgLength];

                read = in.readNBytes(buffer, 0, msgLength);
                if (read < msgLength)
                    throw new IOException("Could not read " + msgLength + " byte long message");

                Message message = Parser.deserialize(buffer, msgLength);

                if (message instanceof ChatMessage) {
                    opponentWriter.pushMessage(message);
                } else if (message instanceof Surrender) {
                    masterThread.notifySurrender(color);
                } else if (message instanceof DrawProposition || message instanceof DrawResponse || message instanceof MoveRequest) {
                    masterThread.pushMessage(color, message);
                } else {
                    log.warn("Illegal message during match from player id={}, message={}", id, message);
                }

            } catch (JsonProcessingException ex) {
                log.warn("Invalid message from player id={}, message={}, stacktrace={}",
                        id, ex.getOriginalMessage(), ex.getStackTrace());
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
}
