package domain.media;

import domain.Category;
import util.Validation;

import java.util.List;
import java.util.Set;

/**
 * Represents a Music CD (Compact Disc) in the library.
 */
public class CD extends MediaItem {
    private int durationMinutes;
    private int trackCount;

    /**
     * Creates a new CD.
     *
     * @param title           the album title
     * @param artists         list of artists/bands
     * @param year            release year
     * @param categories      set of music genres/categories
     * @param durationMinutes total duration in minutes
     * @param trackCount      number of tracks on the disc
     */
    public CD(String title, List<String> artists, int year, Set<Category> categories, int durationMinutes,
            int trackCount) {
        super(title, artists, year, categories);
        setDurationMinutes(durationMinutes);
        setTrackCount(trackCount);
    }

    /**
     * Gets total duration in minutes.
     * 
     * @return duration
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Sets the duration in minutes.
     * 
     * @param durationMinutes duration in minutes
     */
    public void setDurationMinutes(int durationMinutes) {
        Validation.require(durationMinutes >= 1, "durationMinutes must be >= 1");
        this.durationMinutes = durationMinutes;
    }

    /**
     * Gets the number of tracks.
     * 
     * @return track count
     */
    public int getTrackCount() {
        return trackCount;
    }

    /**
     * Sets the number of tracks.
     * 
     * @param trackCount number of tracks
     */
    public void setTrackCount(int trackCount) {
        Validation.require(trackCount >= 1, "trackCount must be >= 1");
        this.trackCount = trackCount;
    }

    @Override
    public String details() {
        return "Type: CD\n" +
                "Title: " + getTitle() + "\n" +
                "Artists: " + String.join(", ", getCreators()) + "\n" +
                "Year: " + getYear() + "\n" +
                "Duration: " + durationMinutes + " min\n" +
                "Tracks: " + trackCount + "\n" +
                "Categories: " + getCategories();
    }
}
