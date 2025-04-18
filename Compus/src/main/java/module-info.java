module com.virtual.compus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.virtual.compus to javafx.fxml;
    exports com.virtual.compus;
}