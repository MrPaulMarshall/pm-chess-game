package chessgame.presenter;

import chessgame.controller.BoardScreenController;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BoardCell {

    private static final Background WHITE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#EEEED5"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background BLACK_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#7D945D"), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Background WHITE_CLICKABLE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#abab9a"), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background BLACK_CLICKABLE_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#64764a"), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Background MARKED_BACKGROUND = new Background(
            new BackgroundFill(Color.valueOf("#E2514C"), CornerRadii.EMPTY, Insets.EMPTY));

    private final Pane pane;

    private final BoardScreenController boardScreenController;

    private final ImageView imageView;

    private final Background normalBackground;
    private final Background clickableBackground;

    public BoardCell(int i, int j, Pane pane, BoardScreenController boardScreenController) {
        this.pane = pane;
        this.boardScreenController = boardScreenController;
        this.imageView = new ImageView();

        pane.setMaxWidth(50);
        pane.setMinWidth(50);
        pane.setMaxHeight(50);
        pane.setMinHeight(50);
        pane.getChildren().add(this.imageView);

        if ((i + j) % 2 == 0) {
            this.normalBackground = WHITE_BACKGROUND;
            this.clickableBackground = WHITE_CLICKABLE_BACKGROUND;
        } else {
            this.normalBackground = BLACK_BACKGROUND;
            this.clickableBackground = BLACK_CLICKABLE_BACKGROUND;
        }

        this.pane.setBackground(this.normalBackground);

        pane.setOnMouseClicked(e -> this.boardScreenController.boardCellOnClick(i, j));
    }

    public Pane getPane() {
        return this.pane;
    }

    public void setImage(Image image) {
        this.imageView.setImage(image);
    }

    public void refreshBackground() {
        this.pane.setBackground(this.normalBackground);
    }

    public void setChosenBackground() {
        this.pane.setBackground(MARKED_BACKGROUND);
    }

    public void setClickableBackground() {
        this.pane.setBackground(this.clickableBackground);
    }
}
