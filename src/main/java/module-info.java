module com.example {
    requires javafx.controls;
    requires com.google.gson;
    requires com.github.kwhat.jnativehook;
    requires com.sun.jna;

    exports com.example;
    opens com.example to com.google.gson;
}
