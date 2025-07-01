package com.hazel.jaksim.websoket;

import com.hazel.jaksim.calendar.Calendar;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "send_user", nullable = false)
    private String sendUser;

    @Column(name = "send_reg", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sendReg;

    @Column(name = "recieve_user", nullable = false)
    private String recieveUser;

    @Column(name = "recieve_reg")
    private LocalDateTime recieveReg;

    @Column(name = "vc", length = 10)
    private String vc = "0";

    @ManyToOne
    @JoinColumn(name = "calendar_id", foreignKey = @ForeignKey(name = "fk_calendar"))
    private Calendar calendar;


}
