package wtf.zv.cache.routes.modules;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import wtf.zv.cache.routes.internal.RequestType;
import wtf.zv.cache.routes.internal.RouteConfig;
import wtf.zv.cache.routes.internal.RouteDefinitionCallback;

/** Defines the health route - see {@link #provideRouteConfig} for specification */
public class HealthRoute implements RouteDefinitionCallback {
    @Override
    public void onHttpRequest(Context context) {
        context.result("ok");
    }

    @NotNull
    @Override
    public RouteConfig provideRouteConfig() {
        return RouteConfig.newBuilder()
                .setRoute("/healthz")
                .setRequestType(RequestType.GET)
                .setCallback(this)
                .build();
    }
}
