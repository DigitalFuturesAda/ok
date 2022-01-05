package wtf.zv.cache.routes.internal;

import io.javalin.http.Context;

public interface HttpRequestCallback {
    void onHttpRequest(Context context);
}
