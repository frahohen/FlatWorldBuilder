/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flatworldbuilder.map;

import flatworldbuilder.batch.Batch;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.layout.Pane;

/**
 *
 * @author frahohen
 */
public class Map {

    private ArrayList<Batch> batchList;
    private Pane mapPane;

    private int width;
    private int height;
    private int size;

    private int cityCount;
    private int forestCount;
    private int grassCount;
    private int riverCount;
    private int connectionCount;

    private double percentageOfInterpolation;
    private int x;
    private int y;

    private ArrayList<Integer> forbiddenTypeList;

    public final String BATCH_GRASS = "BATCH_GRASS";
    public final String BATCH_FOREST = "BATCH_FOREST";
    public final String BATCH_CITY = "BATCH_CITY";
    public final String BATCH_WATER = "BATCH_WATER";
    public final String BATCH_CONNECTION = "BATCH_CONNECTION";

    public Map(int width, int height, int riverCount) {
        this.width = width;
        this.height = height;
        this.mapPane = new Pane();
        this.batchList = new ArrayList<Batch>();
        this.size = 16;
        this.riverCount = riverCount;
        this.forbiddenTypeList = new ArrayList<Integer>();
        this.percentageOfInterpolation = 70.0;
    }

    public void generate() {
        // Init: Generate Land
        generateCountry();

        // Render: Generate River
        for (int river = 0; river < riverCount; river++) {
            generateRiver();
        }

        // Finally: Batches added to Map
        for (int i = 0; i < batchList.size(); i++) {
            mapPane.getChildren().add(batchList.get(i).getBatch());
        }
    }

    private void generateRiver() {
        // TODO: might be only needed in method
        boolean up = false;
        boolean left = false;
        boolean down = false;
        
        Point point = new Point(); // TODO: create indipendent Class with better name

        int side = new Random().nextInt(4);

        x = 0; // TODO: might not be needed public anymore
        int startY = 0; // TODO: maybe can be in method
        int distance = 0; // TODO: can be in method
        double interpolationX = 0; // TODO: can be in method

        // TODO: might not be needed public anymore
        y = new Random().nextInt(height);
        y *= size;
        // TODO: might be only needed in method
        startY = y;
        
        interpolationX = ((width / 100.0) * percentageOfInterpolation) * size; // TODO: can be in method
        left = true;

        if (side == 0) {
            point.setLocation(x, y);
        }
        if (side == 1) {
            point.setLocation((width * size) - size, y);
        }
        if (side == 2) {
            point.setLocation(y, x);
        }
        if (side == 3) {
            point.setLocation(y, (width * size) - size);
        }

        // Depending on up/down or left/right switch coordinate
        changeType((int) point.getX(), (int) point.getY(), BATCH_WATER);
        
        if (side == 0) {
            while (point.getX() != (width * size) - size) {
                point = generateByDirection((int) point.getX(), (int) point.getY(),
                        left, up, down,
                        distance, interpolationX, startY,
                        side);
                point.setLocation(point.getX(), checkOutOfMap((int)point.getY()));
            }
        }
        if (side == 1) {
            while (point.getX() != 0) {
                point = generateByDirection((int) point.getX(), (int) point.getY(),
                        left, up, down,
                        distance, interpolationX, startY,
                        side);
                point.setLocation(point.getX(), checkOutOfMap((int)point.getY()));
            }
        }
        if (side == 2) {
            while (point.getY() != (width * size) - size) {
                point = generateByDirection((int) point.getY(), (int) point.getX(),
                        left, up, down,
                        distance, interpolationX, startY,
                        side);
                point.setLocation(checkOutOfMap((int)point.getX()),(int)point.getY());
            }
        }
        if (side == 3) {
            while (point.getY() != 0) {
                point = generateByDirection((int) point.getY(), (int) point.getX(),
                        left, up, down,
                        distance, interpolationX, startY,
                        side);
                point.setLocation(checkOutOfMap((int)point.getX()),(int)point.getY());
            }
        }

        if (side == 0 || side == 1) {
            distance = (int) point.getY() - startY;
            while ((int) point.getY() != startY) {
                point.setLocation(point.getX(), interpolateToValue((int) point.getY(), distance));
                changeType((int) point.getX(), (int) point.getY(), BATCH_WATER);
            }
        }

        if(side == 2 || side == 3){
            distance = (int)point.getX() - startY;
            while ((int)point.getX() != startY) {
                point.setLocation(interpolateToValue((int)point.getX(),distance), point.getY());
                changeType((int)point.getX(), (int)point.getY(), BATCH_WATER);
            }
        }
    }

    private int checkOutOfMap(int y){
        if (y < 0) {
            y = (height * size) - size;
        }

        if (y > (height * size) - size) {
            y = 0;
        }
        
        return y; // TODO: return earlier
    }
    
