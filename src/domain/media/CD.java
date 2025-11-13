package domain.media;

import domain.Category;
import util.Validation;

import java.util.List;
import java.util.Set;

public class CD extends MediaItem {
    private final int durationMinutes;
    private final int trackCount;

    public CD(String title, List<String> artists, int year, Set<Category> categories, int durationMinutes,
            int trackCount) {
        super(title, artists, year, categories);
        Validation.require(durationMinutes >= 1, "durationMinutes must be >= 1");
        Validation.require(trackCount >= 1, "trackCount must be >= 1");
        this.durationMinutes = durationMinutes;
        this.trackCount = trackCount;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getTrackCount() {
        return trackCount;
    }

    @Override
    public String details() {
        return "[" + durationMinutes + " min, " + trackCount + " tracks]";
    }
}
