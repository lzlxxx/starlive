package com.starlive.org.controller;
import com.starlive.org.model.FileUploadInfo;
import com.starlive.org.result.WebResult;
import com.starlive.org.service.IFilesService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@Api(value = "视频上传接口", tags = "视频上传接口")
@Slf4j
public class FileUploadController {

    @Resource
    IFilesService filesService;

    @PostMapping("/init")
    @Operation(summary = "视频")
    public WebResult initFileUpload(@RequestBody FileUploadInfo fileUploadInfo){
        //1.进入发放签名业务
        return filesService.initMultipartUpload(fileUploadInfo);
    }

    @PostMapping("/merge/{md5}")
    @Operation(summary = "视频分片上传")
    public WebResult mergeFilePart(@PathVariable String md5){
        //1.进入合并业务
        return filesService.mergeFilePart(md5);
    }

}
