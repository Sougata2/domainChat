package com.domain.chat.app.file.service;

import com.domain.chat.app.file.dto.FileDto;
import com.domain.chat.app.message.entity.MessageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {

    FileDto upload(MultipartFile file, MessageEntity message);

    File download(Long id);
}
