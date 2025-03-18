package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.ActiveState;
import com.lookatme.smartstay.constant.RoomState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HotelDTO {

    private Long hotel_num;

    private String business_num;

    private String hotel_name;

    private String owner;

    private String address;

    private String tel;

    private String score;

    private BrandDTO brandDTO;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public Long lowestPrice;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    private ActiveState active_state;

    private ImageDTO mainImage;

    private int review_count;

    private List<RoomDTO> rooms = new ArrayList<>();

    public boolean hasAvailableRooms() {
        return rooms.stream()
                .anyMatch(roomDTO -> roomDTO.getRoom_state() == RoomState.YES);
    }

    public HotelDTO setBrandDTO(BrandDTO brandDTO) {
        this.brandDTO = brandDTO;
        return this;
    }
}
