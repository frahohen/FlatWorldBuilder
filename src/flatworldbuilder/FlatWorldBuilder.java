/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flatworldbuilder;

import flatworldbuilder.map.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

/**
 *
 * @author -HONOR-
 */
public class FlatWorldBuilder extends Application {
    
    public final String FLAT_WORLD_BUILDER = "Flat World Builder";
    
    private ScrollPane scrollPane;
    private Map map;
    
    @Override
    public void start(Stage primaryStage) {
        
        scrollPane = new ScrollPane();
        map = new Map(32,32, 1);
        map.generate();
        
        scrollPane.setContent(map.getMapPane());
        
        Scene scene = new Scene(scrollPane, 515, 515);
        
        primaryStage.setTitle(FLAT_WORLD_BUILDER);
        primaryStage.setScene(scene);
        primaryStage.show();
    } 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
