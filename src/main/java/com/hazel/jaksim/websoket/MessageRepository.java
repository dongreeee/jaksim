package com.hazel.jaksim.websoket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    long countByRecieveUserAndVc(String recieveUser, String vc);

    List<Message> findByRecieveUser(String recieveUser);

    @Modifying
    @Query("UPDATE Message m SET m.vc = '1', m.recieveReg = CURRENT_TIMESTAMP " +
            "WHERE m.id = :id AND m.recieveUser = :username")
    void updateReadStatus(@Param("id") Long id, @Param("username") String username);

//    repository.save()는 전체 엔티티 객체를 직접 조회 -> 수정 -> 저장할때 사용하는 방식
//    단점 : 쿼리가 2번 실행 (조회 -> 업데이트) , 값 하나를 위해 전체 엔티티를 불러오는 건 비효율 적이다, 직접 조건의 어려움

    @Query("SELECT new com.example.dto.MessageDto(m.id, u.displayName, m.recieveUser, m.sendReg, m.vc, m.calendar.id) " +
            "FROM Message m JOIN m.sendUser u WHERE m.recieveUser = :recieveUser")
    List<MessageDto> findMessageDtosByRecieveUser(@Param("recieveUser") String recieveUser);

}
