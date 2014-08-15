package spark.examples.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.beforeAll;
import static spark.Spark.halt;

/**
 * This example shows interceptor complex configuration.
 */
public class FlexibleInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FlexibleInterceptor.class);

    public static void main(String[] args) {
        // Secure everything except public resources
        before("/*").except("/publicOne", "/publicTwo").execute((rq, rs) -> halt("Go away!"));

        // Same as previous one, but with beforeAll() sugar
        beforeAll().except("/publicOne", "/publicTwo").execute((rq, rs) -> halt("Go away!"));

        // Interceptor for concrete acceptType request
        beforeAll().accepting("application/json").execute((rq, rs) -> log.info("application/json interceptor!"));

        // Interceptor for concrete http methods
        before().post().patch().put().on("/readonly").execute((rq, rs) -> halt("You don't have write access!"));

        before().get().on("/readonly").execute((rq, rs) -> log.info("You are welcome!"));

        // Runs after /bye request with any http method and acceptType
        after("/bye").execute((rq, rs) -> log.info("See you again"));
    }

}
