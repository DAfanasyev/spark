package spark.route;

import spark.PathMatcher;
import spark.utils.SparkUtils;

/**
 * Created by Per Wendel on 2014-05-10.
 */
class RouteEntry {

    HttpMethod httpMethod;
    String path;
    String acceptedType;
    Object target;

    boolean matches(HttpMethod httpMethod, String path) {
        if ((httpMethod == HttpMethod.before || httpMethod == HttpMethod.after)
                && (this.httpMethod == httpMethod)
                && this.path.equals(SparkUtils.ALL_PATHS)) {
            // Is filter and matches all
            return true;
        }
        boolean match = false;
        if (this.httpMethod == httpMethod) {
            match = PathMatcher.matches(this.path, path);
        }
        return match;
    }


    public String toString() {
        return httpMethod.name() + ", " + path + ", " + target;
    }
}