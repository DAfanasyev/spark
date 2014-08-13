package spark.interceptor;

import java.util.ArrayList;
import java.util.List;

import spark.route.HttpMethod;
import spark.route.RouteMatch;

import static spark.PathMatcher.matches;
import static spark.interceptor.InterceptorRegistration.InterceptionPhase;
import static spark.utils.MimeParse.NO_MIME_TYPE;
import static spark.utils.MimeParse.mimeBestMatch;

/**
 *
 */
public class InterceptorsRegistry {

    private static final InterceptorsRegistry instance = new InterceptorsRegistry();

    public static InterceptorsRegistry get() {
        return instance;
    }

    public final List<InterceptorRegistration> interceptors = new ArrayList<>();

    private InterceptorsRegistry() {
    }

    public InterceptorRegistration addInterceptorRegistration(InterceptorRegistration reg) {
        this.interceptors.add(reg);
        return reg;
    }

    public List<RouteMatch> findInterceptors(InterceptionPhase phase, HttpMethod httpMethod, String path, String acceptType) {
        List<RouteMatch> routeMatches = new ArrayList<>();
        nextReg:
        for (InterceptorRegistration ir : interceptors) {
            if (!ir.phases.isEmpty() && !ir.phases.contains(phase)) continue;
            if (!ir.httpMethods.isEmpty() && !ir.httpMethods.contains(httpMethod)) continue;
            if (!ir.accceptTypes.isEmpty() && NO_MIME_TYPE.equals(mimeBestMatch(ir.accceptTypes, acceptType))) continue;
            for (String excludedPath : ir.excludedPathes) {
                if (matches(excludedPath, path)) continue nextReg;
            }
            for (String includedPath : ir.includedPathes) {
                if (matches(includedPath, path)) {
                    routeMatches.add(new RouteMatch(httpMethod, ir.handler, includedPath, path, acceptType));
                    break;
                }
            }
        }
        return routeMatches;
    }

}
