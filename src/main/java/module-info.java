module com.example.xsodoku {
    requires javafx.controls;
    requires javafx.fxml;


    opens Controller to javafx.fxml;
    exports Controller;
}