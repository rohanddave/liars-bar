public class TableDimensions {
    private final int width;
    private final int height;
    private final int centerWidth;
    private final int centerHeight;

    public TableDimensions() {
        this.width = 60;
        this.height = 20;
        this.centerWidth = 40;
        this.centerHeight = 12;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getCenterWidth() {
        return this.centerWidth;
    }

    public int getCenterHeight() {
        return this.centerHeight;
    }
}
