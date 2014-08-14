package spark.examples.interceptor;

import static spark.Spark.*;

/**
 * This example shows interceptor complex configuration.
 */
public class FlexibleInterceptor {

    public static void main(String[] args) {
        // Secure everything except public resources
        before("/*").except("/publicOne", "/publicTwo").execute((rq, rs) -> {
            halt("Go away!");
        });

        // Same as previous one, but with beforeAll() sugar
        beforeAll().except("/publicOne", "/publicTwo").execute((rq, rs) -> {
            halt("Go away!");
        });

        // Interceptor for concrete acceptType request
        beforeAll().accepting("application/json").execute((rq, rs) -> {
            System.out.println("application/json interceptor!");
        });

        // Interceptor for concrete http methods
        before().post().patch().put().on("/readonly").execute((rq, rs) -> {
            halt("You don't have write access!");
        });

        before().get().on("/readonly").execute((rq, rs) -> {
            System.out.println("You are welcome!");
        });

        // Runs after /bye request with any http method and acceptType
        after("/bye").execute((rq, rs) -> System.out.println("See you again"));
    }

}
