package ie.cit.teambravo.cardsec.dto;

public class ValidationDto {
    private String reason;
    private boolean validEvent;
    private EventDto currentEvent;
    private EventDto previousEvent;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isValidEvent() {
        return validEvent;
    }

    public void setValidEvent(boolean validEvent) {
        this.validEvent = validEvent;
    }

    public EventDto getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(EventDto currentEvent) {
        this.currentEvent = currentEvent;
    }

    public EventDto getPreviousEvent() {
        return previousEvent;
    }

    public void setPreviousEvent(EventDto previousEvent) {
        this.previousEvent = previousEvent;
    }
}
