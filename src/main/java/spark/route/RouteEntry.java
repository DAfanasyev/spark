package spark.route;

import spark.PathMatcher;
import spark.RouteImpl;

/**
 * Created by Per Wendel on 2014-05-10.
 */
public class RouteEntry {
    public final HttpMethod httpMethod;
    public final String path;
    public final String acceptedType;
    public final RouteImpl route;

    public RouteEntry(HttpMethod httpMethod, String path, String acceptedType, RouteImpl route) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.acceptedType = acceptedType;
        this.route = route;
    }

    public boolean matches(HttpMethod httpMethod, String path) {
        return (this.httpMethod == httpMethod) && PathMatcher.matches(this.path, path);
    }

    @Override
    public String toString() {
        return "RouteEntry{" +
                "httpMethod=" + httpMethod +
                ", path='" + path + '\'' +
                ", acceptedType='" + acceptedType + '\'' +
                ", route=" + route +
                '}';
    }
}
