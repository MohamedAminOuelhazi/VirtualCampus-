module com.virtual.compus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires layout;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.json;


    opens com.virtual.compus to javafx.fxml;
    exports com.virtual.compus;
}