package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.QnaDTO;
import com.lookatme.smartstay.dto.QnaReplyDTO;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.repository.QnaReplyRepository;
import com.lookatme.smartstay.repository.QnaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QnaReplyServiceImpl implements QnaReplyService {

    private final QnaReplyRepository qnaReplyRepository;
    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;

    @Override
    public void register(QnaReplyDTO qnaReplyDTO) {
        Optional<Qna> optionalQna =
                qnaRepository.findById(qnaReplyDTO.getQnaReply_num());

        Qna qna =
                qnaRepository.findById(qnaReplyDTO.getQnaReply_num()).orElseThrow(EntityNotFoundException::new);

        QnaReply qnaReply= modelMapper.map(qnaReplyDTO, QnaReply.class);
        qnaReply.setQna(qna);
        qnaReplyRepository.save(qnaReply);
    }

    @Override
    public QnaReplyDTO read(Long qnaReply_num) {

        QnaReply qnaReply = qnaReplyRepository.findById(qnaReply_num).orElseThrow(EntityNotFoundException::new);
        QnaReplyDTO qnaReplyDTO = modelMapper.map(qnaReply, QnaReplyDTO.class);

        return qnaReplyDTO;
    }

    @Override
    public List<QnaReplyDTO> list(Long qna_num) {
        List<QnaReply> qnaReplyList = qnaReplyRepository.findByQna_num(qna_num);

        List<QnaReplyDTO> list = qnaReplyList.stream()
                .map(qnaReply -> modelMapper.map(qnaReply, QnaReplyDTO.class).setQnaDTO(modelMapper.map(qnaReply.getQna(), QnaDTO.class)))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public QnaReplyDTO modify(QnaReplyDTO qnaReplyDTO) {
        log.info("댓글서비스 qnaReplyDTO: " + qnaReplyDTO);

        QnaReply qnareply =
                qnaReplyRepository.findById(qnaReplyDTO.getQnaReply_num())
                        .orElseThrow(EntityNotFoundException::new);

        qnareply.setComment(qnaReplyDTO.getComment());

        return modelMapper.map(qnaReplyDTO, QnaReplyDTO.class);
    }

    @Override
    public void del(Long qnaReply_num) {
        log.info(qnaReply_num);

        qnaReplyRepository.deleteById(qnaReply_num);

    }
}
