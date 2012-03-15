package au.com.sensis.slicktest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.Input;

public class Model {

    public static final int TANK_HEIGHT = 20;
    public static final int TANK_WIDTH = 10;

    private static final int NUMBER_OF_PIECE_TYPES = 7;
    // time in milliseconds before a command will be repeated if the key is held down
    private static final int REPEAT_LENGTH = 100; 

    private int timeAccumulator = 0;
    private int tickLength = 1000;
    private int ticks = 0;

    private Piece currentPiece;
    private Block[][] tank;
    private int score;
    private Random random;
    
    private final Map<Integer, Integer> repeatAccumulators = new HashMap<Integer, Integer>();
    
    private final int[] repeatableKeys = {Input.KEY_LEFT, Input.KEY_RIGHT, Input.KEY_DOWN};

    public Model() {
        random = new Random();
        tank = new Block[TANK_WIDTH][TANK_HEIGHT];
        generateNextPiece();
        
        for(int key : repeatableKeys) {
            repeatAccumulators.put(key, 0);
        }
    }

    public void update(int delta, Input input) {
        boolean tickPassed = false;
        timeAccumulator += delta;
        for (Integer key : repeatAccumulators.keySet()) {
            repeatAccumulators.put(key, repeatAccumulators.get(key) + delta);
        }
        if (timeAccumulator > tickLength) {
            tickPassed = true;
            ticks += 1;
            timeAccumulator -= tickLength;
        }

        if (input.isKeyPressed(Input.KEY_LEFT)) {
            repeatAccumulators.put(Input.KEY_LEFT, -REPEAT_LENGTH);
            currentPiece.tryMoveLeft(tank);
        } else if (input.isKeyDown(Input.KEY_LEFT) 
                && repeatAccumulators.get(Input.KEY_LEFT) >= REPEAT_LENGTH) {
            repeatAccumulators.put(Input.KEY_LEFT, 0);
            currentPiece.tryMoveLeft(tank);
        }
        if (input.isKeyPressed(Input.KEY_RIGHT)) {
            repeatAccumulators.put(Input.KEY_RIGHT, -REPEAT_LENGTH);
            currentPiece.tryMoveRight(tank);
        } else if (input.isKeyDown(Input.KEY_RIGHT) 
                && repeatAccumulators.get(Input.KEY_RIGHT) >= REPEAT_LENGTH) {
            repeatAccumulators.put(Input.KEY_RIGHT, 0);
            currentPiece.tryMoveRight(tank);
        }
        if (input.isKeyPressed(Input.KEY_DOWN)) {
            repeatAccumulators.put(Input.KEY_DOWN, -REPEAT_LENGTH);
            if (pieceCanBeDropped()) {
                dropPiece();
            }
        } else if (input.isKeyDown(Input.KEY_DOWN) 
                && repeatAccumulators.get(Input.KEY_DOWN) >= REPEAT_LENGTH) {
            repeatAccumulators.put(Input.KEY_DOWN, 0);
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
        // NB: iterating from bottom to top
        for (int y = TANK_HEIGHT - 1; y >= 0; y--) {
            if (rowsToRemove.contains(y)) {
                for (int x = 0; x < TANK_WIDTH; x++) {
                    tank[x][y] = null;
                }
            } else {
                int numberOfRowsRemovedBelow = getNumberOfRowsRemovedBelow(y, rowsToRemove);
                if (numberOfRowsRemovedBelow > 0) {
                    for (int x = 0; x < TANK_WIDTH; x++) {
                        // move this row down by a number of rows equal to those removed below it
                        tank[x][y + numberOfRowsRemovedBelow] = tank[x][y];
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

        Coord startLocation = new Coord(TANK_WIDTH / 2, 0);
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

    private void solidifyPieceIntoTank() {
        Block[][] layout = currentPiece.layout;
        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[0].length; y++) {
                if (layout[x][y] != null) {
                    int tankXCoord = currentPiece.getCoord().getX() + x;
                    int tankYCoord = currentPiece.getCoord().getY() + y;
                    tank[tankXCoord][tankYCoord] = layout[x][y]; // "solidify" block
                }
            }
        }
    }

    private void dropPiece() {
        // drop piece
        Coord newCoord = new Coord(currentPiece.getCoord().getX(), currentPiece.getCoord().getY() + 1);
        currentPiece.setCoord(newCoord);
    }

    private boolean pieceCanBeDropped() {
        // check not at bottom
        if (currentPiece.getCoord().getY() >= (TANK_HEIGHT - currentPiece.getHeight())) {
            return false;
        }

        // check not blocked
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
