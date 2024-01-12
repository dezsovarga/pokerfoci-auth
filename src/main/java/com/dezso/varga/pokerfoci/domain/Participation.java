package com.dezso.varga.pokerfoci.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    private Account account;

    private LocalDateTime registrationDate;

    public Participation() {
    }

    public Participation(Account account, LocalDateTime registrationDate) {
        this.account = account;
        this.registrationDate = registrationDate;
    }
}
