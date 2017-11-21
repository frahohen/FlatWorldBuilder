/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flatworldbuilder.map;

import flatworldbuilder.batch.Batch;
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
        Locator locator = new Locator();

        int side = new Random().nextInt(4);
        int distance = 0;
        int x = 0;
        int y = new Random().nextInt(height) * size;
        int startY = y;

        if (side == 0) {
            locator.setPosition(x, y);
        }
        if (side == 1) {
            locator.setPosition((width * size) - size, y);
        }
        if (side == 2) {
            locator.setPosition(y, x);
        }
        if (side == 3) {
            locator.setPosition(y, (width * size) - size);
        }

        changeType(locator.getX(), locator.getY(), BATCH_WATER);

        if (side == 0) {
            while (locator.getX() != (width * size) - size) {
                locator = generateByDirection(
                        locator.getX(), locator.getY(),
                        distance, startY,
                        side);
                locator.setPosition(locator.getX(), checkOutOfMap(locator.getY()));
            }
        }
        if (side == 1) {
            while (locator.getX() != 0) {
                locator = generateByDirection(
                        locator.getX(), locator.getY(),
                        distance, startY,
                        side);
                locator.setPosition(locator.getX(), checkOutOfMap(locator.getY()));
            }
        }
        if (side == 2) {
            while (locator.getY() != (width * size) - size) {
                locator = generateByDirection(
                        locator.getY(), locator.getX(),
                        distance, startY,
                        side);
                locator.setPosition(checkOutOfMap(locator.getX()), locator.getY());
            }
        }
        if (side == 3) {
            while (locator.getY() != 0) {
                locator = generateByDirection(
                        locator.getY(), locator.getX(),
                        distance, startY,
                        side);
                locator.setPosition(checkOutOfMap(locator.getX()), locator.getY());
            }
        }

        if (side == 0 || side == 1) {
            distance = locator.getY() - startY;
            while (locator.getY() != startY) {
                locator.setPosition(locator.getX(), interpolateToValue(locator.getY(), distance));
                changeType(locator.getX(), locator.getY(), BATCH_WATER);
            }
        }

        if (side == 2 || side == 3) {
            distance = locator.getX() - startY;
            while (locator.getX() != startY) {
                locator.setPosition(interpolateToValue(locator.getX(), distance), locator.getY());
                changeType(locator.getX(), locator.getY(), BATCH_WATER);
            }
        }
    }

    private int checkOutOfMap(int y) {
        if (y < 0) {
            y = (height * size) - size;
        } else if (y > (height * size) - size) {
            y = 0;
        }
        return y;
    }

    private int interpolateToValue(int y, int distance) {
        if (distance < 0) {
            y += size;
        } else {
            y -= size;
        }
        return y;
    }

    private Locator generateByDirection(
            int x, int y,
            int distance,  int startY,
            int side) {

        double interpolation = ((width / 100.0) * percentageOfInterpolation) * size;
        setRiverBatch(x, y, side);

        // Case right to left or left to right
        if (side == 0 || side == 2) {
            x += size;
        } else {
            x -= size;
        }

        setRiverBatch(x, y, side);

        boolean up = new Random().nextBoolean();
        boolean down = new Random().nextBoolean();

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

        if (x > interpolation && (side == 0 || side == 2)) {
            distance = y - startY;

            y = interpolateToValue(y, distance);
        } else if (x < (width * size) - interpolation && (side == 1 || side == 3)) {
            distance = y - startY;

            y = interpolateToValue(y, distance);
        } else {
            if (up) {
                y -= size;
                down = false;
            }
            if (down) {
                y += size;
                up = false;
            }
        }

        setRiverBatch(x, y, side);

        if (side == 2 || side == 3) {
            return new Locator(y, x);
        } else {
            return new Locator(x, y);
        }
    }

    // Depending on up/down or left/right switch coordinate
    private void setRiverBatch(int x, int y, int side) {  
        if (side == 2 || side == 3) {
            changeType(y, x, BATCH_WATER);
        } else {
            changeType(x, y, BATCH_WATER);
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
