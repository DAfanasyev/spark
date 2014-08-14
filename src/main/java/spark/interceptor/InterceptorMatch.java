package spark.interceptor;

import spark.Match;
import spark.route.HttpMethod;

public class InterceptorMatch extends Match {
    public final Interceptor interceptor;

    public InterceptorMatch(HttpMethod httpMethod, String acceptType, String matchedUri, String requestUri, Interceptor interceptor) {
        super(httpMethod, acceptType, matchedUri, requestUri);
        this.interceptor = interceptor;
    }
}
