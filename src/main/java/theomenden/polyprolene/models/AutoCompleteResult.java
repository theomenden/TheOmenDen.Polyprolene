package theomenden.polyprolene.models;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import theomenden.polyprolene.interfaces.ISuggestionProvider;
import theomenden.polyprolene.utils.ConfigurationUtils;
import theomenden.polyprolene.utils.LoggerUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AutoCompleteResult {
    private List<KeyBindSuggestion> allKeyBindSuggestions;
    private List<KeyBindSuggestion> currentSuggestions;

    public static List<ISuggestionProvider> suggestionProviders = Lists.newArrayList();
    public static List<String> suggestionHistory = Lists.newArrayList();
    public static List<String> favorites = Lists.newArrayList();

    public AutoCompleteResult() {
        allKeyBindSuggestions = Lists.newArrayList();
        currentSuggestions = Lists.newArrayList();

        suggestionProviders
                .stream()
                .forEach(provider -> provider.addKeyBindingSuggestions(allKeyBindSuggestions));

        List<KeyBindSuggestion> tempFavorites = Lists.newLinkedList();

        Lists
                .reverse(suggestionHistory)
                .stream()
                .forEach(s -> {
                    var iterator = allKeyBindSuggestions.iterator();

                    generateTemporaryFavoritesFromIterator(s, iterator, tempFavorites);
                });

        allKeyBindSuggestions.addAll(0, tempFavorites);
    }

    public void updateSuggestionsList(String searchTerm) {
        currentSuggestions.clear();
        var terms = searchTerm
                .toLowerCase()
                .split("[\\s,]+");

        currentSuggestions = allKeyBindSuggestions
                .stream()
                .filter(suggestion -> suggestion.matches(terms))
                .collect(Collectors.toList());
    }

    public List<KeyBindSuggestion> getCurrentSuggestions() {
        return currentSuggestions;
    }

    public static void toggleFavorite(KeyBindSuggestion keyBindSuggestion) {
        keyBindSuggestion.isAFavorite ^= true;

        if (keyBindSuggestion.isAFavorite) {
            favorites.add(keyBindSuggestion.getId());
            suggestionHistory.add(keyBindSuggestion.getId());
            return;
        }

        favorites.remove(keyBindSuggestion.getId());
    }

    public static void addToSuggestionHistory(String string) {
        suggestionHistory.remove(string);
        suggestionHistory.add(0, string);
    }

    public static void loadDataFromFile() {
        suggestionHistory.clear();
        favorites.clear();

        CompletableFuture<Void> historyFuture = CompletableFuture
                .supplyAsync(() -> readLinesFromFile(ConfigurationUtils.getHistoryPath()))
                .thenAccept(lines -> suggestionHistory.addAll(lines));

        CompletableFuture<Void> favoritesFuture = CompletableFuture
                .supplyAsync(() -> readLinesFromFile(ConfigurationUtils.getFavoritesPath()))
                .thenAccept(lines -> favorites.addAll(lines));

        CompletableFuture
                .allOf(historyFuture, favoritesFuture)
                .join();
    }

    public static void saveDataToFiles() {
        ConfigurationUtils.isDirectoryReadyToBeWritten();

        CompletableFuture<Void> historyFuture = CompletableFuture
                .runAsync(() -> writeToExistingFile(ConfigurationUtils.getHistoryPath()))
                .exceptionally(e -> {
                    LoggerUtils
                            .getLoggerInstance()
                            .info(e.getMessage());
                    return null;
                })
                .thenRun(() -> LoggerUtils
                        .getLoggerInstance()
                        .info(" History written successfully."));

        CompletableFuture<Void> favoritesFuture = CompletableFuture
                .runAsync(() -> writeToExistingFile(ConfigurationUtils.getFavoritesPath()))
                .exceptionally(e -> {
                    LoggerUtils
                            .getLoggerInstance()
                            .info(e.getMessage());
                    return null;
                })
                .thenRun(() -> LoggerUtils
                        .getLoggerInstance()
                        .info("Current Favorites written successfully."));

        CompletableFuture
                .allOf(historyFuture, favoritesFuture)
                .join();
    }

    private static void writeToExistingFile(Path file) {
        ConfigurationUtils.isFileReady(file);

        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file.toFile(), true), StandardCharsets.UTF_8))) {
            suggestionHistory.forEach(printWriter::println);
        } catch (IOException e) {
            LoggerUtils
                    .getLoggerInstance()
                    .info(e.getMessage());
        }
    }

    private static List<String> readLinesFromFile(Path fileName) {
        List<String> lines = Collections.emptyList();
        try {
            if (ConfigurationUtils.isFileReady(fileName)) {
                lines = Files.readLines(fileName.toFile(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            LoggerUtils
                    .getLoggerInstance()
                    .info("Exception occurred while trying to read a Polyprolene '" + fileName + "' file: " + e.getMessage());
        }

        return lines;
    }

    private void generateTemporaryFavoritesFromIterator(String s, Iterator<KeyBindSuggestion> iterator, List<KeyBindSuggestion> tempFavorites) {
        while (iterator.hasNext()) {
            var bindingSuggestion = iterator.next();

            if (bindingSuggestion
                    .getId()
                    .equals(s)) {
                iterator.remove();

                if (favorites.contains(s)) {
                    bindingSuggestion.isAFavorite = true;
                    tempFavorites.add(bindingSuggestion);
                } else {
                    allKeyBindSuggestions.add(0, bindingSuggestion);
                }
                break;
            }
        }
    }

}
