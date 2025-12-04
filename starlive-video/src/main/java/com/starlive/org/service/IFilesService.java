package com.starlive.org.service;

import com.starlive.org.model.FileUploadInfo;
import com.starlive.org.model.UrlVo;
import com.starlive.org.result.WebResult;

public interface IFilesService {

    WebResult initMultipartUpload(FileUploadInfo fileUploadInfo);

    WebResult mergeFilePart(String md5);

}
