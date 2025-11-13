package repo;

import domain.inventory.Holding;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {
    Holding save(Holding h);

    Optional<Holding> findById(UUID id);

    List<Holding> findByMediaId(UUID mediaId);

    void update(Holding h);
}
