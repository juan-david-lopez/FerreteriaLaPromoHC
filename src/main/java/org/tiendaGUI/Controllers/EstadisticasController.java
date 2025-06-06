package org.tiendaGUI.Controllers;

import LogicaTienda.Data.DataModel;
import LogicaTienda.Logic.LogicaEstadistica;
import LogicaTienda.Model.Productos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
public class EstadisticasController {

    public PieChart pieChart;
    public BarChart<String, Number> barChart;
    public CategoryAxis xAxis;
    public NumberAxis yAxis;
    public Label totalVentasLabel;
    public Label totalDomiciliosLabel;
    public Label productoMasVendidoLabel;
    @FXML
    private Label promedioPreciosLabel;
    @FXML
    private Label sumaTotalPreciosLabel;
    @FXML
    private Label totalStockLabel;
    @FXML
    private Label precioMasAltoLabel;
    @FXML
    private Label precioMasBajoLabel;
    @FXML
    private Label productoMasCaroLabel;
    @FXML
    private Label productoConMasStockLabel;
    @FXML
    private Label valorTotalInventarioLabel;

    @FXML
    public void initialize() {
        System.out.println("sumaTotalPreciosLabel = " + sumaTotalPreciosLabel);
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        List<Productos> productos = DataModel.getInstance().getListaProductos();
        LogicaEstadistica estadistica = new LogicaEstadistica(productos);

        // Debug: Verificar si hay facturas cargadas
        System.out.println("DEBUG: Número de facturas cargadas: " + DataModel.getFacturas().size());
        if (!DataModel.getFacturas().isEmpty()) {
            System.out.println("DEBUG: Primera factura: " + DataModel.getFacturas().get(0));
            System.out.println("DEBUG: Total de la primera factura: " + DataModel.getFacturas().get(0).getTotal());
        }

        // Cargar datos para el gráfico de barras (ventas por día)
        cargarGraficoVentasPorDia(estadistica);

        double totalVentas = estadistica.calcularTotalVentas();
        System.out.println("DEBUG: Total de ventas calculado: " + totalVentas);

        if (totalVentasLabel != null) {
            totalVentasLabel.setText("Total ventas: " + totalVentas);
        }

        // Formateador de números con separadores de miles
        java.text.NumberFormat formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMAN);
        formatter.setMaximumFractionDigits(0);
        
        if (promedioPreciosLabel != null) {
            String promedioFormateado = formatter.format(estadistica.calcularPromedioPrecios());
            promedioPreciosLabel.setText("Promedio de precios: $" + promedioFormateado);
        }

        if (sumaTotalPreciosLabel != null) {
            String sumaFormateada = formatter.format(estadistica.calcularSumaTotalPrecios());
            sumaTotalPreciosLabel.setText("Suma total de precios: $" + sumaFormateada);
        }

        if (totalStockLabel != null) {
            // Mostrar tanto stock como cantidad para mayor claridad
            int totalStock = estadistica.calcularTotalProductosEnStock();
            String stockFormateado = formatter.format(totalStock);
            totalStockLabel.setText(String.format("Total productos en stock: %s (stock: %s + cantidad: %s)", 
                stockFormateado,
                formatter.format(estadistica.getListaProductos().stream().mapToInt(Productos::getStock).sum()),
                formatter.format(estadistica.getListaProductos().stream().mapToInt(Productos::getCantidad).sum())));
        }

        if (precioMasAltoLabel != null) {
            String precioAltoFormateado = formatter.format(estadistica.calcularPrecioMasAlto());
            precioMasAltoLabel.setText("Precio más alto: $" + precioAltoFormateado);
        }

        if (precioMasBajoLabel != null) {
            String precioBajoFormateado = formatter.format(estadistica.calcularPrecioMasBajo());
            precioMasBajoLabel.setText("Precio más bajo: $" + precioBajoFormateado);
        }

        if (productoMasCaroLabel != null && estadistica.obtenerProductoMasCaro() != null) {
            String precioFormateado = formatter.format(estadistica.obtenerProductoMasCaro().getPrecioParaVender());
            productoMasCaroLabel.setText(String.format("Producto más caro: %s ($%s)", 
                estadistica.obtenerProductoMasCaro().getNombre(), 
                formatter.format(estadistica.obtenerProductoMasCaro().getPrecioParaVender())));
        }

        if (productoConMasStockLabel != null && estadistica.obtenerProductoConMasStock() != null) {
            productoConMasStockLabel.setText(String.format("Producto con más stock: %s (%s unidades)", 
                estadistica.obtenerProductoConMasStock().getNombre(), 
                formatter.format(estadistica.obtenerProductoConMasStock().getStock())));
        }

        if (valorTotalInventarioLabel != null) {
            double valorTotal = estadistica.calcularValorTotalInventario();
            String valorFormateado = formatter.format(valorTotal);
            valorTotalInventarioLabel.setText("Valor total del inventario: $" + valorFormateado);
        }
    }

    @FXML
    public void onVolverButtonClick(ActionEvent event) {
        cambiarVentana(event, "hello-view.fxml", "Ferretería La Promo HC");
    }

    private void cambiarVentana(ActionEvent event, String fxmlFile, String titulo) {
        try {
            URL url = getClass().getResource("/org/tiendaGUI/" + fxmlFile);
            System.out.println("🔄 Cargando vista: " + fxmlFile + " → " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
            System.out.println("✅ Vista cargada: " + titulo);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Carga el gráfico de barras con las ventas por día
     * @param estadistica Instancia de LogicaEstadistica para obtener los datos
     */
    private void cargarGraficoVentasPorDia(LogicaEstadistica estadistica) {
        if (barChart == null) {
            System.out.println("ERROR: barChart es null");
            return;
        }

        // Limpiar datos anteriores
        barChart.getData().clear();

        // Obtener ventas por día
        Map<LocalDate, Double> ventasPorDia = estadistica.calcularVentasPorDia();

        if (ventasPorDia.isEmpty()) {
            System.out.println("No hay datos de ventas por día para mostrar");
            return;
        }

        // Crear serie de datos para el gráfico
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas por día");

        // Formatear fechas para mostrar en el eje X
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Agregar datos a la serie
        ventasPorDia.forEach((fecha, total) -> {
            String fechaFormateada = fecha.format(formatter);
            XYChart.Data<String, Number> data = new XYChart.Data<>(fechaFormateada, total);
            series.getData().add(data);
            System.out.println("Agregando al gráfico: " + fechaFormateada + " - " + total);
        });

        // Agregar la serie al gráfico
        barChart.getData().add(series);

        // Mostrar el valor de cada barra en la parte superior
        series.getData().forEach(data -> {
            Label label = new Label(String.format("%.0f", data.getYValue()));
            label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

            // Agregar el label al nodo de la barra cuando esté disponible
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    // Posicionar el label encima de la barra
                    label.setLayoutX(newNode.getLayoutX() - label.getWidth() / 2 + newNode.getBoundsInLocal().getWidth() / 2);
                    label.setLayoutY(newNode.getLayoutY() - label.getHeight());

                    // Agregar el label a la escena
                    ((Group) newNode.getParent()).getChildren().add(label);
                }
            });
        });

        // Configurar etiquetas para el eje X y Y
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Total Ventas");
    }
}
