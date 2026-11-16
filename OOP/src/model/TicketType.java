package model;

public enum TicketType {
    STANDARD("Standard"),
    VIP("VIP");

    private final String displayName;

    TicketType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}