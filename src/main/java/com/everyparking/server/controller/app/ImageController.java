package com.everyparking.server.controller.app;

import com.everyparking.server.filestore.FileStore;
import com.everyparking.server.filestore.UploadFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/images")
@Slf4j
public class ImageController {

    @Autowired
    private final FileStore fileStore;

    public ImageController(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @Value("${file.dir}")
    private String fileDir;


    /*image 리턴*/
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
        value = "/{fileName}",
        produces = "image/jpg")

    public Resource downloadImage(@PathVariable("fileName") String fileName)
        throws MalformedURLException {

        return new UrlResource("file:" + fileDir + fileName);
    }

//    @PostMapping("/upload")
//    public ResponseEntity<?> saveImage(
//        @RequestParam(required = false, name = "imageFile") MultipartFile multipartFile,
//        HttpServletRequest request,
//        @RequestParam(name = "userId") String userId) throws IOException {
//
//        log.info("[{}] {}", this.getClass().getName(),
//            multipartFile.getOriginalFilename());
//
//        log.info("[{}] userId : {} ", this.getClass().getName(),
//            userId);
//
//        try {
//            fileStore.storeFile(multipartFile, userId);
//
//            log.info("[{}] {} file 저장 성공", this.getClass().getName(),
//                multipartFile.getOriginalFilename());
//
//            return ResponseEntity.status(HttpStatus.OK).build();
//
//        } catch (Exception e) {
//            log.info("[{}] {}", this.getClass().getName(),
//                e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//
//        }
//
//    }


    @PostMapping("/upload")
    public ResponseEntity<?> saveImage(
        @RequestParam(required = false, name = "imageFile") MultipartFile multipartFile,
        HttpServletRequest request,
        @RequestParam(name = "userId", required = false) String userId) throws IOException {

        log.info("[{}] {}", this.getClass().getName(),
            multipartFile.getOriginalFilename());

        log.info("[{}] userId : {} ", this.getClass().getName(),
            request.getHeader("userId"));

        // 폴더 생성
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            UploadFile uploadFile = fileStore.storeFile(multipartFile, userId);

            String imageUrl = "http://everyparking.co.kr/images/" + uploadFile.getStoreFileName();


            log.info("[{}] {} file 저장 성공", this.getClass().getName(),
                multipartFile.getOriginalFilename());

//            return ResponseEntity.status(HttpStatus.OK).build();
            return ResponseEntity.status(HttpStatus.OK)
                .body(imageUrl);


        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(),
                e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }

    }

}
