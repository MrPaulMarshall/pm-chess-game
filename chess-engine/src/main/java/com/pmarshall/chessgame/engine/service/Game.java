package com.pmarshall.chessgame.engine.service;

import com.pmarshall.chessgame.engine.dto.LegalMove;
import com.pmarshall.chessgame.engine.dto.Piece;
import com.pmarshall.chessgame.engine.properties.PieceType;
import com.pmarshall.chessgame.engine.properties.Color;
import com.pmarshall.chessgame.engine.properties.Position;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public interface Game {

    Color currentPlayer();

    LegalMove lastMove();

    /**
     * @return pair {winner, message}, where winner == null in case of a draw.
     */
    Pair<Color, String> outcome();

    /**
     * @return 8x8 array of pairs {type, color} that fully represents current configuration on the board.
     *         If the cell is empty, it contains null
     */
    Piece[][] getBoardWithPieces();

    Piece getPiece(Position on);

    boolean isPromotionRequired(Position from, Position to);

    boolean isMoveLegal(Position from, Position to);

    Collection<Position> legalMovesFrom(Position from);

    /**
     * @return true if move is legal and thus executed. In case promotion is required this call will fail.
     */
    boolean executeMove(Position from, Position to);

    /**
     * @return true if move is legal and this executed. In case promotion is not happening this call will fail.
     */
    boolean executeMove(Position from, Position to, PieceType promotion);
}
