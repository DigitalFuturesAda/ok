package wtf.zv.cache.routes.modules.cache.filemanip;

import wtf.zv.cache.routes.modules.cache.filemanip.callbacks.BytesReceivedCallback;
import wtf.zv.cache.routes.modules.cache.filemanip.callbacks.ErrorCallback;

public interface FilePersistenceTransformer {
    FilePersistenceTransformer onBytesReceived(BytesReceivedCallback bytesReceivedCallback);

    FilePersistenceTransformer onError(ErrorCallback errorCallback);

    void execute();
}
