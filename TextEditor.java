package editor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    private JTextField searchInput;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JButton saveBtn;
    private JButton openBtn;
    private JButton startSearchBtn;
    private JButton nextMatchBtn;
    private JButton previousMatchBtn;
    private JCheckBox useRegEx;
    private JFileChooser fileChooser;
    private ArrayDeque<Match> matches;
    private ArrayDeque<Match> initSearch;

    public TextEditor() {
        initUI();
    }

    private void initUI() {
        makeMainWindow();
        makeSaveLoadPanel();
        makeMenuBar();
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        this.setTitle("Text Editor");
    }

    private void forceSize(JComponent comp, int width, int height) {
        Dimension d = new Dimension(width, height);
        comp.setMinimumSize(d);
        comp.setPreferredSize(d);
        comp.setMaximumSize(d);
        comp.setSize(d);
    }

    private void setMarginBorder(JComponent comp, int aTop,
                                int aBottom, int aLeft, int aRight) {
        Border border = comp.getBorder();
        Border marginBorder = BorderFactory.createEmptyBorder(aTop, aLeft, aBottom, aRight);
        if (border == null) {
            comp.setBorder(marginBorder);
        } else {
            comp.setBorder(BorderFactory
                    .createCompoundBorder(border, marginBorder));
        }
    }

    private void makeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e->
                SwingUtilities.invokeLater(new SaveCommand(this)));
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e->
                SwingUtilities.invokeLater(new LoadCommand(this)));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.setName("MenuFile");
        openItem.setName("MenuOpen");
        saveItem.setName("MenuSave");
        exitItem.setName("MenuExit");
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setMnemonic(KeyEvent.VK_S);
        searchMenu.setName("MenuSearch");
        JMenuItem startSearch = new JMenuItem("Start Search");
        startSearch.setName("MenuStartSearch");
        startSearch.addActionListener(e->
                SwingUtilities.invokeLater(
                        new StartSearchCommand(this)));
        JMenuItem nextMatch = new JMenuItem("Next Match");
        nextMatch.setName("MenuNextMatch");
        nextMatch.addActionListener(e->
                SwingUtilities.invokeLater(
                        new ContinueSearchCommand(this, SearchOption.FORWARD)));
        JMenuItem previousMatch = new JMenuItem("Previous Match");
        previousMatch.setName("MenuPreviousMatch");
        previousMatch.addActionListener(e->
                SwingUtilities.invokeLater(
                        new ContinueSearchCommand(this, SearchOption.BACKWARD)));
        JMenuItem useRegex = new JMenuItem("Use regular expressions");
        useRegex.setName("MenuUseRegExp");
        useRegex.addActionListener(e->this.useRegEx.setSelected(true));
        searchMenu.add(startSearch);
        searchMenu.addSeparator();
        searchMenu.add(previousMatch);
        searchMenu.add(nextMatch);
        searchMenu.addSeparator();
        searchMenu.add(useRegex);
        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        this.setJMenuBar(menuBar);
    }
    private void makeSaveLoadPanel() {
        JPanel saveLoadPanel = new JPanel();
        saveLoadPanel.setSize(300, 60);
        GridBagHandler constraints = new GridBagHandler();
        saveLoadPanel.setLayout(new GridBagLayout());
        constraints.gap(10, "left");
        saveLoadPanel.add(this.openBtn, constraints);
        constraints.nextCell().gap(5);
        saveLoadPanel.add(this.saveBtn, constraints);
        constraints.nextCell().span(2).gap(5).setWeights(1, 0);
        forceSize(this.searchInput, 300, 27);
        saveLoadPanel.add(this.searchInput, constraints);
        constraints.nextCell().nextCell().gap(5);
        saveLoadPanel.add(this.startSearchBtn, constraints);
        constraints.nextCell().gap(5);
        saveLoadPanel.add(this.previousMatchBtn, constraints);
        constraints.nextCell().gap(5);
        saveLoadPanel.add(this.nextMatchBtn, constraints);
        constraints.nextCell().gap(10);
        saveLoadPanel.add(this.useRegEx, constraints);
        this.add(saveLoadPanel, BorderLayout.PAGE_START);
        this.saveBtn.addActionListener(e->
                SwingUtilities.invokeLater(new SaveCommand(this)));
        this.openBtn.addActionListener(e->
                SwingUtilities.invokeLater(new LoadCommand(this)));
        this.startSearchBtn
                .addActionListener(e->
                SwingUtilities.invokeLater(
                        new StartSearchCommand(this)));
        this.nextMatchBtn
                .addActionListener(e->
                        SwingUtilities.invokeLater(
                                new ContinueSearchCommand(this, SearchOption.FORWARD)));
        this.previousMatchBtn
                .addActionListener(e->
                        SwingUtilities.invokeLater(
                                new ContinueSearchCommand(this, SearchOption.BACKWARD)));
    }

    private void makeMainWindow() {
        this.textArea = new JTextArea();
        this.textArea.setName("TextArea");
        this.scrollPane = new JScrollPane(this.textArea);
        this.textArea.setBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED));
        setMarginBorder(this.scrollPane, 10, 10, 10, 10);
        this.scrollPane.setName("ScrollPane");
        this.saveBtn = new JButton(new ImageIcon("saveIcon2.jpg"));
        this.saveBtn.setName("SaveButton");
        this.openBtn = new JButton(new ImageIcon("openDoc2.jpg"));
        this.openBtn.setName("OpenButton");
        this.searchInput = new JTextField(100);
        this.searchInput.setName("SearchField");
        this.startSearchBtn = new JButton(new ImageIcon("startSearch.png"));
        this.startSearchBtn.setName("StartSearchButton");
        this.previousMatchBtn = new JButton(new ImageIcon("leftArrow.png"));
        this.previousMatchBtn.setName("PreviousMatchButton");
        this.nextMatchBtn = new JButton(new ImageIcon("rightArrow.png"));
        this.nextMatchBtn.setName("NextMatchButton");
        this.fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        this.add(this.fileChooser);
        this.fileChooser.setVisible(false);
        this.fileChooser.setName("FileChooser");
        forceSize(this.fileChooser, 500,300);
        this.useRegEx = new JCheckBox("Use regex");
        this.useRegEx.setName("UseRegExCheckbox");
        this.matches = new ArrayDeque<>();
        this.initSearch = new ArrayDeque<>();
    }
    private synchronized String getFileName(Operation operation) {
        String fileName = "";
        int returnValue;
        switch (operation) {
            case OPEN:
                fileChooser.setVisible(true);
                returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileName = fileChooser.getSelectedFile().getAbsolutePath();
                } else {
                    fileName = "";
                    //JOptionPane.showMessageDialog(this, "Can't open");
                }
                break;
            case SAVE:
                fileChooser.setVisible(true);
                returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileName = fileChooser.getSelectedFile().getAbsolutePath();
                } else {
                    fileName = "";
                    //JOptionPane.showMessageDialog(this, "Can't save");
                }
                break;
        }
        return fileName;
    }
    public String getText() {
        return this.textArea.getText();
    }
    public boolean getRegexPosition() {
        return this.useRegEx.isSelected();
    }
    public String getSearchPattern() {
        return this.searchInput.getText();
    }
    private void setText(String text) {
        this.textArea.setText(text);
    }
    public ArrayDeque<Match> getMatches() {
        return this.matches;
    }
    public void saveSearchResults() {
        this.initSearch = this.matches;
    }
    public synchronized void saveToFile() {
        String fileName = this.getFileName(Operation.SAVE);
        File file = new File(fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(this.getText().toCharArray());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("Can't save to file!");
        }
    }
    public synchronized void loadFromFile() {
        String fileName = this.getFileName(Operation.OPEN);
        try {
            String text = new String(Files.readAllBytes(Paths.get(fileName)));
            this.setText(text);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("Can't load from file!");
            this.setText("");
        }
    }
    public enum Operation {
        SAVE, OPEN
    }
    public enum SearchOption {
        START, FORWARD, BACKWARD
    }
    protected void startSearch(SearchOption option) {
        int index = 0;
        int wordLength = 0;
        switch(option) {
            case FORWARD:
                if (this.textArea.getCaretPosition()
                        == this.matches.getLast().getIndex()
                        +this.matches.getLast().getLength()) {
                    this.matches.addFirst(this.matches.removeLast());
                }
                index = this.matches.getLast().getIndex();
                wordLength = this.matches.getLast().getLength();
                this.matches.addFirst(this.matches.removeLast());
                break;
            case BACKWARD:
                if (this.textArea.getCaretPosition()
                        == this.matches.getFirst().getIndex()
                        +this.matches.getFirst().getLength()) {
                    this.matches.addLast(this.matches.removeFirst());
                }
                index = this.matches.getFirst().getIndex();
                wordLength = this.matches.getFirst().getLength();
                this.matches.addLast(this.matches.removeFirst());
                break;
            case START:
                index = this.initSearch.getLast().getIndex();
                wordLength = this.initSearch.getLast().getLength();
        }
        this.textArea.setCaretPosition(index + wordLength);
        this.textArea.select(index, index + wordLength);
        this.textArea.grabFocus();
    }

    public class StartSearch extends SwingWorker<Integer, String> {

        private SearchOption option;
        @Override
        protected Integer doInBackground() throws Exception {
            startSearch(this.option);
            return 0;
        }

        public void setOption(SearchOption option) {
           this.option = option;
        }
    }
}

