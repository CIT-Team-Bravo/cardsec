package ie.cit.teambravo.cardsec.dto;

public class ValidationDto {
    private String reason;
    private boolean validEvent;
    private Event currentEvent;
    private Event previousEvent;

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

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Event getPreviousEvent() {
        return previousEvent;
    }

    public void setPreviousEvent(Event previousEvent) {
        this.previousEvent = previousEvent;
    }
}
