package LogicaTienda.Data;

import LogicaTienda.Model.Domicilio;
import LogicaTienda.Model.Productos;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class DataSerializer {

    private final String filePath;
    private final Gson gson;

    public DataSerializer(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            String dateStr = in.nextString();
            return (dateStr == null || dateStr.isEmpty()) ? null : LocalDate.parse(dateStr);
        }
    }

    public void serializeData(ObservableList<Productos> productos) {
        serializeGeneric(productos, filePath);
    }

    public void serializeDomicilios(ObservableList<Domicilio> domicilios) {
        serializeGeneric(domicilios, filePath);
    }

    public void serializeProductos(ObservableList<Productos> productos, String customFilePath) {
        serializeGeneric(productos, customFilePath);
    }

    private <T> void serializeGeneric(ObservableList<T> data, String path) {
        System.out.println("Guardando datos en " + path + ": " + data);

        try (Writer writer = new FileWriter(path)) {
            gson.toJson((data == null || data.isEmpty()) ? List.of() : data, writer);
            System.out.println("✅ Datos guardados correctamente en " + path);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar datos en JSON: " + e.getMessage());
        }
    }

    public ObservableList<Productos> deserializeData() {
        Type listType = new TypeToken<List<Productos>>() {}.getType();
        return deserializeGeneric(listType, filePath);
    }

    public ObservableList<Domicilio> deserializeDomicilios() {
        Type listType = new TypeToken<List<Domicilio>>() {}.getType();
        return deserializeGeneric(listType, filePath);
    }

    public ObservableList<Productos> deserializeProductos(String customFilePath) {
        Type listType = new TypeToken<List<Productos>>() {}.getType();
        return deserializeGeneric(listType, customFilePath);
    }

    private <T> ObservableList<T> deserializeGeneric(Type listType, String path) {
        File file = new File(path);

        if (!file.exists() || file.length() == 0) {
            System.out.println("⚠️ Archivo JSON vacío o no encontrado: " + path);
            return FXCollections.observableArrayList();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<T> data = gson.fromJson(reader, listType);

            if (data == null) {
                System.out.println("⚠️ El JSON no contiene datos válidos.");
                return FXCollections.observableArrayList();
            }

            System.out.println("📂 Datos leídos desde JSON: " + data.size() + " elementos");
            return FXCollections.observableArrayList(data);
        } catch (Exception e) {
            System.err.println("❌ Error al leer el JSON: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
}
