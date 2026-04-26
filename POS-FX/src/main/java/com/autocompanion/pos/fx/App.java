package com.autocompanion.pos.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Get screen bounds EXCLUDING taskbar
        //Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        // Load fonts
        Font.loadFont(
            getClass().getResourceAsStream("/com/autocompanion/pos/Assets/Fonts/Montserrat/static/Montserrat-Regular.ttf"),
            14
        );
        Font.loadFont(
            getClass().getResourceAsStream("/com/autocompanion/pos/Assets/Fonts/Montserrat/static/Montserrat-SemiBold.ttf"),
            14
        );

        Font.loadFont(
            getClass().getResourceAsStream("/com/autocompanion/pos/Assets/Fonts/Orbitron/Orbitron-VariableFont_wght.ttf"),
            14
        );
        
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(root);
        
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/autocompanion/pos/Assets/Logo/(W) Auto-ComPAnIon Logo.png")));
        stage.setTitle("Auto-ComPAnIon");
        stage.setMaximized(true);

        
                // Set to full screen within taskbar bounds
        //stage.setX(screenBounds.getMinX());
       // stage.setY(screenBounds.getMinY());
       // stage.setWidth(screenBounds.getWidth());
       // stage.setHeight(screenBounds.getHeight());
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}