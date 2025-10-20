package books;

import java.util.*;

public final class Book {
    private final UUID ID;

    private final String title;
    private final List<String> authors;
    private final String publisher;
    private final int publicationYear;
    private final String isbn;

    private final Set<Category> categories;

    private Book(
            String title,
            List<String> authors,
            String publisher,
            int publicationYear,
            String isbn,
            Set<Category> categories
    ) {
        this.ID = UUID.randomUUID();
        this.title = Objects.requireNonNull(title, "Title cannot be null!");
        this.authors = List.copyOf(Objects.requireNonNull(authors, "Authors cannot be null!"));
        this.publisher = Objects.requireNonNull(publisher, "Publisher cannot be null!");
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.categories = categories;
    }

    public UUID getID() { return ID; }
    public String getTitle() { return title; }
    public List<String> getAuthors() { return authors; }
    public String getPublisher() { return publisher; }
    public int getPublicationYear() { return publicationYear; }
    public String getIsbn() { return isbn; }
    public Set<Category> getCategories() { return categories; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID ID;

        private String title;
        private List<String> authors;
        private String publisher;
        private int publicationYear;
        private String isbn;

        private Set<Category> categories;

        public Builder ID(UUID ID) {
            this.ID = ID;
            return this;
        }
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        public Builder addAuthor(String author) {
            this.authors.add(author);
            return this;
        }
        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }
        public Builder publicationYear(int publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }
        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }
        public Builder addCategory(Category category) {
            this.categories.add(category);
            return this;
        }

        public Book build() {
            return new Book(title, authors, publisher, publicationYear, isbn, categories);
        }
    }

        @Override
        public String toString() {
            return "Book Details:\n" +
                   "ID: " + ID + "\n" +
                   "Title: " + title + "\n" +
                   "Authors: " + authors + "\n" +
                   "Publisher: " + publisher + "\n" +
                   "Publication Year: " + publicationYear + "\n" +
                   "ISBN: " + isbn + "\n" +
                   "Categories: " + categories;
        }
}
