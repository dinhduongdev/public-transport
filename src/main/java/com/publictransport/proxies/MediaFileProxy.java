package com.publictransport.proxies;

import java.io.IOException;
import java.util.Map;

public interface MediaFileProxy {
    Object uploadFile(Object file, Map options) throws IOException;
    Object deleteFile(Object fileId) throws IOException;
}
