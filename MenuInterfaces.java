package editor;

import javax.swing.*;

abstract class Command implements Runnable {

    abstract public void execute();

    @Override
    public void run() {
        this.execute();
    }
}

class SaveCommand extends Command {
    private final TextEditor textEditor;
    public SaveCommand(TextEditor editor) {
        this.textEditor = editor;
    }
    @Override
    public void execute() {
        this.textEditor.saveToFile();
    }
}

class LoadCommand extends Command {
    private final TextEditor textEditor;
    public LoadCommand(TextEditor editor) {
        this.textEditor = editor;
    }
    @Override
    public void execute() {
        this.textEditor.loadFromFile();
    }
}

class StartSearchCommand extends Command {
    private final TextEditor textEditor;

    public StartSearchCommand(TextEditor editor) {
        this.textEditor = editor;
    }
    @Override
    public void execute() {
        Thread thread = null;
        if (this.textEditor.getRegexPosition()) {
            try {
                thread = new Thread(new RegexSearch(this.textEditor.getSearchPattern(),
                        this.textEditor.getText(),
                        this.textEditor.getMatches()));
                SwingUtilities.invokeLater(thread);
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
        } else {
            try {
                thread = new Thread(new SimpleSearch(this.textEditor.getSearchPattern(),
                        this.textEditor.getText(),
                        this.textEditor.getMatches()));
                SwingUtilities.invokeLater(thread);
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
        }
        if (thread == null) return;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.textEditor.saveSearchResults();
        TextEditor.StartSearch searchThread = this.textEditor.new StartSearch();
        searchThread.setOption(TextEditor.SearchOption.START);
        SwingUtilities.invokeLater(searchThread);
    }
}

class ContinueSearchCommand extends Command {

    private TextEditor textEditor;
    private TextEditor.SearchOption option;
    public ContinueSearchCommand(TextEditor editor, TextEditor.SearchOption option) {
        this.textEditor = editor;
        this.option = option;
    }
    @Override
    public void execute() {
        TextEditor.StartSearch searchThread = this.textEditor.new StartSearch();
        searchThread.setOption(this.option);
        SwingUtilities.invokeLater(searchThread);
    }
}
