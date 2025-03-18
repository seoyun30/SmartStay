package com.lookatme.smartstay.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.lookatme.smartstay.Util.FileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3Client amazonS3Client;
    private final FileUpload fileUpload;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${imgUploadLocation}") //이미지가 저장될 위치
    private String imgLocation;

    @Value("${thumbnailLocation}") // 썸네일을 저장할 위치
    private String thumbnailLocation;
    //파일에 대한 저장
    //  파일명이나 경로를 입력받아 물리적인 파일을 만든다.
    // File 객체 사용 // application.properties의 경로 //uuid
    // 컨트롤러에서 받은 MultipartFile 을 byte타입으로 입력받음

    public String uploadFile(String uploadPath, MultipartFile multipartFile) throws Exception {

        String savedFileName = fileUpload.fileUpload(multipartFile, "N");
            //로컬 실행시 해당 주석 해제
//        log.info(Arrays.toString(multipartFile.getBytes()));

//        UUID uuid = UUID.randomUUID();
//        log.info("생성된 uuid : " + uuid);
//        //파일명  abcd // abcd.jpg   자르다 substring ( abcd.jpg 중에서 "." 구분자를 기준으로  )
//        String originalFilename = multipartFile.getOriginalFilename();
//        originalFilename = originalFilename.substring(originalFilename.lastIndexOf("\\")+1);
//        String extension = originalFilename.substring(multipartFile.getOriginalFilename().indexOf("."));
//
//        log.info("파일명 확장자 : " + extension);
//        // asdfkljlwjk123124  + .png 혹은 jpg
//        String savedFileName = uuid.toString() + extension;
//        // 전체 경로  /shop/item/asfjsajkl  +  .png
//        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
//
//        //물리적인 파일저장
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType(multipartFile.getContentType());
//            metadata.setContentLength(multipartFile.getSize());
//
//            // Upload the file to S3
//            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileUploadFullUrl, inputStream, metadata));
//
//            log.info("File uploaded successfully: {}", savedFileName);
//
//        } catch (IOException e) {
//            log.error("Error uploading file: {}", e.getMessage());
//            throw new RuntimeException("File upload failed", e);
//        }

//        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
//        //만든 객체를 저장합니다. //파라미터로 받은 byte[]타입의 변수
//        fos.write(multipartFile.getBytes());
//        //자원을 해제
//        fos.close();
        return savedFileName;
    }

}
