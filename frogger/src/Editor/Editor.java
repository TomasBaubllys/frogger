package Editor;

import CodeParser.*;
import Constants.Constants;
import DrawingBoard.DrawingBoardInteface;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import Frog.*;


public class Editor extends BorderPane implements EditorInterface{

    private final TextArea textArea;
    private int comCounter;

    private DrawingBoardInteface boardListener = null;
    private ArrayList<String> commands;
    private ArrayList<Command> cmds;

    // Constructs the layout and initializes everything
    public Editor() {
        super();

        this.textArea = new TextArea();
        this.textArea.setStyle("-fx-font-family: 'Roboto Light';");
        this.textArea.setMaxWidth(350);

        // if set to true it breaks the line numbers
        this.textArea.setWrapText(false);

        HBox editorContent = new HBox();
        editorContent.getChildren().add(textArea);

        // Set layout
        this.setPadding(Constants.DEFAULT_PADDING);
        this.setCenter(editorContent);

        this.comCounter = 0;
    }

    public void runCode() {
        this.cmds = CodeParser.parseCode(textArea.getText());
        this.comCounter = 0;

        boardListener.moveFrog(this.getNextCommand(boardListener.getFrog()));
    }

    public void setDrawingBoard(DrawingBoardInteface drawingBoardInteface) {
        this.boardListener = drawingBoardInteface;
    }

    public void setText(String text) {
        this.textArea.setText(text);
    }

    public String getText() {
        return this.textArea.getText();
    }

    // used to fix editor height after a window has been created
    public void fixateEditorTextAreaHeight() {
        this.textArea.setMaxHeight(this.textArea.getHeight());
    }

    public String getNextCommand(Frog frog) {
        if(this.comCounter >= cmds.size()) {
            return null;
        }

        Command currCom = cmds.get(comCounter);
        String c = currCom.execute(frog);

        if(c != null) {
            return c;
        }

        while(c == null) {
            ++this.comCounter;
            if(comCounter >= cmds.size()) {
                return null;
            }

            currCom = cmds.get(comCounter);
            c = currCom.execute(frog);
        }

        return c;

        //return index < this.commands.size()? this.commands.get(index) : null;
    };

    public void stopCode() {
        this.cmds.clear();
    }
}
