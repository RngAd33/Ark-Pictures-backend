package com.rngad33.web.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.rngad33.web.config.CosClientConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 文件传输
 */
@Component
public class CosManager {

    /**
     * 配置
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 客户端
     */
    @Resource
    private COSClient cosClient;

    /**
     * 文件上传
     *
     * @param key
     * @param file
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public PutObjectResult putObject(String key, File file)
            throws CosClientException, CosServiceException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }


}