package repo;

import domain.inventory.Holding;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for accessing and managing physical inventory (Holdings).
 */
public interface InventoryRepository {
    /**
     * Saves or updates a Holding.
     * 
     * @param h the holding to save
     * @return the saved holding
     */
    Holding save(Holding h);

    /**
     * Finds a holding by its unique ID.
     * 
     * @param id the holding UUID
     * @return an Optional containing the holding if found
     */
    Optional<Holding> findById(UUID id);

    /**
     * Finds all holdings associated with a specific media title.
     * 
     * @param mediaId the media item UUID
     * @return list of holdings
     */
    List<Holding> findByMediaId(UUID mediaId);
}
