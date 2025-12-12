package domain.inventory;

import domain.media.MediaItem;
import util.Validation;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a physical copy (a holding) of a media item in the library
 * inventory.
 * Each holding has a unique ID and a status (e.g. AVAILABLE, ON_LOAN).
 */
public final class Holding {
    private final UUID id;
    private final MediaItem item;
    private final String shelfLocation;
    private HoldingStatus status;

    /**
     * Creates a new holding for a media item.
     * 
     * @param item the media item this holding represents
     */
    public Holding(MediaItem item) {
        this(item, null);
    }

    /**
     * Creates a new holding with a specific shelf location.
     *
     * @param item          the media item
     * @param shelfLocation description of where it is stored
     */
    public Holding(MediaItem item, String shelfLocation) {
        this.id = UUID.randomUUID();
        this.item = Objects.requireNonNull(item, "item cannot be null");
        this.status = HoldingStatus.AVAILABLE;
        this.shelfLocation = shelfLocation == null ? "" : shelfLocation.trim();
    }

    /**
     * Gets the unique ID of this holding.
     * 
     * @return the UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the media item this holding refers to.
     * 
     * @return the media item
     */
    public MediaItem getItem() {
        return item;
    }

    /**
     * Gets the physical shelf location.
     * 
     * @return location string
     */
    public String getShelfLocation() {
        return shelfLocation;
    }

    /**
     * Gets the current status of the holding.
     * 
     * @return the status enum
     */
    public HoldingStatus getStatus() {
        return status;
    }

    /**
     * Updates status to ON_LOAN.
     * Throws exception if item is not currently AVAILABLE.
     */
    public void markOnLoan() {
        Validation.require(status == HoldingStatus.AVAILABLE, "Holding must be AVAILABLE to loan");
        status = HoldingStatus.ON_LOAN;
    }

    /**
     * Updates status to AVAILABLE.
     * Throws exception if item is not currently ON_LOAN.
     */
    public void markReturned() {
        Validation.require(status == HoldingStatus.ON_LOAN, "Holding must be ON_LOAN to return");
        status = HoldingStatus.AVAILABLE;
    }

    /**
     * Marks the item as LOST.
     * Allowed from AVAILABLE or ON_LOAN states.
     */
    public void markLost() {
        Validation.require(status == HoldingStatus.ON_LOAN || status == HoldingStatus.AVAILABLE,
                "Illegal transition to LOST");
        status = HoldingStatus.LOST;
    }

    /**
     * Marks the item as DAMAGED.
     * Allowed from AVAILABLE or ON_LOAN states.
     */
    public void markDamaged() {
        Validation.require(status == HoldingStatus.ON_LOAN || status == HoldingStatus.AVAILABLE,
                "Illegal transition to DAMAGED");
        status = HoldingStatus.DAMAGED;
    }
}
