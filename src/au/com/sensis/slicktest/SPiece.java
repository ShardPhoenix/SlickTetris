package au.com.sensis.slicktest;

import org.newdawn.slick.Color;

public class SPiece extends Piece {
    
    private static final Color COLOR = new Color(237, 229, 2);
    
    private static final Rotation[] ROTATIONS;
    
    @Override
	protected Rotation[] getRotations() {
		return ROTATIONS;
	}
    
    static {
        ROTATIONS = new Rotation[2];
        //Inner arrays are the columns (vertical) - read as rotated by 90 degrees clockwise
        ROTATIONS[0] = new Rotation(new Block[][] {
                {new Block(COLOR), new Block(COLOR), null, null},
                {null,             new Block(COLOR), new Block(COLOR), null},
                {null, null, null, null},
                {null, null, null, null},
        }, 3, 2);
        ROTATIONS[1] = new Rotation(new Block[][] {
                {null,             new Block(COLOR), null, null},
                {new Block(COLOR), new Block(COLOR), null, null},
                {new Block(COLOR),        null,        null, null},
                {null, null, null, null},
        }, 2, 3);
    }
    
    public SPiece(final Coord coord) {
        super(coord);
        
        this.layout = ROTATIONS[0].getLayout();
        this.height = ROTATIONS[0].getHeight();
        this.width = ROTATIONS[0].getWidth();
    }

}
