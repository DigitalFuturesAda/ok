package wtf.zv.cache.routes.modules.cache.internal;

import java.nio.file.Path;
import java.util.Set;

/** Wrapper around {@link Path} to support uniform FileSystem access */
public class VolumeMapperPath {
    private static final String DEFAULT_FILE_EXTENSION = "png";
    private static final Set<String> WHITELIST_FILE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp", "mp4");

    private final String basePath;

    /** Creates a {@link Path} wrapper that will locate files absolute from the {@link #basePath}. */
    public VolumeMapperPath(String basePath) {
        this.basePath = basePath;
    }

    public Path ofNative(String fileName, String fileExtension){
        fileExtension = ensureExtensionBoundaryConformance(fileExtension);

        if (basePath.endsWith("/")){
            String format = String.format("%s%s.%s", basePath, fileName, fileExtension);
            return Path.of(format);
        } else {
            return Path.of(String.format("%s/%s.%s", basePath, fileName, fileExtension));
        }
    }

    public VMPath ofExperimental(String fileName, String fileExtension){
        if (basePath.endsWith("/")){
            return new VMPath(String.format("%s%s", basePath, fileName), fileExtension);
        } else {
            return new VMPath(String.format("%s/%s", basePath, fileName), fileExtension);
        }
    }

    /** Wrapper around {@link Path} which provides utilities to extract the file extension from an encoded file name */
    public static class VMPath {
        private final Path path;
        private final String fileExtension;

        private VMPath(String path, String fileExtension) {
            this.path = Path.of(path);
            this.fileExtension = ensureExtensionBoundaryConformance(fileExtension);
        }

        public Path withExtension(){
            return Path.of(String.format("%s.%s", path.toString(), fileExtension));
        }

        public Path withoutExtension(){
            return path;
        }
    }

    private static String ensureExtensionBoundaryConformance(String extension){
        return extension.isEmpty() || !WHITELIST_FILE_EXTENSIONS.contains(extension)
                ? DEFAULT_FILE_EXTENSION
                : extension;
    }
}