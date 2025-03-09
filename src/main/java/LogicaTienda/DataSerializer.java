package LogicaTienda;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataSerializer {
    private String filePath;
    private Gson gson = new Gson();

    public DataSerializer(String filePath) {
        this.filePath = filePath;
    }

    public void serializeData(ObservableList<Productos> productos) {
        System.out.println("🔎 Guardando productos: " + productos);

        try (FileWriter writer = new FileWriter(filePath, false)) { // false -> sobrescribe el archivo
            gson.toJson(new ArrayList<>(productos), writer); // Convertir a ArrayList antes de guardar
            System.out.println("✅ Productos guardados en JSON correctamente.");
        } catch (IOException e) {
            System.out.println("❌ Error al guardar productos: " + e.getMessage());
        }
    }


    public List<Productos> deserializeData() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("⚠️ Archivo JSON no encontrado. Creando una lista vacía.");
            return new ArrayList<>();  // Retorna una lista vacía
        }

        try (Reader reader = new FileReader(filePath)) {
            List<Productos> productos = gson.fromJson(reader, new TypeToken<List<Productos>>() {}.getType());
            return productos != null ? productos : new ArrayList<>(); // Evita devolver null
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error al leer el archivo JSON.");
            return new ArrayList<>();
        }
    }

}
