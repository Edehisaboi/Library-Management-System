package domain.media;

import domain.Category;
import util.Validation;

import java.util.List;
import java.util.Set;

public final class DVD extends MediaItem {
    private final int durationMinutes;
    private final String regionCode;
    private final String rating;

    public DVD(String title, List<String> directors, int year, Set<Category> categories,
            int durationMinutes, String regionCode, String rating) {
        super(title, directors, year, categories);
        Validation.require(durationMinutes >= 1, "durationMinutes must be >= 1");
        this.durationMinutes = durationMinutes;
        this.regionCode = regionCode == null ? "" : regionCode.trim();
        this.rating = rating == null ? "" : rating.trim();
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String details() {
        StringBuilder sb = new StringBuilder("[").append(durationMinutes).append(" min");
        if (!regionCode.isEmpty())
            sb.append(", R").append(regionCode);
        if (!rating.isEmpty())
            sb.append(", ").append(rating);
        sb.append("]");
        return sb.toString();
    }
}
