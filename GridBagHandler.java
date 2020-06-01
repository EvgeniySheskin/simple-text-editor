package editor;

import java.awt.*;

public class GridBagHandler extends GridBagConstraints {

    public GridBagHandler() {
        super();
        this.gridx = 0;
        this.gridy = 0;
    }
    public GridBagHandler nextCell() {
        this.gridx++;
        this.resetCellSettings();
        return this;
    }
    public GridBagHandler nextRow() {
        this.gridy++;
        this.gridx = 0;
        this.resetCellSettings();
        return this;
    }
    public GridBagHandler cell(int gridy, int gridx) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.resetCellSettings();
        return this;
    }
    public GridBagHandler span(int numberOfCells) {
        if (numberOfCells == 0) {
            this.gridwidth = GridBagConstraints.REMAINDER;
        } else this.gridwidth = numberOfCells;
        return this;
    }
    public GridBagHandler fillHorizontally() {
        this.fill = GridBagConstraints.HORIZONTAL;
        return this;
    }
    public GridBagHandler gap(int size) {
        this.insets.right = size;
        return this;
    }
    public GridBagHandler gap(int size, String position) {
        switch (position) {
            case "left":
                this.insets.left = size;
                break;
            case "right":
                this.insets.right = size;
                break;
            case "top":
                this.insets.top = size;
                break;
            case "bottom":
                this.insets.bottom = size;
                break;
            case "horizontal":
                this.insets.left = size;
                this.insets.right = size;
                break;
            case "vertical":
                this.insets.top = size;
                this.insets.bottom = size;
                break;
            case "all":
                this.insets.left = size;
                this.insets.right = size;
                this.insets.top = size;
                this.insets.bottom = size;
        }
        return this;
    }
    public GridBagHandler spanY() {
        this.gridheight = GridBagConstraints.REMAINDER;
        return this;
    }
    public GridBagHandler fillBoth() {
        this.fill = GridBagConstraints.BOTH;
        return this;
    }
    public GridBagHandler align(String alignment) {
        switch (alignment) {
            case "left":
                this.anchor = GridBagConstraints.LINE_START;
                break;
            case "right":
                this.anchor = GridBagConstraints.LINE_END;
                break;
            case "top":
                this.anchor = GridBagConstraints.NORTH;
                break;
            case "bottom":
                this.anchor = GridBagConstraints.SOUTH;
                break;
            case "center":
                this.anchor = GridBagConstraints.CENTER;
                break;
            default:
                this.anchor = GridBagConstraints.NORTHEAST;
        }
        return this;
    }

    public GridBagHandler setInsets(int left, int top, int right, int bottom) {
        Insets i = new Insets(top, left, bottom, right);
        this.insets = i;
        return this;
    }
    public GridBagHandler setWeights(float horizontal, float vertical) {
        this.weightx = horizontal;
        this.weighty = vertical;
        return this;
    }
    public GridBagHandler resetCellSettings() {
        this.setInsets(0,0,0,0);
        this.fill = GridBagConstraints.NONE;
        this.weightx = 0;
        this.weighty = 0;
        this.anchor = GridBagConstraints.CENTER;
        this.gridwidth = 1;
        this.gridheight = 1;
        return this;
    }
}


