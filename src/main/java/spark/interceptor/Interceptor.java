package spark.interceptor;

import spark.Request;
import spark.Response;

/**
 * Created by Per Wendel on 2014-05-10.
 */
public interface Interceptor {

    /**
     * Invoked when a request is made on this filter's corresponding path e.g. '/hello'
     *
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @throws java.lang.Exception when handle fails
     */
    void handle(Request request, Response response) throws Exception;

}
