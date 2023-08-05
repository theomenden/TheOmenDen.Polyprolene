package theomenden.polyprolene.models;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import theomenden.polyprolene.interfaces.ISuggestionProvider;
import theomenden.polyprolene.utils.LoggerUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static theomenden.polyprolene.client.PolyproleneClient.MODID;

public class AutoCompleteResult {
    private List<KeyBindSuggestion> allKeyBindSuggestions = Lists.newArrayList();
    private List<KeyBindSuggestion> currentSuggestions = Lists.newArrayList();

    public static List<ISuggestionProvider> suggestionProviders = Collections.emptyList();
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static List<String> suggestionHistory = Collections.emptyList();
    public static List<String> favorites = Collections.emptyList();

    public void updateSuggestionsList(String searchTerm) {
        currentSuggestions.clear();
        var terms = searchTerm.toLowerCase().split("[\\s,]+");

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

        if(keyBindSuggestion.isAFavorite) {
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
                .supplyAsync(() -> readLinesFromFile("history"))
                .thenAccept(lines -> suggestionHistory.addAll(lines));

        CompletableFuture<Void> favoritesFuture = CompletableFuture
                .supplyAsync(() -> readLinesFromFile("favorites"))
                .thenAccept(lines -> favorites.addAll(lines));

        CompletableFuture.allOf(historyFuture, favoritesFuture).join();
    }

    public static void saveDataToFiles() {

        CompletableFuture<Void> historyFuture = CompletableFuture
                .runAsync(() -> writeToExistingFile("history"))
                .exceptionally(e -> {
                             // Handle exception if any System.err.println("Failed to write data: " + e.getMessage());
                             return null;
                         })
                .thenRun(() -> LoggerUtils.getLoggerInstance().info("History written successfully."));

        CompletableFuture<Void> favoritesFuture =CompletableFuture.runAsync(() -> writeToExistingFile("favorites"))
                         .exceptionally(e -> {
                             // Handle exception if any System.err.println("Failed to write data: " + e.getMessage());
                             return null;
                         })
                         .thenRun(() -> LoggerUtils.getLoggerInstance().info("Current Favorites written successfully."));

        CompletableFuture.allOf(historyFuture, favoritesFuture).join();
    }

    private static void writeToExistingFile(String file) {
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(getBindingsFile(file).toFile(), true), StandardCharsets.UTF_8))) {
            suggestionHistory.forEach(printWriter::println);
        } catch (IOException e) {
            //ignore
        }
    }
    private static List<String> readLinesFromFile(String fileName) {
        List<String> lines =  Collections.emptyList();

        try {
            Path file = getBindingsFile(fileName);
            lines = Files.readLines(file.toFile(), Charset.defaultCharset());
        } catch (IOException e) {
            LoggerUtils.getLoggerInstance()
                       .info("Exception occurred while trying to read a Polyprolene '"+ fileName +"' file: " + e.getMessage());
            }

            return lines;
        }


    private static Path getBindingsFile(String suffix) {
        return Paths.get(MinecraftClient.getInstance().runDirectory.getPath(),
                "polyprolene_", suffix, ".txt");
    }

}
