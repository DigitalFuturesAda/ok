package wtf.zv.cache.routes.modules.cache.internal;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.zv.cache.commons.NetUtility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Handles accessing and persisting given URLs to the FileSystem.
 *
 * This searches through a flat-file database, whilst this is akin to a pretty low-tech CDN it's fine that we're not
 * attempting to reduce the search space (e.g bloom filters, graph searching) as the CDN size will remain relatively
 * low (<1GB or ~4000 files).
 */
public class ImageVolumeMapper {
    private static final Logger logger = LoggerFactory.getLogger(ImageVolumeMapper.class);
    private static final VolumeMapperPath VMPath = new VolumeMapperPath("/data/");
    private final URI imageUrl;

    /** Returns {@code true} if a file has already been mapped to the {@link #imageUrl} */
    public boolean getFileExists(){
        return Files.exists(getAbsolutePath()) && Files.isReadable(getAbsolutePath());
    }

    public byte[] readFile() throws IOException {
        // Note #readAllBytes isn't suitable for larger files, record traces and determine whether usage of this method
        // is detrimental.
        return Files.readAllBytes(getAbsolutePath());
    }

    /** Persists the given {@link #imageUrl} to the filesystem as a Base64 encoded string */
    public byte[] persistToFileSystem() throws IOException {
        Path safePath = getAbsolutePath();

        InputStream inputStream = imageUrl.toURL().openStream();
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(safePath.toFile());
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        return Files.readAllBytes(safePath);
    }

    /** Provides the {@link #imageUrl} */
    public URI getImageUrl(){
        return imageUrl;
    }

    /** Instantiate an {@link ImageVolumeMapper} with the given {@link URI} */
    public static ImageVolumeMapper with(URI imageUrl){
        return new ImageVolumeMapper(imageUrl);
    }

    /** Constructs a {@link Path} using the base value as per the {@link VolumeMapperPath} spec */
    @NotNull
    private Path getAbsolutePath() {
        return VMPath.ofNative(encodeUrlUsingB64(),
                NetUtility.extractFileExtensionFromUrl(NetUtility.unsafeConvertUriToUrl(imageUrl)));
    }

    /** Encodes the URL using {@link java.util.Base64} */
    private String encodeUrlUsingB64(){
        return Base64.getEncoder().encodeToString(NetUtility.extractEntireBaseUrl(imageUrl).getBytes());
    }

    private ImageVolumeMapper(URI imageUrl) {
        this.imageUrl = imageUrl;
    }
}
