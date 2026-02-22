package persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import shared.logging.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStore {
    private final Path dir;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonFileStore(String directoryPath) {
        this.dir = Path.of(directoryPath);
    }

    public <T> List<T> loadList(String fileName, Type listType) {
        try {
            Path file = dir.resolve(fileName);
            String json = Files.readString(file);

            List<T> data = gson.fromJson(json, listType);
            return data != null ? new ArrayList<>(data) : new ArrayList<>();
        } catch (IOException e) {
            Logger.getInstance().error("Failed to load list from '" + fileName + "': " + e.getMessage());
            throw new RuntimeException("Failed to load list from '" + fileName + "'", e);
        }
    }

    public <T> void saveList(String fileName, List<T> data) {
        try {
            Path file = dir.resolve(fileName);
            Files.writeString(file, gson.toJson(data));
        } catch (IOException e) {
            Logger.getInstance().error("Failed to save list to '" + fileName + "': " + e.getMessage());
            throw new RuntimeException("Failed to save list to '" + fileName + "'", e);
        }
    }

    public <T> void appendAll(String fileName, List<T> entities){
        if (entities == null || entities.isEmpty()) return;

        Path file = dir.resolve(fileName);

        try
        {
            List<String> lines = new ArrayList<>();

            for (T entity : entities){
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
            throw new RuntimeException("Failed to append all entities to '" + fileName + "': ", e);
        }
    }

    public <T> Type listTypeOf(Class<T> c) {
        return TypeToken.getParameterized(List.class, c).getType();
    }
}
