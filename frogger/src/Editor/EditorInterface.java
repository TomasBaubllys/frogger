package Editor;
import Frog.*;

public interface EditorInterface {
    String getNextCommand(Frog frog);
    void runCode();
    void setText(String text);
    String getText();
    void stopCode();
}
