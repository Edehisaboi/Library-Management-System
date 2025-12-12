package repo;

import domain.media.MediaItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for accessing the catalog of media titles.
 */
public interface MediaRepository {
    /**
     * Saves a media item to the repository.
     * 
     * @param item the item to save
     * @return the saved item
     */
    MediaItem save(MediaItem item);

    /**
     * Finds a media item by its unique ID.
     * 
     * @param id the item UUID
     * @return an Optional containing the item if found
     */
    Optional<MediaItem> findById(UUID id);

    /**
     * Retrieves all media items in the repository.
     * 
     * @return list of all items
     */
    List<MediaItem> findAll();
}
