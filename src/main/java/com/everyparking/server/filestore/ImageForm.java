package com.everyparking.server.filestore;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageForm {

    private MultipartFile multipartFile;

}
