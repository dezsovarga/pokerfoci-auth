package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventLog;
import com.dezso.varga.pokerfoci.domain.Team;
import com.dezso.varga.pokerfoci.domain.TeamMember;
import com.dezso.varga.pokerfoci.domain.TeamVariation;
import com.dezso.varga.pokerfoci.dto.EventLogsDto;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.TeamDto;
import com.dezso.varga.pokerfoci.dto.TeamVariationDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventConverterImpl implements EventConverter {

    private final AccountConverter accountConverter;
    private final EventRepository eventRepository;

    @Override
    public Event fromCreateEventDtoToEvent(CreateEventDto createEventDto) {

        LocalDateTime eventDateTime = Instant.ofEpochMilli(createEventDto.getEventDateEpoch())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Event.builder()
                .date(eventDateTime)
                .participationList(accountConverter.fromAccountNameListToEventParticipationList(createEventDto.getRegisteredPlayers()))
                .eventLogList(new ArrayList<>())
                .build();
    }

    @Override
    public EventResponseDto fromEventToEventResponseDto(Event event) {
        List<AccountWithSkillDto> registeredPlayers = accountConverter.fromParticipationListToAccountWithSkillDtoList(event.getParticipationList());
        for (AccountWithSkillDto participant: registeredPlayers) {
            if (participant.getRegistrationDate() == null) {
                participant.setRegistrationDate(LocalDateTime.now());
            }
        }
        registeredPlayers.sort(Comparator.comparing(AccountWithSkillDto::getRegistrationDate));

        List<EventLogsDto> eventLogsDto = this.fromEventLogsListToEventLogsDtoList(event.getEventLogList());
        eventLogsDto.sort(Comparator.comparing(EventLogsDto::getLogTime));

        List<TeamVariationDto> teamVariationDtoList = this.fromTeamVariationListToTeamVariationDtoList(event.getTeamVariations());

        return EventResponseDto.builder()
                .id(event.getId())
                .eventDateTime(event.getDate())
                .status(event.getStatus())
                .registeredPlayers(registeredPlayers)
                .teamVariations(teamVariationDtoList)
                .eventLogs(eventLogsDto)
                .build();
    }

    @Override
    public List<EventResponseDto> fromEventListToEventResponseDtoList(List<Event> eventList) {

        return eventList.stream().map(this::fromEventToEventResponseDto).collect(Collectors.toList());
    }

    @Override
    public EventLogsDto fromEventLogsToEventLogsDto(EventLog evenLogs) {
        return EventLogsDto.builder()
                .logMessage(evenLogs.getLogMessage())
                .logTime(evenLogs.getLogTime())
                .build();
    }

    @Override
    public List<EventLogsDto> fromEventLogsListToEventLogsDtoList(List<EventLog> eventLogsList) {
        return eventLogsList.stream().map(this::fromEventLogsToEventLogsDto).collect(Collectors.toList());
    }

    @Override
    public List<TeamVariationDto> fromTeamVariationListToTeamVariationDtoList(List<TeamVariation> teamVariations) {
        return teamVariations.stream().map(this::fromTeamVariationToTeamVariationDto).collect(Collectors.toList());
    }

    @Override
    public TeamVariationDto fromTeamVariationToTeamVariationDto(TeamVariation teamVariation) {
        return new TeamVariationDto(
                fromTeamToTeamDto(teamVariation.getTeam1()),
                fromTeamToTeamDto(teamVariation.getTeam2()),
                teamVariation.getSkillDifference()
        );
    }

    @Override
    public TeamDto fromTeamToTeamDto(Team team) {
        return new TeamDto(
                accountConverter
                        .fromAccountListToAccountWithSkillDtoList(
                                team.getTeamMembers().stream().map(TeamMember::getAccount).collect(Collectors.toList())),
                                team.getSkillSum()
        );
    }

}
