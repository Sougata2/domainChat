package com.domain.chat.app.media.service;


import com.domain.chat.app.message.dto.MessageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    MessageDto sendMedia(List<MultipartFile> files, MessageDto messageDto);
}
