package au.com.sensis.slicktest;

public abstract class Piece {

    public static final int LAYOUT_SIZE = 4;

    protected Block[][] layout;
    protected Coord coord;

    protected int height;
    protected int width;

    private int currentRotation = 0;

    public Piece(Coord coord) {
        this.coord = coord;
        this.layout = new Block[LAYOUT_SIZE][LAYOUT_SIZE];
    }

    protected abstract Rotation[] getRotations();

    // TODO: push away from right edge if rotating near it
    public void tryRotate(Block[][] tank) {
        int nextRotation = currentRotation < getRotations().length - 1 ? currentRotation + 1 : 0;
        Block[][] nextLayout = getRotations()[nextRotation].getLayout();

        for (int x = 0; x < nextLayout.length; x++) {
            for (int y = 0; y < nextLayout[0].length; y++) {
                if (nextLayout[x][y] != null) {
                    int tankXCoord = coord.getX() + x;
                    int tankYCoord = coord.getY() + y;
                    if (tankXCoord >= Model.TANK_WIDTH || tankYCoord >= Model.TANK_HEIGHT - 1 || tank[tankXCoord][tankYCoord] != null) {
                        return; // Don't rotate as this would lead to escaping the tank or overlapping existings blocks
                    }
                }
            }
        }

        this.currentRotation = nextRotation;
        this.layout = getRotations()[currentRotation].getLayout();
        this.height = getRotations()[currentRotation].getHeight();
        this.width = getRotations()[currentRotation].getWidth();
    }
    
    public void tryMoveRight(final Block[][] tank) {
        //TODO: consider case where piece isn't lined up to left of layout
        // Don't escape the tank
        if (coord.getX() >= (Model.TANK_WIDTH - width)) {
            return;
        }

        // Make sure there is space to move into
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = coord.getX() + x;
                    int tankYCoord = coord.getY() + y;
                    if (tank[tankXCoord + 1][tankYCoord] != null) {
                        return;
                    }
                }
            }
        }
        coord = new Coord(getCoord().getX() + 1, getCoord().getY());
    }
    
    public void tryMoveLeft(final Block[][] tank) {
        // Don't escape the tank
        if (coord.getX() <= 0) {
            return;
        }

        // Make sure there is space to move into
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = coord.getX() + x;
                    int tankYCoord = coord.getY() + y;
                    if (tank[tankXCoord - 1][tankYCoord] != null) {
                        return;
                    }
                }
            }
        }

        coord = new Coord(coord.getX() - 1, coord.getY());
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord newCoord) {
        this.coord = newCoord;
    }

    public Block[][] getLayout() {
        return layout;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
