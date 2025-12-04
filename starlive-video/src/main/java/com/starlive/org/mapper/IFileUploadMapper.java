package com.starlive.org.mapper;

import com.starlive.org.enity.Files;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.io.File;

@Mapper
public interface IFileUploadMapper {
    @Insert("INSERT INTO files (user_id, upload_id, md5, url, bucket, object, origin_file_name, size, type, chunk_size, chunk_count, is_delete) " +
            "VALUES (#{userId}, #{uploadId}, #{md5}, #{url}, #{bucket}, #{object}, #{originFileName}, #{size}, #{type}, #{chunkSize}, #{chunkCount}, #{isDelete})")
    public void insertFile(Files files);
}
