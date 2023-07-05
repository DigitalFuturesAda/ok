package wtf.zv.cache.routes.modules.cache;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.zv.cache.routes.internal.RequestType;
import wtf.zv.cache.routes.internal.RouteConfig;
import wtf.zv.cache.routes.internal.RouteDefinitionCallback;
import wtf.zv.cache.routes.modules.cache.filemanip.FileReader;
import wtf.zv.cache.routes.modules.cache.filemanip.FileWriter;
import wtf.zv.cache.routes.modules.cache.internal.ImageVolumeMapper;
import wtf.zv.cache.commons.NetUtility;

import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Defines the cache route - see {@link #provideRouteConfig} for specification */
public class CacheRoute implements RouteDefinitionCallback {
    private static final Logger logger = LoggerFactory.getLogger(CacheRoute.class);
    private static final String URL_QUERY_PARAMETER = "url";

    @Override
    public void onHttpRequest(Context context) {
        String urlParameter = context.pathParam(URL_QUERY_PARAMETER);

        if (!NetUtility.assertUrlValid(urlParameter)) {
            context.status(HttpCode.BAD_REQUEST).result("Invalid URL - " + urlParameter);
            return;
        }

        ImageVolumeMapper volumeMapper = ImageVolumeMapper.with(
                URI.create(urlParameter + formParameterConjunction(context.queryParamMap()))
        );
        Consumer<byte[]> onBytesReceived = bytearray -> context.contentType(ContentType.IMAGE_PNG).result(bytearray);
        Consumer<String> onErrorMessage = errorMessage -> reportInternalServerError(context, errorMessage);

        if (volumeMapper.getFileExists()){
            logger.info("URL: {} - File for URL already cached -> Reading file", volumeMapper.getImageUrl());

            FileReader
                    .withMapper(volumeMapper)
                    .onBytesReceived(onBytesReceived::accept)
                    .onError(onErrorMessage::accept)
                    .execute();
        } else {
            logger.info("URL: {} - File is not cached -> Caching file", volumeMapper.getImageUrl());

            FileWriter
                    .withMapper(volumeMapper)
                    .onBytesReceived(onBytesReceived::accept)
                    .onError(onErrorMessage::accept)
                    .execute();
        }
    }

    /** Merges an opaque object - {@code Map<S, L<S>>} - the query parameter list into a stable string
     *
     * This method exists as using both path-parameters ({@code <K>}) and wildcard parameters ({@code *}) will omit
     * query parameters as these logically do not form part of the provided path-parameter. The most simplistic
     * approach to extract these are to iterate through the entrySet and concatenate their key, values after
     * post-processing.
     *
     * E.g: The string <pre>https://zv.wtf?S=1&K=2&T=3</pre> is represented as a {@code Map{S: [1], K: [2], T[3]}},
     * this logic reduces that into the expected string and appends the prefix "?" to the front of the string if
     * not empty, otherwise it returns an empty string.
     *
     * @return a string representing the sum of all query parameters.
     * @param parameterMap containing the query parameters, formed using the {@link Context#queryParamMap()} method.
     */
    private static String formParameterConjunction(Map<String, List<String>> parameterMap){
        String queryConjunction = parameterMap.entrySet().stream()
                .map(stringListEntry -> new AbstractMap.SimpleImmutableEntry<>(stringListEntry.getKey(),
                        stringListEntry.getValue().get(0)))
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));

        return queryConjunction.isEmpty() ? "" : String.format("?%s", queryConjunction);
    }

    /** Helper method - returns an errored context and error-log */
    private void reportInternalServerError(Context context, String errorMessage){
        logger.error(errorMessage);
        context.status(HttpCode.INTERNAL_SERVER_ERROR).result(errorMessage);
    }

    @NotNull
    @Override
    public RouteConfig provideRouteConfig() {
        return RouteConfig.newBuilder()
                .setRoute(String.format("/cache/<%s>", URL_QUERY_PARAMETER))
                .setRequestType(RequestType.GET)
                .setCallback(this)
                .build();

    }
}
