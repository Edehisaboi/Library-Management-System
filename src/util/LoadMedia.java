package util;

import domain.Category;
import domain.media.Book;
import domain.media.CD;
import domain.media.DVD;
import services.CatalogService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility for loading media items from CSV files.
 * Parses CSV lines and populates the CatalogService.
 */
public class LoadMedia {
    private final CatalogService catalogService;

    /**
     * Creates a new loader.
     * 
     * @param catalogService the service to load items into
     */
    public LoadMedia(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Loads books from a CSV file.
     *
     * @param filePath  path to the CSV file
     * @param hasHeader true if the file has a header row to skip
     */
    public void loadBooks(String filePath, boolean hasHeader) {
        List<List<String>> rows = readCsvFile(filePath, hasHeader);
        for (List<String> columns : rows) {
            try {
                if (columns.size() < 7) {
                    System.out.println("Skipping invalid Book row (cols=" + columns.size() + ")");
                    continue;
                }

                String title = columns.get(0).trim();
                String authorsStr = columns.get(1).trim();
                String publisher = columns.get(2).trim();
                int publicationYear = Integer.parseInt(columns.get(3).trim());
                String isbn = columns.get(4).trim();
                String categoriesStr = columns.get(5).trim();
                int copiesPerTitle = Integer.parseInt(columns.get(6).trim());

                Book.Builder book = Book.builder()
                        .title(title)
                        .publisher(publisher)
                        .year(publicationYear)
                        .isbn(isbn);

                List<String> authors = parseListString(authorsStr);
                for (String author : authors) {
                    book.addAuthor(author);
                }

                Set<Category> categories = parseCategories(categoriesStr);
                for (Category category : categories) {
                    book.addCategory(category);
                }

                catalogService.addTitle(book.build(), copiesPerTitle);
            } catch (Exception ex) {
                System.out.println("Skipping row due to error: " + ex.getMessage());
            }
        }
    }

    /**
     * Loads CDs from a CSV file.
     *
     * @param filePath  path to the CSV file
     * @param hasHeader true to skip the first row
     */
    public void loadCDs(String filePath, boolean hasHeader) {
        List<List<String>> rows = readCsvFile(filePath, hasHeader);
        for (List<String> columns : rows) {
            try {
                if (columns.size() < 7) {
                    System.out.println("Skipping invalid CD row (cols=" + columns.size() + ")");
                    continue;
                }

                String title = columns.get(0).trim();
                String artistsStr = columns.get(1).trim();
                int year = Integer.parseInt(columns.get(2).trim());
                int durationMinutes = Integer.parseInt(columns.get(3).trim());
                int trackCount = Integer.parseInt(columns.get(4).trim());
                String categoriesStr = columns.get(5).trim();
                int copiesPerTitle = Integer.parseInt(columns.get(6).trim());

                List<String> artists = parseListString(artistsStr);
                Set<Category> categorySet = parseCategories(categoriesStr);

                CD cd = new CD(title, artists, year, categorySet, durationMinutes, trackCount);
                catalogService.addTitle(cd, copiesPerTitle);
            } catch (Exception ex) {
                System.out.println("Skipping row due to error: " + ex.getMessage());
            }
        }
    }

    /**
     * Loads DVDs from a CSV file.
     *
     * @param filePath  path to the CSV file
     * @param hasHeader true to skip the first row
     */
    public void loadDVDs(String filePath, boolean hasHeader) {
        List<List<String>> rows = readCsvFile(filePath, hasHeader);
        for (List<String> columns : rows) {
            try {
                if (columns.size() < 8) {
                    System.out.println("Skipping invalid DVD row (cols=" + columns.size() + ")");
                    continue;
                }

                String title = columns.get(0).trim();
                String directorsStr = columns.get(1).trim();
                int year = Integer.parseInt(columns.get(2).trim());
                int durationMinutes = Integer.parseInt(columns.get(3).trim());
                String regionCode = columns.get(4).trim();
                String rating = columns.get(5).trim();
                String categoriesStr = columns.get(6).trim();
                int copiesPerTitle = Integer.parseInt(columns.get(7).trim());

                List<String> directors = parseListString(directorsStr);
                Set<Category> categorySet = parseCategories(categoriesStr);

                DVD dvd = new DVD(title, directors, year, categorySet, durationMinutes, regionCode, rating);
                catalogService.addTitle(dvd, copiesPerTitle);
            } catch (Exception ex) {
                System.out.println("Skipping row due to error: " + ex.getMessage());
            }
        }
    }

    /**
     * Parses a comma-separated string into a list of trimmed strings.
     * 
     * @param input the comma-separated string
     * @return list of non-blank values
     */
    private List<String> parseListString(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isBlank()) {
            return result;
        }
        for (String s : input.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isBlank()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Parses a comma-separated string of categories into a Set of Category enums.
     * 
     * @param input the comma-separated categories string
     * @return set of valid Category enums
     */
    private Set<Category> parseCategories(String input) {
        Set<Category> result = new HashSet<>();
        if (input == null || input.isBlank()) {
            return result;
        }
        for (String c : input.split(",")) {
            try {
                String categoryStr = c.trim().toUpperCase().replace(' ', '_');
                if (!categoryStr.isBlank()) {
                    result.add(Category.valueOf(categoryStr));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: Invalid category '" + c + "' ignored.");
            }
        }
        return result;
    }

    /**
     * Reads a CSV file and returns a list of parsed rows.
     * Each row is a list of column values.
     * 
     * @param filePath  path to the file
     * @param hasHeader whether to skip the first line
     * @return list of parsed rows
     */
    private List<List<String>> readCsvFile(String filePath, boolean hasHeader) {
        List<List<String>> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (hasHeader) {
                    hasHeader = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                try {
                    List<String> columns = parseCsvLine(line);
                    if (!columns.isEmpty()) {
                        rows.add(columns);
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file (" + filePath + "): " + e.getMessage());
        }
        return rows;
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
