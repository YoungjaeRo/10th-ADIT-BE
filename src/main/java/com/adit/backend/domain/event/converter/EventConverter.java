package com.adit.backend.domain.event.converter;

import org.springframework.stereotype.Component;

import com.adit.backend.domain.event.dto.request.EventRequestDto;
import com.adit.backend.domain.event.dto.request.EventUpdateRequestDto;
import com.adit.backend.domain.event.dto.response.EventResponseDto;
import com.adit.backend.domain.event.entity.Event;

@Component
public class EventConverter {

    public Event toEntity(EventRequestDto request) {
        return Event.createEvent(
            request.getName(),
            request.getCategory(),
            request.getStartDate(),
            request.getEndDate(),
            request.getMemo(),
            request.getVisited()
        );
    }

    public EventResponseDto toResponse(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .name(event.getName())
                .category(event.getCategory())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .memo(event.getMemo())
                .visited(event.getVisited())
                .build();
    }

    public void updateEntity(Event event, EventUpdateRequestDto request) {
        event.updateEvent(request);  // Event 엔터티의 update 메서드 호출
    }
}