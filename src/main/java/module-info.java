module fr.upcite {
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.fxml;
    requires transitive java.desktop;

    opens fr.upcite to javafx.fxml;
    exports fr.upcite;
}