    private int interpolateToValue(int y, int distance) {
        if (distance < 0) {
            y += size;
        } else {
            y -= size;
        }

        return y; // TODO: maybe return earlier
    }

    private Point generateByDirection(
            int x, int y,
            boolean left, boolean up, boolean down,
            int distance, double interpolationX, int startY,
            int side) {

        // TODO: this is used multiple times; convert it to one method
        
        if (side == 2 || side == 3) {
            changeType(y, x, BATCH_WATER);
        } else {
            changeType(x, y, BATCH_WATER);
        }

        // Case right to left or left to right
        if (side == 0 || side == 2) {
            x += size;
        } else {
            x -= size;
        }

        if (left) { // TODO: condition might be not needed if always going that way
            // Depending on up/down or left/right switch coordinate
            if (side == 2 || side == 3) {
                changeType(y, x, BATCH_WATER);
            } else {
                changeType(x, y, BATCH_WATER);
            }

            up = new Random().nextBoolean();
            down = new Random().nextBoolean();

            int chooseBoolean = new Random().nextInt(2);

            while (up == false && down == false) {
                switch (chooseBoolean) {
                    case 0:
                        up = new Random().nextBoolean();
                        break;
                    case 1:
                        down = new Random().nextBoolean();
                        break;
                }
            }
            left = false;
        }

        if (x > interpolationX && (side == 0 || side == 2)) {
            distance = y - startY;

            y = interpolateToValue(y,distance);
            left = true;
        }else if (x < (width*size) - interpolationX && (side == 1 || side == 3)) {
            distance = y - startY;

            y = interpolateToValue(y,distance);
            left = true;
        } else {
            if (up) {
                y -= size;

                left = true;
                down = false;
            }
            if (down) {
                y += size;

                left = true;
                up = false;
            }
        }

        // Depending on up/down or left/right switch coordinate
        if (side == 2 || side == 3) {
            changeType(y, x, BATCH_WATER);
        } else {
            changeType(x, y, BATCH_WATER);
        }

        if (side == 2 || side == 3) {
            return new Point(y, x);
        } else {
            return new Point(x, y);
        }
    }

    private void changeType(int x, int y, String type) {

        foundBreak:
        for (int i = 0; i < batchList.size(); i++) {
            if (batchList.get(i).getBatch().getX() == x && batchList.get(i).getBatch().getY() == y) {
                batchList.get(i).setType(type);
                break foundBreak;
            }
        }
    }

    private void generateCountry() {
        for (int y = 0; y < height * size; y += size) {
            for (int x = 0; x < width * size; x += size) {
                // This area should not have any city (or connection == ?)
                if (y == 0 || x == 0 || x == (width * size) - size || y == (height * size) - size) {
                    forbiddenTypeList.add(2);
                    forbiddenTypeList.add(3);
                    forbiddenTypeList.add(4);
                    batchList.add(
                            new Batch(x, y, size, randomBatch(forbiddenTypeList))
                    );
                    forbiddenTypeList = new ArrayList<>();
                } else {
                    forbiddenTypeList.add(2);
                    forbiddenTypeList.add(3);
                    forbiddenTypeList.add(4);
                    batchList.add(
                            new Batch(x, y, size, randomBatch(forbiddenTypeList))
                    );
                    forbiddenTypeList = new ArrayList<>();
                }

            }
        }
    }

    // Percentage of batches in the world => must be 100% in total
    private int percentageRandom(int grass, int forest, int city, int water, int connection) {
        int random = new Random().nextInt(100);

        if (random < grass) { // 60%
            return 0;
        } else if (random < grass + forest) { // 19%
            return 1;
        } else if (random < grass + forest + city) { // 14%
            return 2;
        } else if (random < grass + forest + city + water) { // 14%
            return 3;
        } else { // 7%
            return 4;
        }
    }

    // allowed types are given over to this method so only these are adjusted if
    // there are types that should not appear on the screen
    private String randomBatch(ArrayList<Integer> type) {
        int random = percentageRandom(55, 25, 10, 5, 5);

        if (type.size() != 0) {
            boolean isAdjusted = false;

            while (isAdjusted == false) {
                random = percentageRandom(55, 25, 10, 5, 5);

                adjustBreak:
                for (int i = 0; i < type.size(); i++) {
                    if (random == type.get(i)) {
                        isAdjusted = false;
                        break adjustBreak;
                    } else {
                        isAdjusted = true;
                    }
                }
            }
        }

        switch (random) {
            case 0:
                return BATCH_GRASS;
            case 1:
                return BATCH_FOREST;
            case 2:
                return BATCH_CITY;
            case 3:
                return BATCH_WATER;
            case 4:
                return BATCH_CONNECTION;
            default:
                return BATCH_GRASS;
        }
    }

    public Pane getMapPane() {
        return mapPane;
    }

    public void setMapPane(Pane mapPane) {
        this.mapPane = mapPane;
    }
}
