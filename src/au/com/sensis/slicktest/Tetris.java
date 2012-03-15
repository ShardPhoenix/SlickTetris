package au.com.sensis.slicktest;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Tetris extends BasicGame {
    
    private Model model;
    private Renderer renderer;
    
    public Tetris() {
        super("Tetris");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        model = new Model();
        renderer = new Renderer();
        container.setShowFPS(false);
    }
    
    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        model.update(delta, container.getInput());
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        renderer.render(model, container, g);
    }
    
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Tetris());
        app.start();
    }

}
