package com.starlive.org.config;

import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.messages.Part;

public class CustomMinioClient extends MinioAsyncClient {
    public CustomMinioClient(MinioAsyncClient client) {
        super(client);
    }

    public String initMultiPartUpload(String bucket, String region, String object, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws Exception {
        CreateMultipartUploadResponse response = super.createMultipartUploadAsync(bucket, region, object, headers, extraQueryParams).get();
        return response.result().uploadId();
    }

    /**
     * 查询当前上传后的分片信息
     * @param bucketName String   桶名称
     * @param region String
     * @param objectName String   文件名称
     * @param maxParts Integer  分片数量
     * @param partNumberMarker Integer  分片起始值
     * @param uploadId String   上传的 uploadId
     * @param extraHeaders Multimap<String, String>
     * @param extraQueryParams Multimap<String, String>
     * @return ListPartsResponse
     */
    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws Exception {
        return super.listPartsAsync(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams).get();
    }

    /**
     * 合并分片
     * @param bucketName String   桶名称
     * @param region String
     * @param objectName String   文件名称
     * @param uploadId String   上传的 uploadId
     * @param parts Part[]   分片集合
     * @param extraHeaders Multimap<String, String>
     * @param extraQueryParams Multimap<String, String>
     * @return ObjectWriteResponse
     */
    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws Exception {
        return super.completeMultipartUploadAsync(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams).get();
    }
}
