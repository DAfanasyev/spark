package spark.route;

import spark.Match;
import spark.RouteImpl;

public class RouteMatch extends Match {
    public final RouteImpl route;

    public RouteMatch(HttpMethod httpMethod, String acceptType, String matchedUri, String requestUri, RouteImpl route) {
        super(matchedUri, requestUri);
        this.route = route;
    }
}
