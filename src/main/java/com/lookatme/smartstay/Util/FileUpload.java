package com.lookatme.smartstay.Util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

//파일업드에 관련된 메소드를 담은 클래스
//poster.jpg==>poster.jpg 저장, poster.jpg새로 저장==>poster.jpg 기존내용되고 새로운파일 저장
//poster.jpg==>32423sd324dsrw3d.jpg 중복되지 않는 이름으로 파일 저장
//UUID : 파일이름을 난수로 생성
@Configuration
public class FileUpload {
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
    public String FileUpload(MultipartFile imageFile) {
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
            // 썸네일 생성
            createThumbnail(path, filename);

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
            int maxWidth = 1000;
            int maxHeight = 1000;

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

     /*---------------------------
    함수명 : void FileDelete(String imgLocation, MultipartFile imageFileName)
    인수 : 저장될 위치, 이미지파일
    출력 : 저장후 생성된 새로운 파일명
    설명 : 이미지파일을 새로운 이름으로 지정된 폴더에 저장하고 새로운 이름을 전달
    */
    public void FileDelete(String imageFileName) {
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

        }
    }
}
