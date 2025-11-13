package domain.media;

import domain.Category;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Book extends MediaItem {
    private final String isbn;
    private final String publisher;

    public Book(String title, List<String> authors, int year, Set<Category> categories, String isbn, String publisher) {
        super(title, authors, year, categories);
        this.isbn = isbn;
        this.publisher = publisher;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    @Override
    public String details() {
        return "\n───────────────────────────────\n" +
                " Title: " + getTitle() + "\n" +
                "───────────────────────────────\n" +
                " ID: " + getId() + "\n" +
                " Authors: " + String.join(", ", getCreators()) + "\n" +
                " Publisher: " + getPublisher() + "\n" +
                " Year: " + getYear() + "\n" +
                " ISBN: " + getIsbn() + "\n" +
                " Categories: " + getCategories() + "\n";
    }

    public static final class Builder {
        private String title;
        private final List<String> authors = new ArrayList<>();
        private int year;
        private String publisher;
        private String isbn;
        private final Set<Category> categories = new HashSet<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder addAuthor(String author) {
            if (author != null && !author.trim().isEmpty()) {
                authors.add(author.trim());
            }
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder addCategory(Category category) {
            if (category != null) {
                categories.add(category);
            }
            return this;
        }

        public Book build() {
            return new Book(title, authors, year, categories, isbn, publisher);
        }
    }
}
