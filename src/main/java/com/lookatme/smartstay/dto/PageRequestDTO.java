package com.lookatme.smartstay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

  // 클라이언트로부터 입력받은 url 및 파라미터 정보 controller에서 파라미터 수집

  @Builder.Default
  private int page = 1; //페이지 현재

  @Builder.Default
  private int size = 10; //사이즈 10개씩

  private String type; //검색 종류 셀렉트박스
                        //t, c, w, tc, tw, twc

  private String keyword; //검색어

  private String link; //주소

  private String searchDateType; // all , 1d, 1w, 1m, 6m
                                //<select>
  //<option value="all">전체</option>
  //<option value="1d">하루전</option>
  //<option value="1w">1주전</option>
  // </select>


  public String[] getTypes(){

    if (type == null || type.isEmpty()){

      return null;

    }

    return type.split("");

  }

  public Pageable getPageable(String...props){

    return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());

  }

  public String getLink(){

    if (link == null){
      StringBuffer buffer = new StringBuffer();

      buffer.append("page=" + this.page);
      buffer.append("&size=" + this.size);

      if (type != null && type.length() > 0){

        buffer.append("&type=" + type);

      }

      if (keyword != null){

        try {
          buffer.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
        } catch (UnsupportedEncodingException e){

        }

      }

      link = buffer.toString();

    }

    return link;

  }



}
