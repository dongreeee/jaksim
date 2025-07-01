package com.hazel.jaksim.websoket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    long countByRecieveUserAndVc(String recieveUser, String vc);
}
