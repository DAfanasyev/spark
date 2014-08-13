package spark.interceptor;

import spark.route.HttpMethod;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 *
*/
public class InterceptorRegistration {
    public static enum InterceptionPhase { before, after }

    public final Set<String> includedPathes = new HashSet<>();
    public final Set<String> excludedPathes = new HashSet<>();
    public final Set<String> accceptTypes = new HashSet<>();
    public final Set<HttpMethod> httpMethods = EnumSet.noneOf(HttpMethod.class);
    public final Set<InterceptionPhase> phases = EnumSet.noneOf(InterceptionPhase.class);

    public Interceptor handler = (rq, rs) -> { /* Do nothing */ };

    public InterceptorRegistration before() {
        this.phases.add(InterceptionPhase.before);
        return this;
    }

    public InterceptorRegistration before(String... pathes) {
        return before().on(pathes);
    }

    public InterceptorRegistration after() {
        this.phases.add(InterceptionPhase.after);
        return this;
    }

    public InterceptorRegistration after(String... pathes) {
        return after().on(pathes);
    }

    public InterceptorRegistration get() {
        this.httpMethods.add(HttpMethod.get);
        return this;
    }

    public InterceptorRegistration post() {
        this.httpMethods.add(HttpMethod.post);
        return this;
    }

    public InterceptorRegistration put() {
        this.httpMethods.add(HttpMethod.put);
        return this;
    }

    public InterceptorRegistration patch() {
        this.httpMethods.add(HttpMethod.patch);
        return this;
    }

    public InterceptorRegistration on(String... pathes) {
        this.includedPathes.addAll(asList(pathes));
        return this;
    }

    public InterceptorRegistration accepting(String... acceptTypes) {
        this.accceptTypes.addAll(asList(acceptTypes));
        return this;
    }

    public InterceptorRegistration except(String... pathes) {
        this.excludedPathes.addAll(asList(pathes));
        return this;
    }

    public InterceptorRegistration execute(Interceptor interceptor) {
        this.handler = interceptor;
        return this;
    }
}
