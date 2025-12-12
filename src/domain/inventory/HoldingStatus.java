package domain.inventory;

/**
 * Enumeration of possible states for a physical inventory item.
 */
public enum HoldingStatus {
    // The item is on the shelf and ready to be borrowed.
    AVAILABLE,
    // The item is currently borrowed by a member.
    ON_LOAN,
    // The item is marked as lost and not available.
    LOST,
    // The item is damaged and pulled from circulation.
    DAMAGED
}