abstract class Search extends SwingWorker<Integer, String> {

    protected String pattern;
    protected String text;
    protected ArrayDeque<Match> matches;

    abstract protected void findAll();

    public Search(String pattern, String text, ArrayDeque<Match> matches) {
        this.pattern = pattern;
        this.text = text;
        this.matches = matches;
    }

    /*public ArrayDeque<Match> getMatches() {
        return this.matches;
    }

    public void setPattern(String newPattern) {
        this.pattern = newPattern;
    }

    public void setText(String newText) {
        this.text = newText;
    }*/
    @Override
    protected Integer doInBackground() throws Exception {
        this.findAll();
        return 0;
    }
}

class SimpleSearch extends Search {

    public SimpleSearch(String pattern, String text, ArrayDeque<Match> matches) {
        super(pattern, text, matches);
    }

    @Override
    protected void findAll() {
        int index;
        int begin = 0;
        this.matches.clear();
        do {
            index = this.text.indexOf(this.pattern, begin);
            if (index < 0) break;
            begin = index + this.pattern.length();
            this.matches.push(new Match(index, this.pattern.length()));
        } while(index >= 0);
    }
}

class RegexSearch extends Search {

    public RegexSearch(String pattern, String text, ArrayDeque<Match> matches) {
        super(pattern, text, matches);
    }

    @Override
    public void findAll() {
        Pattern regExPattern = Pattern.compile(this.pattern);
        Matcher matcher = regExPattern.matcher(this.text);
        this.matches.clear();
        while (matcher.find()) {
            this.matches.push(new Match(matcher.start(),
                                matcher.end()-matcher.start()));
        }
    }
}

class Match {
    private final int index;
    private final int length;
    public Match(int index, int length) {
        this.index = index;
        this.length = length;
    }
    public int getIndex() {
        return this.index;
    }
    public int getLength() {
        return this.length;
    }
}