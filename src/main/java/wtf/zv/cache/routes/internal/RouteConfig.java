package wtf.zv.cache.routes.internal;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

// TODO: May as well just implement autovalue-builder.
public class RouteConfig {
    public static Builder newBuilder(){
        return new Builder();
    }

    private final String route;
    private final HttpRequestCallback callback;
    private final RequestType requestType;
    private final NativeHandler nativeHandler;

    private RouteConfig(String route, HttpRequestCallback callback, RequestType requestType){
        this.route = route;
        this.callback = callback;
        this.requestType = requestType;
        this.nativeHandler = NativeHandler.withCallback(callback);
    }

    public String getRoute(){
        return route;
    }

    public HttpRequestCallback getCallback(){
        return callback;
    }

    public Handler getNativeCallback(){
        return nativeHandler;
    }

    public RequestType getRequestType(){
        return requestType;
    }

    public static class Builder {
        private String route = "";
        private HttpRequestCallback callback;
        private RequestType requestType = RequestType.GET;

        public Builder setRoute(String route){
            this.route = route;
            return this;
        }

        public Builder setCallback(HttpRequestCallback callback){
            this.callback = callback;
            return this;
        }

        public Builder setRequestType(RequestType requestType){
            this.requestType = requestType;
            return this;
        }

        public RouteConfig build(){
            return new RouteConfig(route, callback, requestType);
        }
    }

    private static class NativeHandler implements Handler {
        HttpRequestCallback callback;

        public static NativeHandler withCallback(HttpRequestCallback callback){
            return new NativeHandler(callback);
        }

        private NativeHandler(HttpRequestCallback callback) {
            this.callback = callback;
        }

        @Override
        public void handle(@NotNull Context ctx) {
            callback.onHttpRequest(ctx);
        }
    }
}
