package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Menu;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class MenuService {

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final HotelRepository hotelRepository;

    public void menuRegister(MenuDTO menuDTO, Long hotel_num, List<MultipartFile> multipartFiles, Long mainImageIndex) throws Exception{

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Menu menu = modelMapper.map(menuDTO, Menu.class);
        menu.setHotel(hotel);
        menu = menuRepository.save(menu);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            imageService.saveImage(multipartFiles, "menu", menu.getMenu_num());
        }
    }

    public MenuDTO menuRead(Long menu_num) {

        Menu menu = menuRepository.findById(menu_num).orElseThrow(EntityNotFoundException::new);

        MenuDTO menuDTO = modelMapper.map(menu, MenuDTO.class);

        Hotel hotel = menu.getHotel();
        if (hotel != null) {
            HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
            menuDTO.setHotelDTO(hotelDTO);
        }

        List<Image> imageList = imageRepository.findByTarget("menu", menu_num);

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());

            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id).collect(Collectors.toList());

            menuDTO.setImageDTOList(imageDTOList);
            menuDTO.setImageIdList(imageIdList);
        }

        return menuDTO;
    }

    //전체 메뉴
    public PageResponseDTO<MenuDTO> menuList(HotelDTO hotelDTO, PageRequestDTO pageRequestDTO) {

        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findByHotel(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }


    //한식 메뉴만
    public PageResponseDTO<MenuDTO> koreanMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findKoreanMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    //중식 메뉴만
    public PageResponseDTO<MenuDTO> chineseMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findChineseMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    //일식 메뉴만
    public PageResponseDTO<MenuDTO> japaneseMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findJapaneseMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    //양식 메뉴만
    public PageResponseDTO<MenuDTO> westernMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findWesternMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    //분식 메뉴만
    public PageResponseDTO<MenuDTO> snackMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findSnackMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    //음료 메뉴만
    public PageResponseDTO<MenuDTO> drinkMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findDrinkMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    //기타 메뉴만
    public PageResponseDTO<MenuDTO> etcMenuList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("menu_num");

        Page<Menu> result = menuRepository.findEtcMenu(hotel, pageable);

        List<MenuDTO> menuDTOList = result.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        if (menuDTOList == null) {
            menuDTOList = Collections.emptyList();
        }

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = PageResponseDTO.<MenuDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(menuDTOList).total((int) result.getTotalElements()).build();

        return menuDTOPageResponseDTO;
    }

    public void menuModify(MenuDTO menuDTO, List<MultipartFile> multipartFiles, HotelDTO hotelDTO, List<Long> delnumList) throws Exception {

        Menu menu = menuRepository.findById(menuDTO.getMenu_num()).orElseThrow(EntityNotFoundException::new);

        if (!menu.getHotel().getHotel_num().equals(hotelDTO.getHotel_num())) {
            throw new Exception();
        }

        Hotel hotel = hotelRepository.findById(hotelDTO.getHotel_num()).orElseThrow(() -> new EntityNotFoundException("해당 호텔 정보 찾을 수 없음."));
        menu.setHotel(hotel);

        menu.setMenu_name(menuDTO.getMenu_name());
        menu.setMenu_sort(menuDTO.getMenu_sort());
        menu.setMenu_detail(menuDTO.getMenu_detail());
        menu.setMenu_price(menuDTO.getMenu_price());

        menu = menuRepository.save(menu);

        boolean hasNewImages = multipartFiles != null && multipartFiles.stream().anyMatch(file -> !file.isEmpty());
        boolean hasDeletedImages = delnumList != null && !delnumList.isEmpty();

        if (hasNewImages || hasDeletedImages) {
            log.info("이미지 업데이트 실행");
            try {
                imageService.updateImage(
                        hasNewImages ? multipartFiles : null,
                        hasDeletedImages ? delnumList : null,
                        "menu",
                        menu.getMenu_num()
                );
            } catch (IndexOutOfBoundsException e) {
                log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
                throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
            }
        } else {
            log.info("이미지 업데이트 없이 텍스트 정보만 수정");
        }

    }

    public void menuDelete(Long id) {

        List<Image> images = imageRepository.findByTarget("menu", id);

        if (images == null || images.isEmpty()) {
            log.info("삭제하려는 메뉴 이미지가 없습니다. menu_num:" + id);
        }else {
            for (Image image : images) {
                try {
                    imageService.deleteImage(image.getImage_id());
                    imageRepository.deleteById(image.getImage_id());
                }catch (Exception e) {
                    log.info("이미지 삭제 실패 :" + e.getMessage());
                }
            }
        }
        menuRepository.deleteById(id);
        log.info("메뉴가 삭제되었습니다. menu_num:" + id);
    }

    public List<MenuDTO> searchList(String query) {

        List<Menu> menus = menuRepository.findByMenu_nameContaining(query);

        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        List<MenuDTO> menuDTOS = menus.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)).collect(Collectors.toList());

        return menuDTOS;
    }


}
