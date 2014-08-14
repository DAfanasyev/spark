package spark;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        get("/hello", (rq, rs) -> "hello");
        get("/bye", (rq, rs) -> "bye");

        before().get().on("/*").except("/bye").execute((rq, rs) -> System.out.println("You are still working"));
        after().get().on("/bye").execute((rq, rs) -> System.out.println("See you again"));
    }

}
