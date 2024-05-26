package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;

public class EventSearchDto {
    private long dauer; //in Minuten
    private EventType typ;
    private String textSearch;

    public EventSearchDto(long dauer, EventType typ, String textSearch) {
        this.dauer = dauer;
        this.typ = typ;
        this.textSearch = textSearch;
    }

    public EventSearchDto() {
    }

    public long getDauer() {
        return dauer;
    }

    public EventType getTyp() {
        return typ;
    }

    public String getTextSearch() {
        return textSearch;
    }

    public EventSearchDto setDauer(long dauer) {
        this.dauer = dauer;
        return this;
    }

    public EventSearchDto setTyp(EventType typ) {
        this.typ = typ;
        return this;
    }

    public EventSearchDto setTextSearch(String textSearch) {
        this.textSearch = textSearch;
        return this;
    }
}
