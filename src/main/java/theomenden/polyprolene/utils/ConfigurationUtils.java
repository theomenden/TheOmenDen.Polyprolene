package theomenden.polyprolene.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigurationUtils {
    private static final Path configurationDirectory = FabricLoader.getInstance().getConfigDir().resolve("polyprolene");
    private static final Path optionsPath = configurationDirectory.resolve("bindings-options.txt");
    private static final Path historyPath = configurationDirectory.resolve("history.txt");
    private static final Path favoritesPath = configurationDirectory.resolve("favorites.txt");

    public static boolean isDirectoryReadyToBeWritten() {
        if(configurationDirectory.toFile().isDirectory()) {
            return true;
        }
        return makeDirectory();
    }

    public static boolean isFileReady(Path fileToTest) {
        return fileToTest
                .toFile()
                .isFile() || createFile(fileToTest);
    }

    public static boolean makeDirectory() {
        return configurationDirectory.toFile()
                                     .mkdirs();
    }

    public static Path getHistoryPath() {
        return historyPath;
    }

    public static Path getFavoritesPath() {
        return favoritesPath;
    }

    public static boolean createFile(Path fileToTest) {
        try {
            return fileToTest.toFile().createNewFile();
        }
        catch(IOException e) {
            LoggerUtils.getLoggerInstance().warning(e.getMessage());
            return false;
        }
    }
}
