package spark.examples.transformer;

import static spark.Spark.get;

public class TransformerExample {

    public static void main(String args[]) {
        get("/hello", "application/json", (request, response) -> new MyMessage("Hello World"), new JsonTransformer());
    }

    public static class MyMessage {
        public String message;

        public MyMessage(String message) {
            this.message = message;
        }
    }

}
