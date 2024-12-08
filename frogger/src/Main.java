import Constants.Constants;
import DrawingBoard.DrawingBoard;
import Editor.Editor;
import Header.Header;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    private TextArea textArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        //HBox root = new HBox();
        Editor codeEditor = new Editor();
        root.setLeft(codeEditor);

        Header header = new Header(codeEditor);
        root.setTop(header);

        DrawingBoard drawingBoard = new DrawingBoard();
        root.setCenter(drawingBoard);

        codeEditor.setDrawingBoard(drawingBoard);
        drawingBoard.setEditorListener(codeEditor);

        drawingBoard.setConsoleInterface(header);

        Scene scene = new Scene(root, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
        primaryStage.setFullScreen(false);
        primaryStage.setMaximized(false);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle(Constants.TITLE);
        primaryStage.getIcons().add(new Image(Constants.IMG));
        primaryStage.show();

        // fixate the textAreas height after it has been stretched to scene
        codeEditor.fixateEditorTextAreaHeight();
    }
}
