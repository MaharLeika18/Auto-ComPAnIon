module com.autocompanion.pos.fx {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.autocompanion.pos.fx to javafx.fxml;
    exports com.autocompanion.pos.fx;
}
