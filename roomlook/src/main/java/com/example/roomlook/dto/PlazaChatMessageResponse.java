package com.example.roomlook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlazaChatMessageResponse {
    private String id;              // 메시지 ID
    private String senderUserId;    // 보낸 사용자 ID
    private String senderNickname;  // 보낸 사용자 닉네임
    private String text;            // 메시지 내용
    private Long sentAtMillis;      // 메시지 전송 시각 (밀리초 단위)
}//.