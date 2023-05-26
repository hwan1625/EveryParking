package com.everyparking.server.filestore;

import com.everyparking.server.data.repository.UploadFileRepository;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStore {


    @Autowired
    private final UploadFileRepository uploadFileRepository;

//    @Autowired
//    private final ReportRepository reportRepository;


    @Value("${file.dir}")
    private String fileDir;

    public FileStore(UploadFileRepository uploadFileRepository) {
        this.uploadFileRepository = uploadFileRepository;
//        this.reportRepository = reportRepository;
    }

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    public UploadFile storeFile(MultipartFile multipartFile, String userId)
        throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFileName);
        multipartFile.transferTo(
            new File(
                getFullPath(storeFileName)
            )
        );

//        Report report = reportRepository.findByMember_UserId(userId).get();

        UploadFile uploadFile = new UploadFile(originalFileName, storeFileName);

        uploadFileRepository.save(uploadFile);

        return uploadFile;

    }

//    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
//        List<UploadFile> storeFileResult =
//
//    }

    private String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();

        return uuid + "." + ext;

    }

    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }
}
