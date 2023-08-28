package theomenden.polyprolene.models.keyinfo;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import theomenden.polyprolene.utils.LoggerUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Getter
@ToString
@AllArgsConstructor
@Accessors(fluent = true)
public class KeyboardLayout {
    private static final String LAYOUT_PATH = "";
    protected static KeyboardLayout defaultLayout;
    protected static KeyboardLayout currentLayout;
    protected static Map<String, KeyboardLayout> additionalLayouts = Maps.newHashMap();
    @Setter
    protected KeyMetaData metaData;
    @Setter
    protected KeyBindings keyBindings;

    private static void tryLoadLayout(Gson gson, ClassLoader loader, Path p) {
        try (InputStreamReader reader =
                     new InputStreamReader(
                             Objects
                                     .requireNonNull(
                                             loader
                                                     .getResourceAsStream(
                                                             p.toString())), StandardCharsets.UTF_8)) {

        } catch (Exception e) {
            LoggerUtils
                    .getLoggerInstance()
                    .warning("Can't load " + p.getFileName() + " due to: " + e.getMessage());
            LoggerUtils
                    .getLoggerInstance()
                    .warning("Stack trace: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private static FileSystem getFileSystem(URI layoutUri) throws IOException {
        FileSystem fs;
        try {
            fs = FileSystems.getFileSystem(layoutUri);
            LoggerUtils
                    .getLoggerInstance()
                    .info("GET_FS");
        } catch (Exception e) {
            fs = FileSystems.newFileSystem(layoutUri, Collections.emptyMap());
            LoggerUtils
                    .getLoggerInstance()
                    .info("NEW_FS");
        }
        return fs;
    }

    public static KeyboardLayout getLayoutByCode(String code) {
        if (additionalLayouts.containsKey(code)) {
            return additionalLayouts.get(code);
        }
        LoggerUtils
                .getLoggerInstance()
                .warning("Could not load layout for [" + code + "] defaulting to en_us");
        return additionalLayouts.get("en_us");
    }

    public static void registerNewLayout(KeyboardLayout layout) {
        additionalLayouts.put(layout.metaData.languageCode, layout);
    }

    public static void loadKeyLayouts() {
        additionalLayouts.clear();

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson json = gsonBuilder.create();
        ClassLoader classLoader = KeyboardLayout.class.getClassLoader();
        @Nullable Stream<Path> files = null;
        FileSystem fs;

        try {
            LoggerUtils
                    .getLoggerInstance()
                    .warning(String.valueOf(classLoader.getResource("/")));
            LoggerUtils
                    .getLoggerInstance()
                    .warning(String.valueOf(classLoader.getResource(LAYOUT_PATH)));

            URI layoutUri = Objects.requireNonNull(
                    Objects
                            .requireNonNull(classLoader.getResource(LAYOUT_PATH))
                            .toURI()
            );

            LoggerUtils
                    .getLoggerInstance()
                    .warning("Layout stream source: " + layoutUri);
            LoggerUtils
                    .getLoggerInstance()
                    .warning("Layout stream scheme: " + layoutUri.getScheme());

            Path attemptedPath;

            if (layoutUri
                    .getScheme()
                    .equals("jar")) {
                fs = getFileSystem(layoutUri);
                attemptedPath = fs.getPath(LAYOUT_PATH);
            } else {
                attemptedPath = Path.of(layoutUri);
            }

            files = Files.walk(attemptedPath, 1);

            Iterator<Path> it = files.iterator();
            while (it.hasNext()) {
                Path p = it.next();
                if (!p
                        .getFileName()
                        .toString()
                        .endsWith(".json")) {
                    continue;
                }

                tryLoadLayout(json, classLoader, p);
            }

        } catch (Exception e) {
            LoggerUtils
                    .getLoggerInstance()
                    .severe(e.getMessage());
            LoggerUtils
                    .getLoggerInstance()
                    .info(Arrays.toString(e.getStackTrace()));
        } finally {
            if (files != null) {
                files.close();
            }
        }
    }

    protected static void updateKeyRows(List<KeyDataRow> rows) {
        rows
                .stream()
                .flatMap(k -> k.row.stream())
                .filter(k -> k.keyCode < 10)
                .forEach(key -> key.isMouseKey = true);
    }
}
