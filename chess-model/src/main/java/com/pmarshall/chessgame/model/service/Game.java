package com.pmarshall.chessgame.model.service;

import com.pmarshall.chessgame.model.properties.PieceType;
import com.pmarshall.chessgame.model.properties.Color;
import com.pmarshall.chessgame.model.properties.Position;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

public interface Game {

    Color currentPlayer();

    boolean activeCheck();

    boolean gameEnded();

    String lastMoveInNotation();

    /**
     * @return pair {winner, message}, where winner == null in case of a draw.
     */
    Pair<Color, String> outcome();

    /**
     * @return 8x8 array of pairs {type, color} that fully represents current configuration on the board.
     *         If the cell is empty, it contains null
     */
    Pair<PieceType, Color>[][] getBoardWithPieces();

    /**
     * @return collection of triples {from, to, promotionRequired} that represent legal moves in current turn
     */
    Collection<Triple<Position, Position, Boolean>> legalMoves();

    /**
     * @return true if move is legal and thus executed. In case promotion is required this call will fail.
     */
    boolean executeMove(Position from, Position to);

    /**
     * @return true if move is legal and this executed. In case promotion is not happening this call will fail.
     */
    boolean executeMove(Position from, Position to, PieceType promotion);
}
