module com.example.cis296proj4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cis296proj4 to javafx.fxml;
    exports com.example.cis296proj4;
}