package Header;

import Constants.Constants;
import Editor.EditorInterface;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class Header extends HBox implements ConsoleInterface {
    private TextField console;

    public Header(EditorInterface editor) {
        super();
        Button runButton = new Button("Run");

        runButton.setOnAction(e -> {
            editor.runCode();
            this.clearConsole();
        });

        this.setSpacing(Constants.DEFAULT_SPACING);
        Button saveButton = getButton(editor);

        Button loadButton = new Button("Load");

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");

            // Optional: Add extension filters to limit file types
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(new Stage());

            if(file == null) {
                return;
            }

            try {

                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();

                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                    line = bufferedReader.readLine();
                }

                bufferedReader.close();
                editor.setText(stringBuilder.toString());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }


        });


        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> {
            editor.stopCode();
        });

        this.setPadding(Constants.DEFAULT_PADDING);

        this.console = new TextField();
        this.console.setPrefHeight(25);
        this.console.setEditable(false);
        this.console.setText("Frog frog frog!!!");
        this.console.setPrefWidth(300);

        this.getChildren().addAll(runButton, saveButton, loadButton, stopButton, console);
    }

    private static Button getButton(EditorInterface editor) {
        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text files","*.txt"),new FileChooser.ExtensionFilter("All files", "*.*"));
            File file = fileChooser.showSaveDialog(new Stage());

            if(file == null) {
                return;
            }

            String code = editor.getText();
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write(code);
                bufferedWriter.close();

            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });
        return saveButton;
    }

    public void clearConsole() {
        this.console.setText("");
    }

    public void setConsoleText(String text) {
        this.console.setText(text);
    }

}
