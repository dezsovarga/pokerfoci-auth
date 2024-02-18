package com.dezso.varga.pokerfoci.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDateTime date;
    private EventStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "EVENT_PARTICIPATION",
            joinColumns = @JoinColumn(name = "EVENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "PARTICIPATION_ID")
    )
    private List<Participation> participationList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "EVENT_EVENTLOG",
            joinColumns = @JoinColumn(name = "EVENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENTLOG_ID")
    )
    private List<EventLog> eventLogList;

    private String score;


    public Event() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<Participation> getParticipationList() {
        return participationList;
    }

    public void setParticipationList(List<Participation> participationList) {
        this.participationList = participationList;
    }

    public List<EventLog> getEventLogList() {
        return eventLogList;
    }

    public void setEventLogList(List<EventLog> eventLogList) {
        this.eventLogList = eventLogList;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
