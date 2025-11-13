package repo;

import domain.media.MediaItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaRepository {
    MediaItem save(MediaItem item);

    Optional<MediaItem> findById(UUID id);

    List<MediaItem> findAll();
}
