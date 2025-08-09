public class PlayerPosition {
    private final TableSide side;
    private final int position;
    private final int x,y;

    public PlayerPosition(TableSide side, int position, int x, int y) {
        this.side = side;
        this.position = position;
        this.x = x;
        this.y = y;
    }

    public TableSide getSide() {
        return side;
    }

    public int getPosition() {
        return position;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
