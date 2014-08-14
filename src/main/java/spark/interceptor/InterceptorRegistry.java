package spark.interceptor;

import spark.route.HttpMethod;
import spark.utils.SparkUtils;

import java.util.ArrayList;
import java.util.List;

import static spark.PathMatcher.matches;
import static spark.interceptor.InterceptorRegistration.InterceptionPhase;
import static spark.utils.MimeParse.mimeMatches;

/**
 *
 */
public class InterceptorRegistry {

    private static final InterceptorRegistry instance = new InterceptorRegistry();

    public static InterceptorRegistry get() {
        return instance;
    }

    public final List<InterceptorRegistration> registrations = new ArrayList<>();

    private InterceptorRegistry() {
    }

    public void clearInterceptors() {
        registrations.clear();
    }

    public InterceptorRegistration addInterceptor(InterceptorRegistration registration) {
        this.registrations.add(registration);
        return registration;
    }

    public List<InterceptorMatch> findInterceptors(InterceptionPhase phase, HttpMethod httpMethod, String path, String acceptType) {
        List<InterceptorMatch> matchResults = new ArrayList<>();
        nextReg:
        for (InterceptorRegistration ir : registrations) {
            if (!ir.phases.isEmpty() && !ir.phases.contains(phase)) continue;
            if (!ir.httpMethods.isEmpty() && !ir.httpMethods.contains(httpMethod)) continue;
            if (!ir.acceptTypes.isEmpty() && acceptType != null && !mimeMatches(ir.acceptTypes, acceptType)) continue;
            for (String excludedPath : ir.excludedPaths) {
                if (matches(excludedPath, path)) continue nextReg;
            }
            if (ir.includedPaths.isEmpty()) {
                matchResults.add(new InterceptorMatch(httpMethod, acceptType, SparkUtils.ALL_PATHS, path, ir.handler));
            } else {
                for (String includedPath : ir.includedPaths) {
                    if (matches(includedPath, path)) {
                        matchResults.add(new InterceptorMatch(httpMethod, acceptType, includedPath, path, ir.handler));
                        break;
                    }
                }
            }
        }
        return matchResults;
    }

}
