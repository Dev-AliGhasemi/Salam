package ir.vira.salam.Enumerations;

public enum EventType {
    JOIN("join"),
    NEW_MSG("new msg");


    private String eventType;

    EventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return eventType;
    }
}
