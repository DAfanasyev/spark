package spark.interceptor;

import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.utils.SparkUtils;

import java.util.ArrayList;
import java.util.List;

import static spark.PathMatcher.matches;
import static spark.interceptor.InterceptorRegistration.InterceptionPhase;
import static spark.utils.MimeParse.NO_MIME_TYPE;
import static spark.utils.MimeParse.mimeBestMatch;

/**
 *
 */
public class InterceptorRegistry {

    private static final InterceptorRegistry instance = new InterceptorRegistry();
    public static InterceptorRegistry get() { return instance; }

    public final List<InterceptorRegistration> registrations = new ArrayList<>();

    private InterceptorRegistry() {
    }

    public InterceptorRegistration addInterceptor(InterceptorRegistration registration) {
        this.registrations.add(registration);
        return registration;
    }

    public List<RouteMatch> findInterceptors(InterceptionPhase phase, HttpMethod httpMethod, String path, String acceptType) {
        List<RouteMatch> routeMatches = new ArrayList<>();
        nextReg:
        for (InterceptorRegistration ir : registrations) {
            if (!ir.phases.isEmpty() && !ir.phases.contains(phase)) continue;
            if (!ir.httpMethods.isEmpty() && !ir.httpMethods.contains(httpMethod)) continue;
            if (!ir.acceptTypes.isEmpty() && NO_MIME_TYPE.equals(mimeBestMatch(ir.acceptTypes, acceptType))) continue;
            for (String excludedPath : ir.excludedPaths) {
                if (matches(excludedPath, path)) continue nextReg;
            }
            if (ir.includedPaths.isEmpty()) {
                routeMatches.add(new RouteMatch(httpMethod, ir.handler, SparkUtils.ALL_PATHS, path, acceptType));
            } else {
                for (String includedPath : ir.includedPaths) {
                    if (matches(includedPath, path)) {
                        routeMatches.add(new RouteMatch(httpMethod, ir.handler, includedPath, path, acceptType));
                        break;
                    }
                }
            }
        }
        return routeMatches;
    }

}
