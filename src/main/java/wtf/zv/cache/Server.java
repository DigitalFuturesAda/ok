package wtf.zv.cache;

import io.javalin.Javalin;
import wtf.zv.cache.routes.internal.RouteConfig;
import wtf.zv.cache.routes.internal.RouteDefinitionCallback;
import wtf.zv.cache.routes.modules.cache.CacheRoute;
import wtf.zv.cache.routes.modules.HealthRoute;

import java.util.Arrays;

/**
 * Web server spec:
 *  - service/healthz -> ok (200 OK)
 *  - service/cache/URL -> Returns image
 */
public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(PORT);

        // TODO: Incorporate DI through Dagger and then use multi-bindings to find, inject and instantiate these routes.
        RouteDefinitionCallback[] routeDefinitions = { new CacheRoute(), new HealthRoute() };

        Arrays.stream(routeDefinitions).forEach(routeDefinition -> {
            RouteConfig config = routeDefinition.provideRouteConfig();

            switch (config.getRequestType()){
                case GET:
                    app.get(config.getRoute(), config.getNativeCallback());
                    break;

                case POST:
                    app.post(config.getRoute(), config.getNativeCallback());
                    break;
            }
        });
    }
}
