package com.everyparking.server.data.entity;

/**
 * 차량 등록 상태
 * APPROVED : 등록됨
 * WAITING : 대기
 * DENIED : 거부
 * NONE : none
 */
public enum CarStatus {
    APPROVED,
    WAITING,
    DENIED,
    NONE,
}
