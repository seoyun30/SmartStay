package com.lookatme.smartstay.Util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.UUID;

//파일업드에 관련된 메소드를 담은 클래스
//poster.jpg==>poster.jpg 저장, poster.jpg새로 저장==>poster.jpg 기존내용되고 새로운파일 저장
//poster.jpg==>32423sd324dsrw3d.jpg 중복되지 않는 이름으로 파일 저장
//UUID : 파일이름을 난수로 생성
@Configuration
public class FileUpload {

    private final S3Client s3Client;

    // S3Client를 생성자 주입으로 받음
    public FileUpload(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${imgUploadLocation}") //이미지가 저장될 위치
    private String imgLocation;

    @Value("${thumbnailLocation}") // 썸네일을 저장할 위치
    private String thumbnailLocation;

    /*---------------------------
    함수명 : String FileUpload(String imgLocation, MultipartFile imageFile)
    인수 : 저장될 위치, 이미지파일
    출력 : 저장후 생성된 새로운 파일명
    설명 : 이미지파일을 새로운 이름으로 지정된 폴더에 저장하고 새로운 이름을 전달
    */
    public String fileUpload(MultipartFile imageFile, String createThumbnail) {
        // 이미지파일에 파일명을 읽어온다. sample.jpg
        String originalFilename = imageFile.getOriginalFilename();
        // 확장자 분리 .jpg
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID(); // 난수로 이름을 생성
        String filename = uuid.toString() + extension; // 새로운이름에 확장자 결합

        // S3에 파일 업로드
        try (InputStream inputStream = imageFile.getInputStream()) {
            // S3 객체 업로드 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(imgLocation) // 이미지 파일이 저장될 버킷
                    .key(filename) // 파일 이름
                    .contentType(imageFile.getContentType()) // 파일의 content type
                    .build();

            // S3에 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, imageFile.getSize()));
            System.out.println("파일 업로드 완료: " + filename);

            // 썸네일 생성 여부 확인
            if ("Y".equalsIgnoreCase(createThumbnail)) {
                createThumbnail(filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 실패 시 null 반환
        }

        return filename; // 성공 시 새 파일 이름 반환
    }

    private void createThumbnail(String filename) {
        try {
            // S3에서 원본 이미지 가져오기
            String originalImagePath = "s3://" + imgLocation + "/" + filename;
            BufferedImage img = ImageIO.read(new URL(originalImagePath));
            if (img == null) {
                throw new IllegalArgumentException("이미지를 읽을 수 없습니다: " + originalImagePath);
            }

            // 원본 크기 가져오기
            int originalWidth = img.getWidth();
            int originalHeight = img.getHeight();

            // 원하는 최대 크기
            int maxWidth = 350;
            int maxHeight = 350;

            // 비율 계산
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double scaleFactor = Math.min(widthRatio, heightRatio); // 더 작은 비율로 맞추기

            int newWidth = (int) (originalWidth * scaleFactor);
            int newHeight = (int) (originalHeight * scaleFactor);

            // 썸네일 생성
            ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
            Thumbnails.of(img)
                    .size(newWidth, newHeight)
                    .outputFormat("jpg")
                    .toOutputStream(thumbnailStream);

            // 썸네일 파일명
            String thumbnailFilename = "thumb_" + filename;

            // S3에 썸네일 업로드
            PutObjectRequest thumbnailRequest = PutObjectRequest.builder()
                    .bucket(imgLocation) // S3 버킷 위치
                    .key(thumbnailFilename) // 썸네일 파일 이름
                    .contentType("image/jpeg") // 썸네일 content type
                    .build();

            s3Client.putObject(thumbnailRequest, RequestBody.fromBytes(thumbnailStream.toByteArray()));

            System.out.println("썸네일 업로드 완료: " + thumbnailFilename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public String fileUpload(MultipartFile imageFile, String createThumbnail) {
        //이미지파일에 파일명을 읽어온다. sample.jpg
        String originalFilename = imageFile.getOriginalFilename();
        //확장자인 분리 .jpg
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID(); //난수로 이름을 생성 432-erw3342-4324
        String filename = uuid.toString() + extension; //새로운이름에 확장자 결합,432-erw3342-4324.jpg

        //c:/movie/432-erw3342-4324.jpg
        String path = imgLocation + filename; //최종 저장될 위치와 파일명
        System.out.println(path);
        //외부작업은 반드시 try~catch로 예외처리
        try {
            // 폴더가 없으면 생성
            File folder = new File(imgLocation);
            if (!folder.exists()) {
                boolean result = folder.mkdirs();
                if (!result) {
                    throw new IOException("폴더 생성 실패: " + imgLocation);
                }
            }
            // 파일 저장 (try-with-resources 사용)
            try (FileOutputStream fos = new FileOutputStream(new File(path))) {
                byte[] filedata = imageFile.getBytes();
                fos.write(filedata); // 파일 쓰기
            }
            // 썸네일 생성 여부 확인
            if ("Y".equalsIgnoreCase(createThumbnail)) {
                createThumbnail(path, filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 실패 시 null 반환
        }

        return filename; // 성공 시 새 파일 이름 반환
    }

    private void createThumbnail(String imagePath, String filename) {
        try {
            // 파일 이름에서 확장자 제거 및 안전한 파일 이름 생성
            int dotIndex = filename.lastIndexOf('.');
            String safeFilename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
            String baseFilename = (dotIndex != -1) ? safeFilename.substring(0, dotIndex) : safeFilename;

            // 원본 이미지와 동일한 경로 사용 (imagePath 기반)
            String baseDirectory = new File(imagePath).getParent(); // imagePath의 부모 디렉토리 가져오기
            String thumbnailPath = baseDirectory + "/thumb" + baseFilename + ".jpg";

            // 디버깅 로그 추가
            System.out.println("Original imagePath: " + imagePath);
            System.out.println("Thumbnail path: " + thumbnailPath);

            // 디렉토리 생성 (필요 시)
            File folder = new File(baseDirectory);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("폴더 생성 실패: " + baseDirectory);
            }

            // 원본 이미지 읽기
            BufferedImage img = ImageIO.read(new File(imagePath));
            if (img == null) {
                throw new IllegalArgumentException("이미지를 읽을 수 없습니다: " + imagePath);
            }

            // 원본 크기 가져오기
            int originalWidth = img.getWidth();
            int originalHeight = img.getHeight();

            // 원하는 최대 크기
            int maxWidth = 350;
            int maxHeight = 350;

            // 비율 계산
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double scaleFactor = Math.min(widthRatio, heightRatio); // 더 작은 비율로 맞추기

            int newWidth = (int) (originalWidth * scaleFactor);
            int newHeight = (int) (originalHeight * scaleFactor);

            // 썸네일 생성
            Thumbnails.of(img)
                    .size(newWidth, newHeight)
                    .outputFormat("jpg")
                    .toFile(new File(thumbnailPath));

            System.out.println("썸네일 저장 경로: " + thumbnailPath);
            System.out.println("썸네일 파일 존재 여부: " + new File(thumbnailPath).exists());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

     /*---------------------------
    함수명 : void FileDelete(String imgLocation, MultipartFile imageFileName)
    인수 : 저장될 위치, 이미지파일
    출력 : 저장후 생성된 새로운 파일명
    설명 : 이미지파일을 새로운 이름으로 지정된 폴더에 저장하고 새로운 이름을 전달
    */

    public void fileDelete(String imageFileName) {
        // S3에 저장된 파일 경로를 설정
        String imageFilePath = imgLocation + imageFileName; // 예: images/432erw-234w342.jpg
        String thumbnailFilePath = thumbnailLocation + "thumb" + imageFileName; // 예: images/thumb432erw-234w342.jpg

        try {
            // 원본 이미지 파일 삭제
            DeleteObjectRequest deleteImageRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageFilePath)
                    .build();
            s3Client.deleteObject(deleteImageRequest);
            System.out.println("파일 삭제 완료: " + imageFilePath);

            // 썸네일 파일 삭제
            DeleteObjectRequest deleteThumbnailRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailFilePath)
                    .build();
            s3Client.deleteObject(deleteThumbnailRequest);
            System.out.println("썸네일 삭제 완료: " + thumbnailFilePath);

        } catch (S3Exception e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public void fileDelete(String imageFileName) {
        //imgLocation(c:/movie), imageFileName(432erw-234w342.jpg)
        String deleteFileName = imgLocation + imageFileName; //c:/movie/432erw-234w342.jpg

        try {
            File deleteFile = new File(deleteFileName); //삭제할 파일
            if (deleteFile.exists()) { //해당하는 파일이 존재하면
                  deleteFile.delete(); //파일삭제
            }

            String deleteThumbnailPath = thumbnailLocation + "thumb" + imageFileName;
            File deleteThumbnail = new File(deleteThumbnailPath);
            if (deleteThumbnail.exists()) {
                deleteThumbnail.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */
}
