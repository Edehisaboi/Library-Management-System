package domain.media;

import domain.Category;
import util.Validation;

import java.util.List;
import java.util.Set;

/**
 * Represents a DVD (Movie or TV Show) in the library.
 */
public final class DVD extends MediaItem {
    private final int durationMinutes;
    private final String regionCode;
    private final String rating;

    /**
     * Creates a new DVD.
     *
     * @param title           the movie title
     * @param directors       list of directors
     * @param year            release year
     * @param categories      genres
     * @param durationMinutes length in minutes
     * @param regionCode      DVD region code (e.g. "1", "2", "ALL")
     * @param rating          MPAA rating or equivalent (e.g. "PG", "R")
     */
    public DVD(String title, List<String> directors, int year, Set<Category> categories,
            int durationMinutes, String regionCode, String rating) {
        super(title, directors, year, categories);
        Validation.require(durationMinutes >= 1, "durationMinutes must be >= 1");
        this.durationMinutes = durationMinutes;
        this.regionCode = regionCode == null ? "" : regionCode.trim();
        this.rating = rating == null ? "" : rating.trim();
    }

    /**
     * Gets duration in minutes.
     * 
     * @return duration
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Gets the DVD region code.
     * 
     * @return region code
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Gets the content rating (e.g. PG-13).
     * 
     * @return rating string
     */
    public String getRating() {
        return rating;
    }

    @Override
    public String details() {
        return "Type: DVD\n" +
                "Title: " + getTitle() + "\n" +
                "Directors: " + String.join(", ", getCreators()) + "\n" +
                "Year: " + getYear() + "\n" +
                "Duration: " + durationMinutes + " min\n" +
                "Rating: " + rating + "\n" +
                "Region Code: " + regionCode + "\n" +
                "Categories: " + getCategories();
    }
}
