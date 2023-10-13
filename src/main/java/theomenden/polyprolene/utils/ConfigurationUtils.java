package theomenden.polyprolene.utils;

import lombok.AccessLevel;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theomenden.polyprolene.client.PolyproleneClient;

import java.io.IOException;
import java.nio.file.Path;

@Getter
public class ConfigurationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolyproleneClient.MODID);
    private static final Path configurationDirectory = FabricLoader
            .getInstance()
            .getConfigDir()
            .resolve("polyprolene");
    private static final Path optionsPath = configurationDirectory.resolve("bindings-options.txt");
    @Getter(value = AccessLevel.PUBLIC)
    private static final Path historyPath = configurationDirectory.resolve("history.txt");
    @Getter
    private static final Path favoritesPath = configurationDirectory.resolve("favorites.txt");

    public static boolean isDirectoryReadyToBeWritten() {
        if (configurationDirectory
                .toFile()
                .isDirectory()) {
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
        return configurationDirectory
                .toFile()
                .mkdirs();
    }

    public static boolean createFile(Path fileToTest) {
        try {
            return fileToTest
                    .toFile()
                    .createNewFile();
        } catch (IOException e) {
            LOGGER.info("Could not create file {}", fileToTest, e);
            return false;
        }
    }
}
