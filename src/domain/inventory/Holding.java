package domain.inventory;

import domain.media.MediaItem;
import util.Validation;

import java.util.Objects;
import java.util.UUID;

public final class Holding {
    private final UUID id;
    private final MediaItem item;
    private final String shelfLocation;
    private HoldingStatus status;

    public Holding(MediaItem item) {
        this(item, null);
    }

    public Holding(MediaItem item, String shelfLocation) {
        this.id = UUID.randomUUID();
        this.item = Objects.requireNonNull(item, "item cannot be null");
        this.status = HoldingStatus.AVAILABLE;
        this.shelfLocation = shelfLocation == null ? "" : shelfLocation.trim();
    }

    public UUID getId() {
        return id;
    }

    public MediaItem getItem() {
        return item;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public HoldingStatus getStatus() {
        return status;
    }

    public void markOnLoan() {
        Validation.require(status == HoldingStatus.AVAILABLE, "Holding must be AVAILABLE to loan");
        status = HoldingStatus.ON_LOAN;
    }

    public void markReturned() {
        Validation.require(status == HoldingStatus.ON_LOAN, "Holding must be ON_LOAN to return");
        status = HoldingStatus.AVAILABLE;
    }

    public void markLost() {
        Validation.require(status == HoldingStatus.ON_LOAN || status == HoldingStatus.AVAILABLE,
                "Illegal transition to LOST");
        status = HoldingStatus.LOST;
    }

    public void markDamaged() {
        Validation.require(status == HoldingStatus.ON_LOAN || status == HoldingStatus.AVAILABLE,
                "Illegal transition to DAMAGED");
        status = HoldingStatus.DAMAGED;
    }
}
