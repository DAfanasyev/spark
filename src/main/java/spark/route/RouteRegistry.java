/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route;

import spark.utils.MimeParse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.utils.MimeParse.mimeBestMatch;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
public class RouteRegistry {
    private final List<RouteEntry> routes = new ArrayList<>();

    public void addRoute(RouteEntry routeEntry) {
        routes.add(routeEntry);
    }

    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path       the path
     * @param acceptType the accept type
     * @return the target
     */
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        List<RouteEntry> routeEntries = findTargetsForRequestedRoute(httpMethod, path);
        RouteEntry entry = findTargetWithGivenAcceptType(routeEntries, acceptType);
        return entry != null ? new RouteMatch(httpMethod, acceptType, entry.path, path, entry.route) : null;
    }

    public void clearRoutes() {
        routes.clear();
    }

    //////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////

    private Map<String, RouteEntry> getAcceptedMimeTypes(List<RouteEntry> routes) {
        Map<String, RouteEntry> acceptedTypes = new HashMap<>();
        for (RouteEntry routeEntry : routes) {
            if (!acceptedTypes.containsKey(routeEntry.acceptedType)) {
                acceptedTypes.put(routeEntry.acceptedType, routeEntry);
            }
        }
        return acceptedTypes;
    }

    private List<RouteEntry> findTargetsForRequestedRoute(HttpMethod httpMethod, String path) {
        List<RouteEntry> matchSet = new ArrayList<>();
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }

    private RouteEntry findTargetWithGivenAcceptType(List<RouteEntry> routeMatches, String acceptType) {
        if (acceptType != null && routeMatches.size() > 0) {
            Map<String, RouteEntry> acceptedMimeTypes = getAcceptedMimeTypes(routeMatches);
            String bestMatch = mimeBestMatch(acceptedMimeTypes.keySet(), acceptType);
            if (!MimeParse.NO_MIME_TYPE.equals(bestMatch)) {
                return acceptedMimeTypes.get(bestMatch);
            } else {
                return null;
            }
        } else {
            if (routeMatches.size() > 0) {
                return routeMatches.get(0);
            }
        }
        return null;
    }

}
