package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findBySender(Member sender);

}
