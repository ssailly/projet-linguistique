module fr.upcite {
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.fxml;
    requires transitive java.desktop;
    requires transitive javafx.graphics;

    requires opencv;
    requires batik.transcoder;

    opens fr.upcite to javafx.fxml;
    exports fr.upcite;
}
