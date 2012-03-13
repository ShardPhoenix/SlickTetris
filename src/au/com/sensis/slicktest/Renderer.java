package au.com.sensis.slicktest;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Renderer {
    
    private int blockSizeInPx = 20;
    
    public void render(Model model, GameContainer container, Graphics g) throws SlickException {
        //draw tank outline
        g.setColor(Color.white);
        g.drawRect(0, 0, Model.TANK_WIDTH * blockSizeInPx, Model.TANK_HEIGHT * blockSizeInPx);
        
        //draw piece
        Piece piece = model.getCurrentPiece();
        Coord coord = piece.getCoord();
        Block[][] layout = piece.getLayout();
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int xRenderCoord = blockSizeInPx * (coord.getX() + x);
                    int yRenderCoord = blockSizeInPx * (coord.getY() + y);
                    g.setColor(layout[x][y].getColor());
                    g.fill(new Rectangle(xRenderCoord, yRenderCoord, blockSizeInPx, blockSizeInPx));
                }
            }
        }
        
        //draw blocks in tank
        for (int x = 0; x < model.getTank().length; x++) {
            for (int y = 0; y < model.getTank()[0].length; y++) {
                if (model.getTank()[x][y] != null) {
                    int xRenderCoord = blockSizeInPx * x;
                    int yRenderCoord = blockSizeInPx * y;
                    g.setColor(model.getTank()[x][y].getColor());
                    g.fill(new Rectangle(xRenderCoord, yRenderCoord, blockSizeInPx, blockSizeInPx));
                }
            }
        }
        
        g.setColor(Color.white);
        
        g.drawString("Time: " + model.getTicks(), 250, 10);
        
        g.drawString("Score: " + model.getScore(), 250, 40);
        
    }

}
