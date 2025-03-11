package com.lookatme.smartstay.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReserveSearchDTO {

    private String reserve_id;
    private String hotel_name;
    private String room_name;
    private String reserve_name;
    private String state;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sdate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date edate;

    // Date -> LocalDateTime 변환 메서드 추가
    public LocalDateTime getSdateAsLocalDateTime() {
        return (sdate != null) ? sdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    public LocalDateTime getEdateAsLocalDateTime() {
        return (edate != null) ? edate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }
}
