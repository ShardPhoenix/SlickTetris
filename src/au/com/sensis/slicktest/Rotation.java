package au.com.sensis.slicktest;

public class Rotation {

    private final Block[][] layout;
    private final int height;
    private final int width;

    //TODO: calculate width and height instead of supplying it manually
    public Rotation(Block[][] layout, int height, int length) {
        this.layout = layout;
        this.height = height;
        this.width = length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Block[][] getLayout() {
        return layout;
    }
}
