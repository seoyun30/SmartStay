package com.lookatme.smartstay.service;

import com.lookatme.smartstay.Util.FileUpload;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;
    private final BrandRepository brandRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final MenuRepository menuRepository;
    private final CareRepository careRepository;
    private final ReviewRepository reviewRepository;
    private final QnaRepository qnaRepository;
    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    private final FileUpload fileUpload; //파일업로드 클래스 상속

    //이미지 등록
    public void saveImage(List<MultipartFile> imageFileList, String targetType, Long targetId) throws Exception {

        if (imageFileList != null && !imageFileList.isEmpty()) {
            for (MultipartFile file : imageFileList) {

                if (file.isEmpty()) {
                    log.warn("빈 파일이 업로드 되었습니다.:{}", file.getOriginalFilename());
                    continue;
                }

                String imageName = fileUpload.FileUpload(file);
                String originalName = file.getOriginalFilename();
                String imageUrl = "/images/image" + imageName; // 실제 서비스 시에는 S3 URL 등으로 변경해야 함

                Image image = Image.builder()
                        .image_name(imageName)
                        .origin_name(originalName)
                        .image_url(imageUrl)
                        .build();

                // targetType에 따라 연관관계 설정
                switch (targetType) {
                    case "brand":
                        Brand brand = brandRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드 정보를 찾을 수 없습니다."));
                        image.setBrand(brand);
                        break;
                    case "hotel":
                        Hotel hotel = hotelRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 호텔 정보를 찾을 수 없습니다."));
                        image.setHotel(hotel);
                        break;
                    case "room":
                        Room room = roomRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 룸 정보를 찾을 수 없습니다."));
                        image.setRoom(room);
                        break;
                    case "menu":
                        Menu menu = menuRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴 정보를 찾을 수 없습니다."));
                        image.setMenu(menu);
                        break;
                    case "care":
                        Care care = careRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 룸 케어 정보를 찾을 수 없습니다."));
                        image.setCare(care);
                        break;
                    case "review":
                        Review review = reviewRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보를 찾을 수 없습니다."));
                        image.setReview(review);
                        break;
                    case "qna":
                        Qna qna = qnaRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 Q&A 정보를 찾을 수 없습니다."));
                        image.setQna(qna);
                        break;
                    case "notice":
                        Notice notice = noticeRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항 정보를 찾을 수 없습니다."));
                        image.setNotice(notice);
                        break;
                    default:
                        throw new IllegalArgumentException("잘못된 targetType입니다.");
                }

                if (imageFileList.indexOf(file) == 0) {
                    image.setRepimg_yn("Y"); //대표이미지
                } else {
                    image.setRepimg_yn("N");
                }

                imageRepository.save(image);

            }
        }

    } //이미지 등록

    //이미지 삭제
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다."));

        fileUpload.FileDelete(image.getImage_name());
        imageRepository.delete(image);
    } //이미지 삭제

    //이미지 수정
    public void updateImage(List<MultipartFile> imageFileList, List<Long> delnumList, String targetType, Long targetId) throws Exception {

        // 1. 삭제할 이미지 처리
        if (delnumList != null && !delnumList.isEmpty()) {
            for (Long imageId : delnumList) {
                deleteImage(imageId); // 기존 deleteImage 메서드 재사용
            }
        }

        // 2. 추가/변경할 이미지 처리
        if (imageFileList != null && !imageFileList.isEmpty()) {

            List<Image> existingImages = imageRepository.findByTarget(targetType, targetId);

            for (MultipartFile file : imageFileList) {

                if (file.isEmpty()) {
                    log.warn("빈 파일이 업로드되었습니다: {}", file.getOriginalFilename());
                    continue;
                }

                String imageName = fileUpload.FileUpload(file);
                String originalName = file.getOriginalFilename();
                String imageUrl = "/images/image" + imageName;

                Image image = Image.builder()
                        .image_name(imageName)
                        .origin_name(originalName)
                        .image_url(imageUrl)
                        .build();

                switch (targetType) {
                    case "brand":
                        Brand brand = brandRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드 정보를 찾을 수 없습니다."));
                        image.setBrand(brand);
                        break;
                    case "hotel":
                        Hotel hotel = hotelRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 호텔 정보를 찾을 수 없습니다."));
                        image.setHotel(hotel);
                        break;
                    case "room":
                        Room room = roomRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 룸 정보를 찾을 수 없습니다."));
                        image.setRoom(room);
                        break;
                    case "menu":
                        Menu menu = menuRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴 정보를 찾을 수 없습니다."));
                        image.setMenu(menu);
                        break;
                    case "care":
                        Care care = careRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 룸 케어 정보를 찾을 수 없습니다."));
                        image.setCare(care);
                        break;
                    case "review":
                        Review review = reviewRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보를 찾을 수 없습니다."));
                        image.setReview(review);
                        break;
                    case "qna":
                        Qna qna = qnaRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 Q&A 정보를 찾을 수 없습니다."));
                        image.setQna(qna);
                        break;
                    case "notice":
                        Notice notice = noticeRepository.findById(targetId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항 정보를 찾을 수 없습니다."));
                        image.setNotice(notice);
                        break;
                    default:
                        throw new IllegalArgumentException("잘못된 targetType입니다.");
                }
                if (!existingImages.isEmpty()) {
                    if (imageFileList.indexOf(file) == 0) {
                        existingImages.stream()
                                .filter(img -> "Y".equals(img.getRepimg_yn()))
                                .forEach(img -> {deleteImage(img.getImage_id());});
                        image.setRepimg_yn("Y");
                    } else {
                        image.setRepimg_yn("N");
                    }
                } else {
                    image.setRepimg_yn(imageFileList.indexOf(file) == 0 ? "Y" : "N");
                }

                imageRepository.save(image);
            }
        }
    }

    //targetType과 targetId로 이미지 목록을 찾는 메서드 추가
    public List<Image> findImagesByTarget(String targetType, Long targetId){
        return imageRepository.findByTarget(targetType, targetId);
    }

}
