package com.domain.chat.app.media.service;


import com.domain.chat.app.media.dto.MediaDto;
import com.domain.chat.app.message.dto.MessageDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    MessageDto sendMedia(List<MultipartFile> files, MessageDto messageDto);

    List<MediaDto> uploadMedia(List<MultipartFile> files);

    MessageDto send(@RequestBody MessageDto dto);
}
