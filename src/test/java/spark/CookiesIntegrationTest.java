package spark;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spark.Spark.halt;
import static spark.Spark.post;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
public class CookiesIntegrationTest {

    private static final String DEFAULT_HOST_URL = "http://localhost:4567";
    private HttpClient httpClient = HttpClients.createDefault();

    @BeforeClass
    public static void initRoutes() throws InterruptedException {
        post("/assertNoCookies", (request, response) -> {
            if (!request.cookies().isEmpty()) {
                halt(500);
            }
            return "";
        });

        post("/setCookie", (request, response) -> {
            response.cookie(request.queryParams("cookieName"), request.queryParams("cookieValue"));
            return "";
        });

        post("/assertHasCookie", (request, response) -> {
            String cookieValue = request.cookie(request.queryParams("cookieName"));
            if (!request.queryParams("cookieValue").equals(cookieValue)) {
                halt(500);
            }
            return "";
        });

        post("/removeCookie", (request, response) -> {
            String cookieName = request.queryParams("cookieName");
            String cookieValue = request.cookie(cookieName);
            if (!request.queryParams("cookieValue").equals(cookieValue)) {
                halt(500);
            }
            response.removeCookie(cookieName);
            return "";
        });
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    public void testEmptyCookies() {
        httpPost("/assertNoCookies");
    }

    @Test
    public void testCreateCookie() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost("/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost("/assertHasCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
    }

    @Test
    public void testRemoveCookie() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost("/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost("/removeCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost("/assertNoCookies");
    }

    private void httpPost(String relativePath) {
        HttpPost request = new HttpPost(DEFAULT_HOST_URL + relativePath);
        try {
            HttpResponse response = httpClient.execute(request);
            assertEquals(200, response.getStatusLine().getStatusCode());
        } catch (Exception ex) {
            fail(ex.toString());
        } finally {
            request.releaseConnection();
        }
    }
}
