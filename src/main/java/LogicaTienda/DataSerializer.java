package LogicaTienda;

import LogicaTienda.Model.Productos;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class DataSerializer {
    private final String filePath;
    private final Gson gson;

    public DataSerializer(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();  // ✅ Instancia única de Gson
    }

    public void serializeData(ObservableList<Productos> productos) {
        System.out.println("Guardando productos: " + productos); // Debugging
        if (productos == null || productos.isEmpty()) {
            System.out.println("⚠️ No hay productos que guardar. Se guardará un JSON vacío.");
            try (Writer writer = new FileWriter(filePath)) {
                writer.write("[]");  // 💾 Sobrescribe el archivo con una lista vacía
            } catch (IOException e) {
                System.err.println("❌ Error al limpiar el archivo JSON: " + e.getMessage());
            }
            return;
        }

        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(productos, writer);
            System.out.println("✅ Datos guardados correctamente en " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar productos en JSON: " + e.getMessage());
        }
    }



    public ObservableList<Productos> deserializeData() {
        File file = new File(filePath);

        // 📂 Si el archivo no existe o está vacío, devolvemos una lista vacía
        if (!file.exists() || file.length() == 0) {
            System.out.println("⚠️ Archivo JSON vacío o no encontrado. Se usará una lista vacía.");
            return FXCollections.observableArrayList();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Type listType = new TypeToken<List<Productos>>() {}.getType();
            List<Productos> productos = gson.fromJson(reader, listType);

            if (productos == null) {
                System.out.println("⚠️ El JSON no contiene productos válidos.");
                return FXCollections.observableArrayList();
            }

            System.out.println("📂 Productos leídos desde JSON: " + productos);
            return FXCollections.observableArrayList(productos);
        } catch (Exception e) {
            System.err.println("❌ Error al leer el JSON: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
}
