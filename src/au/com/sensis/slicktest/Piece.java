package au.com.sensis.slicktest;

public abstract class Piece {
    
    public static final int LAYOUT_SIZE = 4;
    
    protected static Rotation[] ROTATIONS = null; //specify in child class
    
    protected Block[][] layout;
    protected Coord coord;

    protected int height;
    protected int width;
    
    private int currentRotation = 0;
    
    public Piece(Coord coord) {
        this.coord = coord;
        this.layout = new Block[LAYOUT_SIZE][LAYOUT_SIZE];
    }
    
    //TODO: push away from right edge if rotating near it
    public void tryRotate(Block[][] tank) {
        int nextRotation = this.currentRotation < ROTATIONS.length - 1 ? this.currentRotation + 1 : 0;
        Block[][] nextLayout = ROTATIONS[nextRotation].getLayout();
        
        for (int x = 0; x < nextLayout.length; x++) {
            for (int y = 0; y < nextLayout[0].length; y++) {
                if (nextLayout[x][y] != null) {
                    int tankXCoord = coord.getX() + x;
                    int tankYCoord = coord.getY() + y;
                    if (tankXCoord >= Model.TANK_WIDTH || tankYCoord >= Model.TANK_HEIGHT - 1
                            || tank[tankXCoord][tankYCoord] != null) {
                        return; //Don't rotate as this would lead to escaping the tank
                    }
                }
            }
        }
        
        this.currentRotation = nextRotation;
        this.layout = ROTATIONS[currentRotation].getLayout();
        this.height = ROTATIONS[currentRotation].getHeight();
        this.width = ROTATIONS[currentRotation].getWidth();
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
