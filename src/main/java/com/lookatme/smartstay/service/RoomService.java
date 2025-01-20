package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.RoomDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomService {

    public void roomRegister(RoomDTO roomDTO, List<MultipartFile> multipartFileList) {

    }
}
