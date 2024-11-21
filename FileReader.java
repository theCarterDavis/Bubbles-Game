import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class FileReader {
    private static final double TILE_SIZE = 30;
    private static Map<String, TileComponent> tileAtb = new HashMap<>();

    // First, load all attributes and store them as prototypes
    public static Map<String, TileComponent> initializeTilePrototypes(String attributesFile) {
        Map<String, TileComponent> tilePrototypes = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(attributesFile))) {
            while (scanner.hasNext()) {
                try {
                    // Read the basic tile properties
                    String tileName = scanner.next();

                    // Read 8 color values
                    float[] colors = new float[8];
                    for (int i = 0; i < 8; i++) {
                        colors[i] = scanner.nextFloat();
                    }

                    // Read texture and collision
                    String texture = scanner.next();
                    boolean collideable = scanner.nextBoolean();

                    // Create base tile with the basic properties
                    TileComponent baseTile = new BaseTile(tileName, colors, texture, collideable);

                    int atbs = scanner.nextInt();
                    for (int i = 0; i < atbs; i++) {
                        String decoratorStr = scanner.next();
                        TileType decoratorType = TileType.fromString(decoratorStr);


                        if (decoratorType == TileType.VELMOD) {
                            // Read the speed multiplier parameter
                            //System.out.println("Speed tile read in for " + tileName+"    fileread,ln.47");
                            int x = scanner.nextInt();
                            double y = scanner.nextDouble();
                            double r = scanner.nextDouble();
                            baseTile = decoratorType.decorate(baseTile, x,y,r);
                        }
                        else if (decoratorType == TileType.PUSH){
                            int r = scanner.nextInt();
                            double amount = scanner.nextDouble();
                            boolean obd = scanner.next().equalsIgnoreCase("true"); //OnByDefualt
                            baseTile = decoratorType.decorate(baseTile, r,amount,obd);
                        }
                        else if (decoratorType == TileType.PULL){
                            int r = scanner.nextInt();
                            double amount = scanner.nextDouble();
                            boolean obd = scanner.next().equalsIgnoreCase("true"); //OnByDefualt
                            baseTile = decoratorType.decorate(baseTile, r,amount,obd);
                        }
                        else if (decoratorType == TileType.ACTIVATE){
                            String targetName = scanner.next();
                            baseTile = decoratorType.decorate(baseTile, targetName);

                        }
                        else if (decoratorType == TileType.BREAK){
                            double time = scanner.nextDouble();
                            baseTile = decoratorType.decorate(baseTile, time);

                        } else if (decoratorType == TileType.TIMER){
                            double time = scanner.nextDouble();
                            baseTile = decoratorType.decorate(baseTile, time);

                        }
                        else if (decoratorType == TileType.ERASE){

                            baseTile = decoratorType.decorate(baseTile);

                        }
                        else {
                                baseTile = decoratorType.decorate(baseTile);
                            }
                        }
                    // Store the fully decorated tile as a prototype
                    tilePrototypes.put(tileName, baseTile);

                } catch (Exception e) {
                    System.out.println("Error processing tile attributes");
                    e.printStackTrace();
                    if (scanner.hasNextLine()) scanner.nextLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Attributes file not found!");
            e.printStackTrace();
        }
        tileAtb = tilePrototypes;
        return tilePrototypes;
    }

    // Then use the prototypes to create tiles at specific locations
    // In FileReader.java, modify the loadTiles method:
    public static CoordinateTileMap loadTiles(String filename) {
        CoordinateTileMap tileMap = new CoordinateTileMap();
        BaseTile.clearTiles();

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNext()) {
                try {
                    String tileName = scanner.next();

                    // Check if we have more tokens for x and y coordinates
                    if (!scanner.hasNextInt()) {
                        System.out.println("Missing coordinates for tile: " + tileName);
                        continue;
                    }
                    int x = scanner.nextInt();

                    if (!scanner.hasNextInt()) {
                        System.out.println("Missing y coordinate for tile: " + tileName);
                        continue;
                    }
                    int y = scanner.nextInt();

                    TileComponent prototype = tileAtb.get(tileName);
                    if (prototype == null) {
                        System.out.println("Unknown tile type: " + tileName);
                        // Skip remaining parameters on this line
                        if (scanner.hasNextLine()) scanner.nextLine();
                        continue;
                    }

                    // Create new tile based on prototype
                    TileComponent newTile;

                    newTile = prototype.clone();


                    newTile.setPosition(x * TILE_SIZE, y * TILE_SIZE);
                    tileMap.addTile(new int[]{x/27,y/15}, newTile);
                    BaseTile.addTile(newTile);

                } catch (IllegalArgumentException e) {
                    System.out.println("Error processing tokens");
                    e.printStackTrace();
                    // Skip to next line on error
                    if (scanner.hasNextLine()) scanner.nextLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading tiles file!");
            e.printStackTrace();
        }
        return tileMap;
    }
}
