package com.publictransport.proxies.impl;

import com.cloudinary.Cloudinary;
import com.publictransport.proxies.MediaFileProxy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CloudinaryMediaFileProxy implements MediaFileProxy {
    private final Cloudinary CLOUDINARY;

    public CloudinaryMediaFileProxy(Environment env) {
        Map cloudinary_config = new HashMap(){{
            put("cloud_name", env.getProperty("cloudinary.cloud_name"));
            put("api_key", env.getProperty("cloudinary.api_key"));
            put("api_secret", env.getProperty("cloudinary.api_secret"));
            put("secure", env.getProperty("cloudinary.secure"));
        }};
        this.CLOUDINARY = new Cloudinary(cloudinary_config);
    }

    @Override
    public Object uploadFile(Object file, Map options) throws IOException {
        return CLOUDINARY.uploader().upload(file, options);
    }

    @Override
    public Object deleteFile(Object fileId) throws IOException {
        return CLOUDINARY.uploader().destroy((String)fileId, new HashMap<>());
    }
}
