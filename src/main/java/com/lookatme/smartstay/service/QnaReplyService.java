package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.QnaReplyDTO;

import java.util.List;

public interface QnaReplyService {

    //등록
    public void register(QnaReplyDTO qnaReplyDTO);

    //읽기
    public QnaReplyDTO read(Long qnaReply_num);

    //목록
    public List<QnaReplyDTO> list(Long qnaReply_num);

    //public Page<ReplyDTO> listPage(Long qnaReply_num, Pageable pageable);

    //목록 페이징처리, 일반 컨트롤러 일때
    //public int totalEl();

    //목록 페이징 처리 restController일때
    //public PageResponseDTO<QnaReplyDTO> pageList(PageRequestDTO pageRequestDTO, Long bno);

    //수정
    public QnaReplyDTO modify(QnaReplyDTO qnaReplyDTO);

    //삭제
    public void del(Long qnaReply_num);
}
