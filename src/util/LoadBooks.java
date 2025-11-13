package util;

import domain.media.Book;
import services.CatalogService;
import domain.Category;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadBooks {
    private final String filePath;
    private boolean hasHeader;

    public LoadBooks(String filePath, boolean hasHeader) {
        this.filePath = filePath;
        this.hasHeader = hasHeader;
    }

    public void load(CatalogService catalogService, int copiesPerTitle) {
        String row;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((row = br.readLine()) != null) {
                if (hasHeader) {
                    hasHeader = false;
                    continue;
                }
                if (row.isBlank())
                    continue;

                try {
                    List<String> columns = parseCsvLine(row);
                    if (columns.size() < 6) {
                        System.out.println("Skipping invalid row: " + row);
                        continue;
                    }

                    String title = columns.get(0).trim();
                    String authorsStr = columns.get(1).trim();
                    String publisher = columns.get(2).trim();
                    int publicationYear = Integer.parseInt(columns.get(3).trim());
                    String isbn = columns.get(4).trim();
                    String categoriesStr = columns.get(5).trim();

                    Book.Builder book = Book.builder()
                            .title(title)
                            .publisher(publisher)
                            .year(publicationYear)
                            .isbn(isbn);

                    for (String a : authorsStr.split(",")) {
                        String author = a.trim();
                        if (!author.isBlank())
                            book.addAuthor(author);
                    }

                    for (String c : categoriesStr.split(",")) {
                        String category = c.trim().toUpperCase().replace(' ', '_');
                        if (!category.isBlank())
                            book.addCategory(Category.valueOf(category));
                    }

                    Book bookObj = book.build();
                    catalogService.addTitle(bookObj, copiesPerTitle);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Skipping row due to data error: " + ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Skipping row due to unexpected error: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static List<String> parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder curr = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '\"') {
                if (inQuote && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    curr.append(ch);
                    i++;
                } else {
                    inQuote = !inQuote;
                }
            } else if (ch == ',' && !inQuote) {
                out.add(curr.toString());
                curr.setLength(0);
            } else {
                curr.append(ch);
            }
        }
        out.add(curr.toString());
        return out;
    }
}
