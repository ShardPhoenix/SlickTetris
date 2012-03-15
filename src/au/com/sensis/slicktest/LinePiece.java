package au.com.sensis.slicktest;

import org.newdawn.slick.Color;

public class LinePiece extends Piece {
    
    private static final Color COLOR = new Color(145, 10, 255);
    
    private static final Rotation[] ROTATIONS;
    
    @Override
	protected Rotation[] getRotations() {
		return ROTATIONS;
	}
    
    static {
        ROTATIONS = new Rotation[2];
        //Inner arrays are the columns (vertical) - read as rotated by 90 degrees clockwise
        ROTATIONS[0] = new Rotation(new Block[][] {
                {new Block(COLOR), null, null, null},
                {new Block(COLOR), null, null, null},
                {new Block(COLOR), null, null, null},
                {new Block(COLOR), null, null, null},
        }, 1, 4);
        ROTATIONS[1] = new Rotation(new Block[][] {
                {new Block(COLOR), new Block(COLOR), new Block(COLOR), new Block(COLOR)},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
        }, 4, 1);
    }
    
    public LinePiece(final Coord coord) {
        super(coord);
        
        this.layout = ROTATIONS[0].getLayout();
        this.height = ROTATIONS[0].getHeight();
        this.width = ROTATIONS[0].getWidth();
    }

}
