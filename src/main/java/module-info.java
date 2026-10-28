module com.example {
    requires javafx.controls;
    requires com.google.gson;

    exports com.example;
    opens com.example to com.google.gson;
}
