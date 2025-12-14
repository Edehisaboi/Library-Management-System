package domain.media;

import domain.Category;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Book in the library catalog.
 */
public final class Book extends MediaItem {
    private String isbn;
    private String publisher;

    /**
     * Creates a new Book instance.
     *
     * @param title      the title of the book
     * @param authors    list of authors
     * @param year       publication year
     * @param categories set of categories
     * @param isbn       ISBN-13 or ISBN-10
     * @param publisher  publisher name
     */
    public Book(String title, List<String> authors, int year, Set<Category> categories, String isbn, String publisher) {
        super(title, authors, year, categories);
        this.isbn = isbn;
        this.publisher = publisher;
    }

    /**
     * Creates a Builder for constructing a Book step-by-step.
     * 
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the ISBN of the book.
     * 
     * @return the ISBN string
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     * 
     * @param isbn the new ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the publisher of the book.
     * 
     * @return the publisher name
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     * 
     * @param publisher the new publisher name
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String details() {
        return "Type: Book\n" +
                "Title: " + getTitle() + "\n" +
                "Authors: " + String.join(", ", getCreators()) + "\n" +
                "Publisher: " + publisher + "\n" +
                "Year: " + getYear() + "\n" +
                "ISBN: " + isbn + "\n" +
                "Categories: " + getCategories();
    }

    /**
     * Builder pattern for creating Book objects comfortably.
     */
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
