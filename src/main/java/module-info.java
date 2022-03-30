module fr.upcite {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.upcite to javafx.fxml;
    exports fr.upcite;
}
