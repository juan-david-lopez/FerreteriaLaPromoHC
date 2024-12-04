package LogicaTienda;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DataSerializer {

    private String filePath;

    public DataSerializer(String filePath) {
        this.filePath = filePath;
    }

    // Método para serializar los datos en un archivo .txt
    public void serializeData(List<Productos> listaProductos) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(listaProductos);
            System.out.println("Datos serializados exitosamente en " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" no se pudo serializar los datos correctamente");
        }
    }

    // Método para deserializar los datos desde un archivo .txt
    public List<Productos> deserializeData() {
        List<Productos> listaProductos = null;
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            listaProductos = (List<Productos>) in.readObject();
            System.out.println("Datos deserializados exitosamente desde " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("no se pudo serializar los datos correctamente");
        }
        return listaProductos;
    }
}
