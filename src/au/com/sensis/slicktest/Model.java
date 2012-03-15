package au.com.sensis.slicktest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Input;

public class Model {
    
    private static final int NUMBER_OF_PIECE_TYPES = 7;
    public static final int TANK_HEIGHT = 20;
    public static final int TANK_WIDTH = 10;
    private static final int REPEAT_LENGTH = 100;  //time in milliseconds before a command will be repeated if the key is held down
    
    private int timeAccumulator = 0;
    private int tickLength = 1000;
    private int ticks = 0;
    
    private Piece currentPiece;
    private Block[][] tank;
    private int score;
    private Random random;
    private int repeatAccumulator;
    
    public Model() {
        random = new Random();
        tank = new Block[TANK_WIDTH][TANK_HEIGHT];
        generateNextPiece();
    }
    
    public void update(int delta, Input input) {
        boolean tickPassed = false;
        timeAccumulator += delta;
        repeatAccumulator += delta;
        if (timeAccumulator > tickLength) {
            tickPassed = true;
            ticks += 1;
            timeAccumulator -= tickLength;
        }
        
        if (input.isKeyPressed(Input.KEY_LEFT)) {
                if (canMoveLeft()) {
                    currentPiece.setCoord(new Coord(currentPiece.getCoord().getX() - 1, currentPiece.getCoord().getY()));
                }
        }
        if (input.isKeyPressed(Input.KEY_RIGHT)) {
            if (canMoveRight()) {
                currentPiece.setCoord(new Coord(currentPiece.getCoord().getX() + 1, currentPiece.getCoord().getY()));
            }
        }
        if (input.isKeyPressed(Input.KEY_DOWN)) {
            repeatAccumulator = -REPEAT_LENGTH; //cool down for a whole cycle
            if (pieceCanBeDropped()) {
                dropPiece();
            }
        } else if (input.isKeyDown(Input.KEY_DOWN) && repeatAccumulator >= REPEAT_LENGTH) {
            repeatAccumulator = 0;
            if (pieceCanBeDropped()) {
                dropPiece();
            }
        }
        if (input.isKeyPressed(Input.KEY_UP)) {
            currentPiece.tryRotate(tank);
        }
        if (input.isKeyPressed(Input.KEY_SPACE)) {
            currentPiece.tryRotate(tank);
        }

        
        if (tickPassed) {
            if (pieceCanBeDropped()) {
                dropPiece();
            } else {
                solidifyPieceIntoTank();
                clearLines();
                generateNextPiece();
            }
        }
        
    }

    private void clearLines() {
        List<Integer> rowsToRemove = new ArrayList<Integer>();
        for (int y = 0; y < TANK_HEIGHT; y++) {
            boolean rowFull = true;
            for (int x = 0; x < TANK_WIDTH; x++) {
                if (tank[x][y] == null) {
                    rowFull = false;
                    break;
                }
            }
            if (rowFull) {
                rowsToRemove.add(y);
            }
        }
        
        score += rowsToRemove.size() * rowsToRemove.size();
        
        removeRowsAndDrop(rowsToRemove);
    }

    private void removeRowsAndDrop(List<Integer> rowsToRemove) {
        for (int y = TANK_HEIGHT - 1; y >= 0; y--) { // NB: iterating from bottom to top
            for (int x = 0; x < TANK_WIDTH; x++) {
                if (rowsToRemove.contains(y)) {
                    tank[x][y] = null;
                } else {
                    int numberOfRowsRemovedBelow = getNumberOfRowsRemovedBelow(y, rowsToRemove);
                    if (numberOfRowsRemovedBelow > 0) {
                        tank[x][y + numberOfRowsRemovedBelow] = tank[x][y]; // copy this row down to the lowest cleared location
                        tank[x][y] = null;
                    }
                }
            }
        }
    }

    private int getNumberOfRowsRemovedBelow(int row, List<Integer> rowsToRemove) {
        int number = 0;
        for (Integer removedRow : rowsToRemove) {
            if (removedRow > row) { // Bigger number is further down
                number++;
            }
        }
        return number;
    }

    private void generateNextPiece() {
        int pieceType = random.nextInt(NUMBER_OF_PIECE_TYPES);
        
        // generating numbers ok but not piece?!?!?
        
        Coord startLocation = new Coord(TANK_WIDTH/2, 0);
        switch (pieceType) {
        case 0:
            currentPiece = new SquarePiece(startLocation);
            break;
        case 1:
            currentPiece = new LPiece(startLocation);
            break;
        case 2:
            currentPiece = new BackwardsLPiece(startLocation);
            break;
        case 3:
            currentPiece = new LinePiece(startLocation);
            break;
        case 4:
            currentPiece = new TPiece(startLocation);
            break;
        case 5:
            currentPiece = new ZPiece(startLocation);
            break;
        case 6:
            currentPiece = new SPiece(startLocation);
            break;
        default:
            throw new RuntimeException("Unknown pieceType: " + pieceType);
        }
    }

    //TODO: put this stuff into piece eg tryMoveRight()
    private boolean canMoveRight() {
        if (currentPiece.getCoord().getX() >= (TANK_WIDTH - currentPiece.getWidth())) {
            return false;
        }
        
        Block[][] layout = currentPiece.getLayout();
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = currentPiece.getCoord().getX() + x;
                    int tankYCoord = currentPiece.getCoord().getY() + y;
                    if (tank[tankXCoord + 1][tankYCoord] != null) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    private boolean canMoveLeft() {
        if (currentPiece.getCoord().getX() <= 0) {
            return false;
        }
        
        Block[][] layout = currentPiece.getLayout();
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = currentPiece.getCoord().getX() + x;
                    int tankYCoord = currentPiece.getCoord().getY() + y;
                    if (tank[tankXCoord - 1][tankYCoord] != null) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    private void solidifyPieceIntoTank() {
        Block[][] layout = currentPiece.layout;
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = currentPiece.getCoord().getX() + x;
                    int tankYCoord = currentPiece.getCoord().getY() + y;
                    tank[tankXCoord][tankYCoord] = layout[x][y]; //"solidify" block
                }
            }
        }
    }

    private void dropPiece() {
        //drop piece
        Coord newCoord = new Coord(currentPiece.getCoord().getX(), currentPiece.getCoord().getY() + 1);
        currentPiece.setCoord(newCoord);
    }

    private boolean pieceCanBeDropped() {
        //check not at bottom
        if (currentPiece.getCoord().getY() >= (TANK_HEIGHT - currentPiece.getHeight())) {
            return false;
        }
        
        //check not blocked
        Block[][] layout = currentPiece.getLayout();
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = currentPiece.getCoord().getX() + x;
                    int tankYCoord = currentPiece.getCoord().getY() + y;
                    if (tank[tankXCoord][tankYCoord + 1] != null) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public Piece getCurrentPiece() {
        return currentPiece;
    }
    
    public int getTicks() {
        return ticks;
    }

    public Block[][] getTank() {
        return tank;
    }
    
    public int getScore() {
        return score;
    }
}
