package wtf.zv.cache.routes.modules.cache.filemanip;

import wtf.zv.cache.routes.modules.cache.filemanip.callbacks.BytesReceivedCallback;
import wtf.zv.cache.routes.modules.cache.filemanip.callbacks.ErrorCallback;
import wtf.zv.cache.routes.modules.cache.internal.ImageVolumeMapper;

import java.io.IOException;

public class FileReader implements FilePersistenceTransformer {
    private final ImageVolumeMapper imageVolumeMapper;

    private BytesReceivedCallback bytesReceivedCallback;
    private ErrorCallback errorCallback;

    public static FileReader withMapper(ImageVolumeMapper volumeMapper){
        return new FileReader(volumeMapper);
    }

    @Override
    public FileReader onBytesReceived(BytesReceivedCallback callback){
        bytesReceivedCallback = callback;
        return this;
    }

    @Override
    public FileReader onError(ErrorCallback callback){
        errorCallback = callback;
        return this;
    }

    @Override
    public void execute(){
        try {
            byte[] bytes = imageVolumeMapper.readFile();
            bytesReceivedCallback.onBytesReceived(bytes);
        } catch (IOException e) {
            String errorMessage = String.format("URL: %s - Encountered error whilst reading file: %s",
                    imageVolumeMapper.getImageUrl(), e.getMessage());
            errorCallback.onError(errorMessage);
        }
    }

    private FileReader(ImageVolumeMapper imageVolumeMapper) {
        this.imageVolumeMapper = imageVolumeMapper;
    }
}
