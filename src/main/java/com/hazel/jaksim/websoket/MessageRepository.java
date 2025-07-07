package com.hazel.jaksim.websoket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    long countByRecieveUserAndVc(String recieveUser, String vc);

    @Modifying
    @Query("UPDATE Message m SET m.vc = '1', m.recieve_reg = CURRENT_TIMESTAMP " +
            "WHERE m.id = :id AND m.recieve_user = :username")
    void updateReadStatus(@Param("id") Long id, @Param("username") String username);
}
