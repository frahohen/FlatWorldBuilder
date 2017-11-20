/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flatworldbuilder.batch;

import flatworldbuilder.model.Model;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author frahohen
 */
public class Batch {
    private ArrayList<Model> modelList;
    
    private int width;
    private int height;
    private Rectangle rectangle;
    
    public final Color BATCH_GRASS = Color.GREEN;
    public final Color BATCH_FOREST = Color.BROWN;
    public final Color BATCH_CITY = Color.GREY;
    public final Color BATCH_WATER = Color.BLUE;
    public final Color BATCH_CONNECTION = Color.YELLOW;

    public Batch(int x, int y, int size, String type) {
        this.height = size;
        this.width = size;
        this.rectangle = new Rectangle(x, y, size, size);
        
        setType(type);
    }
    
    public void setType(String type){
        switch (type) {
            case "BATCH_GRASS":
                rectangle.setFill(BATCH_GRASS);
                break;
            case "BATCH_FOREST":
                rectangle.setFill(BATCH_FOREST);
                break;
            case "BATCH_CITY":
                rectangle.setFill(BATCH_CITY);
                break;
            case "BATCH_WATER":
                rectangle.setFill(BATCH_WATER);
                break;
            case "BATCH_CONNECTION":
                rectangle.setFill(BATCH_CONNECTION);
                break;
        }
        rectangle.setStroke(Color.BLACK);
    }
    
    public Rectangle getBatch(){
        return this.rectangle;
    }
}
