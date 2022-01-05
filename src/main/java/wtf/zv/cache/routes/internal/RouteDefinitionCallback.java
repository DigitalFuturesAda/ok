package wtf.zv.cache.routes.internal;

import org.jetbrains.annotations.NotNull;

public interface RouteDefinitionCallback extends HttpRequestCallback {
    /** Provides the route config */
    @NotNull
    RouteConfig provideRouteConfig();
}
