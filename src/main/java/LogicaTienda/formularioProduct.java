package LogicaTienda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class formularioProduct extends JFrame {

    private List<Productos> productos;

    public List<Productos> getProductos() {
        return productos;
    }

    void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    private JTextField idProductoField;
    private JTextField nombreField;
    private JTextField precioField;
    private JTextField cantidadField;
    private JTextField stockField;
    private JButton submitButton;

    public formularioProduct() {
        setTitle("Ingreso de Producto");
        setSize(400, 300);
        setDefaultCloseOperation(formularioProduct.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the productos list
        productos = new ArrayList<>();

        // Crear panel y establecer layout
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        // Crear etiquetas y campos de texto
        JLabel idProductoLabel = new JLabel("ID Producto:");
        idProductoField = new JTextField();

        JLabel nombreLabel = new JLabel("Nombre:");
        nombreField = new JTextField();

        JLabel precioLabel = new JLabel("Precio:");
        precioField = new JTextField();

        JLabel cantidadLabel = new JLabel("Cantidad:");
        cantidadField = new JTextField();

        JLabel stockLabel = new JLabel("Stock:");
        stockField = new JTextField();

        // Crear botón de envío
        submitButton = new JButton("Enviar");

        // Agregar componentes al panel
        panel.add(idProductoLabel);
        panel.add(idProductoField);
        panel.add(nombreLabel);
        panel.add(nombreField);
        panel.add(precioLabel);
        panel.add(precioField);
        panel.add(cantidadLabel);
        panel.add(cantidadField);
        panel.add(stockLabel);
        panel.add(stockField);
        panel.add(new JLabel()); // Espacio vacío
        panel.add(submitButton);

        // Agregar panel a la ventana
        add(panel);

        // Agregar acción al botón de envío
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String idProducto = idProductoField.getText();
                    String nombre = nombreField.getText();
                    double precio = Double.parseDouble(precioField.getText());
                    int cantidad = Integer.parseInt(cantidadField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    String centinelaproducto = idProducto + ";" + nombre + ";" + precio + ";" + cantidad + ";" + stock;
                    if (idProducto != null && nombre != null && precio > 0 && cantidad > 0 && stock > 0) {
                        productos.add(ConvertirAobjeto(centinelaproducto));
                    }
                    JOptionPane.showMessageDialog(null, "Datos ingresados:\n" +
                            "ID Producto: " + idProducto + "\n" +
                            "Nombre: " + nombre + "\n" +
                            "Precio: " + precio + "\n" +
                            "Cantidad: " + cantidad + "\n" +
                            "Stock: " + stock);

                }catch (Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

    }

    private Productos ConvertirAobjeto(String centinelaproducto) {
        System.out.println(centinelaproducto);
        Productos productosList = LogicaEstadistica.ConvertirAobjetoProducto(centinelaproducto);
        return productosList;
    }
}
