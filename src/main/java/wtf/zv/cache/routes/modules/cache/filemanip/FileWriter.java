package wtf.zv.cache.routes.modules.cache.filemanip;

import wtf.zv.cache.routes.modules.cache.filemanip.callbacks.BytesReceivedCallback;
import wtf.zv.cache.routes.modules.cache.filemanip.callbacks.ErrorCallback;
import wtf.zv.cache.routes.modules.cache.internal.ImageVolumeMapper;

import java.io.IOException;

public class FileWriter implements FilePersistenceTransformer {
    private final ImageVolumeMapper imageVolumeMapper;

    private BytesReceivedCallback bytesReceivedCallback;
    private ErrorCallback errorCallback;

    public static FileWriter withMapper(ImageVolumeMapper volumeMapper){
        return new FileWriter(volumeMapper);
    }

    @Override
    public FileWriter onBytesReceived(BytesReceivedCallback callback){
        bytesReceivedCallback = callback;
        return this;
    }

    @Override
    public FileWriter onError(ErrorCallback callback){
        errorCallback = callback;
        return this;
    }

    @Override
    public void execute(){
        try {
            byte[] bytes = imageVolumeMapper.persistToFileSystem();
            bytesReceivedCallback.onBytesReceived(bytes);
        } catch (IOException e) {
            String errorMessage = String.format("URL: %s - Encountered error whilst persisting image: %s",
                    imageVolumeMapper.getImageUrl(), e.getMessage());

            errorCallback.onError(errorMessage);
        }
    }

    private FileWriter(ImageVolumeMapper imageVolumeMapper) {
        this.imageVolumeMapper = imageVolumeMapper;
    }
}
