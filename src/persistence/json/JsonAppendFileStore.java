package persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import shared.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonAppendFileStore
{
    private final Path dir;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public JsonAppendFileStore(String directoryPath)
    {
        this.dir = Path.of(directoryPath);
    }

    public <T> void appendAll(String fileName, List<T> entities)
    {
        if (entities == null || entities.isEmpty()) return;

        Path file = dir.resolve(fileName);

        try
        {
            List<String> lines = new ArrayList<>(entities.size());
            for (T entity : entities)
            {
                lines.add(gson.toJson(entity));
            }

            Files.write(
                    file,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e)
        {
            Logger.getInstance().error("Failed to append all entities to '" + fileName + "': " + e.getMessage());
            throw new RuntimeException("Failed to append all entities to '" + fileName + "'", e);
        }
    }

    public <T> void append(String fileName, T entity)
    {
        appendAll(fileName, List.of(entity));
    }

    public <T> List<T> loadAll(String fileName, Class<T> c)
    {
        Path file = dir.resolve(fileName);

        if (!Files.exists(file)) return new ArrayList<>();

        try (var lines = Files.lines(file);)
        {
            List<T> result = new ArrayList<>();
            lines.filter(line -> !line.isBlank())
                    .forEach(line -> result.add(gson.fromJson(line, c)));
            return result;
        } catch (IOException e)
        {
            Logger.getInstance().error("Failed to load NDJSON from '" + fileName + "': " + e.getMessage());
            throw new RuntimeException("Failed to load NDJSON from '" + fileName + "'", e);
        }
    }
}