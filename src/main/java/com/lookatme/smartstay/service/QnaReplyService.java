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

    //수정
    public QnaReplyDTO modify(QnaReplyDTO qnaReplyDTO);

    //삭제
    public void del(Long qnaReply_num);
}
