import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class GUI {
    public  int sceneWidth = 800;
    public  int sceneHeight = 450;
    private  VBox guiPane = new VBox();

    private  HBox esshBox = new HBox(10); // editor start&save H box
    private ArrayList<String[]> editorButtons = new ArrayList<>();
    private ArrayList<String[]> visibleButtons = new ArrayList<>();
    private VBox buttonBox = new VBox(10);
    private GridPane edGrid = new GridPane();
    private boolean deleteActive = true;
    private ColorAdjust normal = new ColorAdjust();

    private Button selectedButton = null;
    private ColorAdjust grayscale = new ColorAdjust();
    private ColorAdjust colorAdjust = new ColorAdjust();
    private ColorAdjust active = new ColorAdjust();
    private String selectedButtonType = "delete"; // DEFUALT TO DELETE


    public GUI(){

    }

    public  Pane load(char scene) {

        guiPane.setPickOnBounds(true);

        //VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(6));
        buttonBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        //buttonBox.setAlignment(Pos.CENTER);


        //pane attributes
        guiPane.setPadding(new Insets(10));
        //guiPane.getChildren().add(buttonBox);
        guiPane.setPrefSize(sceneWidth, sceneHeight);
        guiPane.setMaxSize(sceneWidth, sceneHeight);
        guiPane.setAlignment(Pos.TOP_LEFT);
        guiPane.setFocusTraversable(false);


        // which GUI to load
        if (scene == 'e') { //add buttons for editor mode

            // add place and delete selector. -hbox
            //addActionSelectButtons();
            //add scroll buttons -hbox
            addScrollButtons();
            //add tiles -gridpane
            addEditorButtons();
            guiPane.getChildren().add(edGrid);
            guiPane.setPrefSize(90, 240);
            guiPane.setMaxSize(90, 240);


        } else if (scene == 'p') { //add buttons for levelplayer
            addPlayerButtons(buttonBox);
            guiPane.getChildren().add(buttonBox);
            buttonBox.setPrefWidth(85);
            buttonBox.setMaxWidth(85);
            buttonBox.setPrefHeight(40);
            buttonBox.setMaxHeight(40);
            buttonBox.setAlignment(Pos.CENTER);
        } else if (scene == 's') { //add start and save buttons
            //addPlayerButtons(buttonBox);
            //add start and save buttons
            addStartSaveButtons(buttonBox);
            guiPane.getChildren().add(esshBox);
            guiPane.setPrefSize(170, 45);
            guiPane.setMaxSize(170, 45);
        }


        return guiPane;
    }

    private int currentStartIndex = 0;
    //private final int VISIBLE_COUNT = ;

    //private int currentStartIndex = 0;

    private void updateVisibleButtons() {
        visibleButtons.clear();

        // Ensure editorButtons is fully populated with all tiles
        if (editorButtons.isEmpty()) {
            Map<String, TileComponent> tilePrototypes = FileReader.initializeTilePrototypes("atributes.txt");

            // Maintain order with an ArrayList instead of relying solely on the map
            List<String> tileOrder = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File("atributes.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    String[] tokens = line.split("\\s+");
                    if (tokens.length < 10) continue;

                    String tileName = tokens[0];

                    // Only add if it's a unique tile and exists in prototypes
                    if (!tileOrder.contains(tileName) && tilePrototypes.containsKey(tileName)) {
                        tileOrder.add(tileName);
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error reading attributes file!");
                e.printStackTrace();
                return;
            }

            // Populate editorButtons in the exact file order
            for (String tileName : tileOrder) {
                TileComponent tile = tilePrototypes.get(tileName);
                editorButtons.add(new String[]{tile.getName() + "x", tile.getTexture()});
            }
        }

        // Calculate how many buttons to show (max 10)
        int count = Math.min(10, editorButtons.size());

        // Populate visible buttons with a sliding window
        for (int i = 0; i < count; i++) {
            int index = (currentStartIndex + i) % editorButtons.size();
            visibleButtons.add(new String[]{editorButtons.get(index)[0], editorButtons.get(index)[1]});
        }
    }





    private void scrollDown() {
        if (editorButtons.size() > 10) {
            currentStartIndex = (currentStartIndex + 1) % editorButtons.size();
            updateVisibleButtons();
            addEditorButtons();
        }
    }

    private void scrollUp() {
        if (editorButtons.size() > 10) {
            currentStartIndex--;
            if (currentStartIndex < 0) {
                currentStartIndex = editorButtons.size() - 1;
            }
            updateVisibleButtons();
            addEditorButtons();
        }
    }







    //add scroll button for editor
    private  void addScrollButtons() {


        //buttonBox.setPadding(new Insets(10));
        //buttonBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        //buttonBox.setPrefWidth(160);
        //buttonBox.setMaxWidth(160);
        HBox scroll = new HBox();

        //scroll up BUTTON
        Button upButton = createStyledButton(new String[]{"▲", null});
        upButton.setPrefSize(30,30);
        upButton.setOnAction(e -> {
            //up
            scrollUp();
            scrollUp();



        });
        upButton.setFocusTraversable(false);
        //buttonBox.getChildren().add(0,upButton);
        scroll.getChildren().add(upButton);


        //scroll down BUTTON
        Button downButton = createStyledButton(new String[]{"▼", null});
        downButton.setPrefSize(30,30);
        downButton.setOnAction(e -> {
            //down
            scrollDown();
            scrollDown();

        });
        downButton.setFocusTraversable(false);
        //buttonBox.getChildren().add(downButton);
        //edGrid.add(downButton, 1,7);
        scroll.getChildren().add(downButton);
        guiPane.getChildren().add(scroll);

    }






    //add start and save button for editor
    private  void addStartSaveButtons(VBox buttonBox) {


        esshBox.setPadding(new Insets(10));
        esshBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        esshBox.setPrefWidth(140); //135
        esshBox.setMaxWidth(140);
        esshBox.setPrefHeight(0);
        esshBox.setMaxHeight(0);


        //PLAY BUTTON
        Button playButton = createStyledButton(new String[]{"PLAY",null});
        playButton.setOnAction(e -> {
            //stop editor animation
            //clear
            //load levelplayer
            LevelEditor.handleEditorButton(e);
            //Client.loadLevelPlayer();
            //LevelEditor.stopAnimations();

        });
        playButton.setPrefWidth(60);
        playButton.setFocusTraversable(false);
        esshBox.getChildren().add(playButton);


        //SAVE BUTTON
        Button saveButton = createStyledButton(new String[]{"SAVE", null});
        saveButton.setOnAction(e -> {

            LevelEditor.handleEditorButton(e);


        });
        saveButton.setPrefWidth(60);
        saveButton.setFocusTraversable(false);
        esshBox.getChildren().add(saveButton);

    }




    // BUTTONS FOR GAME PLAYAGE (just the back to editor button)
    private  void addPlayerButtons(VBox buttonBox) {
        Button button = createStyledButton(new String[]{"EDITOR", null});
        button.setOnAction(e -> {
                Client.loadLevelEditor();
        });
        button.setPrefWidth(80);
        button.setFocusTraversable(false);
        buttonBox.getChildren().add(button);
    }

    private BackgroundImage backgroundImage = null;
    private  Button createStyledButton(String[] text) {
        // Create the button
        Button button;

        String cssatt;
        //BackgroundImage backgroundImage = null;

        //System.out.println("file: "+text[1]);
        if(text[1] != null){
            cssatt = "";//editorButtons.contains(text)) {


            // Create the button
            button = new Button();
            try {
                Image textureImage = new Image(getClass().getResourceAsStream("assets/" + text[1]));

                // Create a smaller BackgroundSize (for example, 50x50 pixels)
                BackgroundSize backgroundSize = new BackgroundSize(
                        30, 30,          // Width and height in pixels
                        false, false,    // Don't scale based on percentage
                        false, false      // Contain within the bounds (or use 'false' for exact size)

                );

                // Create a BackgroundImage for tiling with the specified size
                backgroundImage = new BackgroundImage(
                        textureImage,
                        BackgroundRepeat.REPEAT,  // Repeat the image horizontally
                        BackgroundRepeat.REPEAT,  // Repeat the image vertically
                        BackgroundPosition.DEFAULT,
                        backgroundSize
                );

                // Set the background
                button.setBackground(new Background(backgroundImage));

        } catch (Exception e) {
                System.out.println("something went wrong loading image :(");
                //System.out.println("PNG NOT FOUND IN ASSESTS");

                throw new RuntimeException("\nPNG "+text[1]+" FOR "+text[0]+ " NOT FOUND IN ASSESTS\n:( :( :(\n\nCHECK ATTRIBUTE FILE AND CONFIRM PNG EXISTS IN ASSETS FOLDER\n X( X( X(\n", e);
        }

        }
        else{
            // Create the button
            button = new Button(text[0]);
            cssatt = "-fx-background-color: #4a4a4a;" +"-fx-background-radius: 5;" ;
        }

        String cssSize = "-fx-font-size: 15px;";
        String cssBC = "";


        if(text[0].equals("erase")){
            //cssSize = "-fx-font-size: 9px;";
            //cssBC = "-fx-background-color: #990707;";

        }
        if(text[0].equals("▼")){
            cssSize = "-fx-font-size: 10px;";
        }
        if(text[0].equals("▲")){
            cssSize = "-fx-font-size: 10px;";
        }



        //Button button = new Button();
        //button.setGraphic(content);
        button.setStyle(
                        "-fx-text-fill: white;" +
                        "-fx-padding: 3 10 3 10;" +
                        "-fx-font-family: 'Agency FB';"+
                        "-fx-font-weight: bold;" +
                                "-fx-letter-spacing: 64px;"+
                                "-fx-cursor: hand;"+

        cssatt + cssSize + cssBC
        );
        final BackgroundImage backgroundImage2 = this.backgroundImage;
        //System.out.println(this.backgroundImage);

        //tile image adjust on mouse entering
        //ColorAdjust grayscale = new ColorAdjust();
        grayscale.setSaturation(-0.5); // value between -1.0 (fully grayscale) and 0.0 (normal)
        grayscale.setContrast(0.4);
        grayscale.setBrightness(0.6);

        //normla
        //ColorAdjust normal = new ColorAdjust();
        normal.setSaturation(0);


        //mouse enter avg button adjust
        //ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.7);
        colorAdjust.setContrast(0.3);
        colorAdjust.setBrightness(0.4);

        //when clicked
        //ColorAdjust active = new ColorAdjust();
        //active.setSaturation(0.3);
        active.setContrast(.5);
        active.setBrightness(0.5);


        String finalCssBC = cssBC;
        String finalCssSize = cssSize;
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-text-fill: black;" +
                    //"-fx-background-radius: 5;" +
                    "-fx-padding: 3 10 3 10;" +
                    "-fx-font-family: 'Agency FB';" +
                    "-fx-font-weight: bold;"+
                            "-fx-letter-spacing: 64px;"+
                    cssatt + finalCssSize + finalCssBC
            );
            button.setEffect(colorAdjust); // Apply the grayscale effect

            if (backgroundImage2 != null) {
                button.setBackground(new Background(backgroundImage2));
                button.setEffect(grayscale); // Apply the grayscale effect

            }
        });


        button.setOnMouseExited(e -> {


                button.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-padding: 3 10 3 10;" +
                                "-fx-font-family: 'Agency FB';" +
                                "-fx-font-weight: bold;" +
                                "-fx-letter-spacing: 64px;" +
                                cssatt + finalCssSize + finalCssBC
                );
                button.setEffect(normal);
                if (backgroundImage2 != null) {
                    button.setBackground(new Background(backgroundImage2));
                    button.setEffect(normal);
                }


        });


        /*
        if(!text[0].equals("PLAY") && !text[0].equals("SAVE") && !text[0].equals("EDITOR") && !text[0].equals("▲") && !text[0].equals("▼")){

            button.setOnAction(e ->{
                handleTileSelection(button, text[0]);
                System.out.println("\n\n"+text[0] + " select\n\n");
            });

        }

         */

        return button;
    }




    private void addEditorButtons() {
        edGrid.getChildren().clear();
        editorButtons.clear(); // Clear previous buttons first

        Map<String, TileComponent> tilePrototypes = FileReader.initializeTilePrototypes("atributes.txt");

        // Maintain order with an ArrayList instead of relying solely on the map
        List<String> tileOrder = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("atributes.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue; // Skip empty lines

                String[] tokens = line.split("\\s+");
                if (tokens.length < 10) continue; // Skip incomplete lines

                String tileName = tokens[0];

                // Only add if it's a unique tile and exists in prototypes
                if (!tileOrder.contains(tileName) && tilePrototypes.containsKey(tileName)) {
                    tileOrder.add(tileName);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading attributes file!");
            e.printStackTrace();
            return;
        }

        // Populate editorButtons in the exact file order
        for (String tileName : tileOrder) {
            TileComponent tile = tilePrototypes.get(tileName);
            editorButtons.add(new String[]{tile.getName() + "x", tile.getTexture()});
        }

        visibleButtons = new ArrayList<>();
        updateVisibleButtons();

        int i = 0;
        Button buttonToSelect = null;

        for (String[] buttonText : visibleButtons) {
            Button button = createStyledButton(buttonText);
            button.setPrefWidth(30);
            button.setMaxWidth(30);
            button.setPrefHeight(30);
            button.setMaxHeight(30);

            final Button finalButton = button;
            button.setOnAction(e -> {
                handleTileSelection(finalButton, buttonText[0]);

                switch(buttonText[0]) {
                    case "erasex":
                        if(!deleteActive) {
                            deleteActive = true;
                            LevelEditor.toggleDel();
                        }
                        break;
                    default:
                        if (deleteActive){
                            deleteActive = false;
                            LevelEditor.toggleDel();
                        }

                        LevelEditor.setDrawTile(buttonText[0].substring(0, buttonText[0].length() - 1), buttonText[1]);
                        break;
                }
            });

            int row = i / 2;
            int col = i % 2;
            button.setFocusTraversable(false);
            edGrid.add(button, col, row);

            if (buttonText[0].equals(selectedButtonType)) {
                buttonToSelect = button;
            }

            i++;
        }

        // Apply selection after all buttons are created
        if (buttonToSelect != null) {
            Button finalButtonToSelect = buttonToSelect;
            Platform.runLater(() -> handleTileSelection(finalButtonToSelect, selectedButtonType));
        } else if (selectedButton == null) {
            // If no button was selected and no matching button found, select first button (delete)
            Button firstButton = (Button) edGrid.getChildren().get(0);
            Platform.runLater(() -> handleTileSelection(firstButton, "erasex"));
        }
    }

    private void applySelectedStyle(Button button) {

        button.setEffect(active);


        button.setOnMouseEntered(e -> {
            button.setEffect(active);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(active);
        });

        // Add a border for the selected state
        String currentStyle = button.getStyle().replaceAll("-fx-border-[^;]+;", ""); // Remove any existing border styles
        button.setStyle(
                currentStyle +
                        "-fx-border-color: #ea0707;"+//ffd700;" + // Gold border
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 4px;"
        );
    }
    private void handleTileSelection(Button button, String buttonName) {

        selectedButtonType = buttonName;


        if (selectedButton != null && selectedButton != button) {
            resetButtonStyle(selectedButton);
        }

        selectedButton = button;
        applySelectedStyle(button);

        Background currentBackground = button.getBackground();

        button.setEffect(active);

        button.setOnMouseEntered(e -> {
            button.setEffect(active);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(active);
        });
    }

    private void resetButtonStyle(Button button) {
        if (button == null) return;

        //System.out.println("\n\nBEING RESET: "+button.getText()+"\n\n");
        String currentStyle = button.getStyle().replaceAll("-fx-border-[^;]+;", ""); // Remove any existing border styles
        button.setStyle(
                currentStyle
        );

        Background currentBackground = button.getBackground();

        // reset effect
        button.setEffect(normal);

        button.setOnMouseEntered(e -> {
            if (currentBackground != null) {
                button.setBackground(currentBackground);
                button.setEffect(grayscale);
            } else {
                button.setEffect(colorAdjust);
            }
        });

        button.setOnMouseExited(e -> {
            if (currentBackground != null) {
                button.setBackground(currentBackground);
            }
            button.setEffect(normal);
        });


        if (currentBackground != null) {
            button.setBackground(currentBackground);
        }
    }



}
