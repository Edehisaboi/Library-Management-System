package books;

import java.util.Objects;
import java.util.UUID;

public final class BookCopy {
    private final UUID copyId;
    private final UUID bookId;
    private Status status;

    public BookCopy(UUID bookId) {
        this.copyId = UUID.randomUUID();
        this.bookId = Objects.requireNonNull(bookId, "Book ID cannot be null!");
        this.status = Status.AVAILABLE;
    }

    public UUID getCopyId() {
        return copyId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null!");
    }

    @Override
    public String toString() {
        return "\n──────── Book Copy ────────\n" +
               " Copy ID: " + copyId + "\n" +
               " Book ID: " + bookId + "\n" +
               " Status: " + status + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookCopy other)) return false;
        return copyId.equals(other.copyId);
    }

    @Override
    public int hashCode() {
        return copyId.hashCode();
    }
}
