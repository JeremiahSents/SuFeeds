package com.sentomero.sufeeds.javasufeeds.Utilities;

import javafx.scene.Scene;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CSSLoader {
    private static final Logger LOGGER = Logger.getLogger(CSSLoader.class.getName());
    private static final String BASE_PATH = "/com/sentomero/sufeeds/views/";

    public static void loadCSS(Scene scene, String... cssFiles) {
        if (scene == null) {
            LOGGER.warning("Cannot load CSS: Scene is null");
            return;
        }

        for (String cssFile : cssFiles) {
            String fullPath = BASE_PATH + cssFile;
            URL cssUrl = CSSLoader.class.getResource(fullPath);

            if (cssUrl != null) {
                String externalForm = cssUrl.toExternalForm();
                // Check if stylesheet is already loaded
                if (!scene.getStylesheets().contains(externalForm)) {
                    scene.getStylesheets().add(externalForm);
                    LOGGER.info("Successfully loaded CSS: " + cssFile);
                }
            } else {
                LOGGER.log(Level.WARNING, "Could not load CSS file: {0}", cssFile);
            }
        }
    }

    public static void removeCSS(Scene scene, String... cssFiles) {
        if (scene == null) {
            LOGGER.warning("Cannot remove CSS: Scene is null");
            return;
        }

        for (String cssFile : cssFiles) {
            String fullPath = BASE_PATH + cssFile;
            URL cssUrl = CSSLoader.class.getResource(fullPath);

            if (cssUrl != null) {
                scene.getStylesheets().remove(cssUrl.toExternalForm());
                LOGGER.info("Successfully removed CSS: " + cssFile);
            }
        }
    }
}