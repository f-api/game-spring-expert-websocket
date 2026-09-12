package com.example.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PositionService {
    public void update(
            Long roomId,
            String nickname,
            double x,
            double y
    ) {
        log.info("위치 갱신: room={} {} → ({}, {})", roomId, nickname, x, y);
    }
}
