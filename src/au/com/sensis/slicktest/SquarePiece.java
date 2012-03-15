package au.com.sensis.slicktest;

import org.newdawn.slick.Color;

public class SquarePiece extends Piece {
    
    private static final Color COLOR = new Color(97, 5, 255);
    
    @Override
	protected Rotation[] getRotations() {
		return null;
	}

    public SquarePiece(Coord coord) {
        super(coord);
        layout[0][0] = new Block(COLOR);
        layout[0][1] = new Block(COLOR);
        layout[1][0] = new Block(COLOR);
        layout[1][1] = new Block(COLOR);
        height = 2;
        width = 2;
    }

    @Override
    public void tryRotate(Block[][] tank) {
        //do nothing, piece does not rotate
    }
    
}
