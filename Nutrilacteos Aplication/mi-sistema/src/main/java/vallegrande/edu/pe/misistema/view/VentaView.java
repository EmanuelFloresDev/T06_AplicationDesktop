package vallegrande.edu.pe.misistema.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vallegrande.edu.pe.misistema.model.DetalleVenta;
import vallegrande.edu.pe.misistema.model.Producto;
import vallegrande.edu.pe.misistema.model.Venta;

public class VentaView extends ScrollPane {

    private final TextField txtNombreCliente = new TextField();
    private final TextField txtDocumentoCliente = new TextField();
    private final TextField txtCantidad = new TextField("1");

    private final ComboBox<String> cmbMetodoPago = new ComboBox<>();
    private final ComboBox<Producto> cmbProducto = new ComboBox<>();

    private final Button btnAgregarDetalle = new Button("Agregar producto");
    private final Button btnQuitarDetalle = new Button("Quitar producto");
    private final Button btnRegistrar = new Button("Registrar venta");
    private final Button btnActualizar = new Button("Actualizar venta");
    private final Button btnEliminar = new Button("Eliminar venta");
    private final Button btnNuevaVenta = new Button("Nueva venta");

    private final TableView<DetalleVenta> tablaDetalles = new TableView<>();
    private final TableView<Venta> tablaVentas = new TableView<>();
    private final Label lblTotal = new Label("Total: 0.00");

    public VentaView() {
        configurarCampos();
        crearTablas();
        crearContenido();
    }

    private void configurarCampos() {
        txtNombreCliente.setPromptText("Nombre del cliente (opcional)");
        txtDocumentoCliente.setPromptText("Documento del cliente (opcional)");
        txtCantidad.setPromptText("Cantidad");

        cmbMetodoPago.setPromptText("Método de pago");
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
                "Efectivo",
                "Tarjeta",
                "Transferencia"
        ));

        cmbProducto.setPromptText("Selecciona un producto");
        cmbProducto.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto producto) {
                if (producto == null) {
                    return "";
                }

                return producto.getCodigo()
                        + " - "
                        + producto.getNombre()
                        + " | S/ "
                        + producto.getPrecio()
                        + " | Stock: "
                        + producto.getStock();
            }

            @Override
            public Producto fromString(String texto) {
                return null;
            }
        });

        txtNombreCliente.setPrefWidth(220);
        txtDocumentoCliente.setPrefWidth(180);
        cmbMetodoPago.setPrefWidth(160);
        cmbProducto.setPrefWidth(350);
        txtCantidad.setPrefWidth(90);
    }

    private void crearTablas() {
        tablaDetalles.setPrefHeight(180);
        agregarColumnaDetalle("Producto", "nombreProducto", 240);
        agregarColumnaDetalle("Cantidad", "cantidad", 90);
        agregarColumnaDetalle("Precio unitario", "precioUnitario", 130);
        agregarColumnaDetalle("Subtotal", "subtotal", 120);

        tablaVentas.setPrefHeight(200);
        agregarColumnaVenta("ID", "id", 70);
        agregarColumnaVenta("Fecha", "fechaVenta", 180);
        agregarColumnaVenta("Cliente", "nombreCliente", 180);
        agregarColumnaVenta("Método de pago", "metodoPago", 150);
        agregarColumnaVenta("Total", "total", 120);
    }

    private void agregarColumnaDetalle(
            String titulo,
            String propiedad,
            double ancho
    ) {
        TableColumn<DetalleVenta, Object> columna =
                new TableColumn<>(titulo);
        columna.setCellValueFactory(
                new PropertyValueFactory<>(propiedad)
        );
        columna.setPrefWidth(ancho);
        tablaDetalles.getColumns().add(columna);
    }

    private void agregarColumnaVenta(
            String titulo,
            String propiedad,
            double ancho
    ) {
        TableColumn<Venta, Object> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(
                new PropertyValueFactory<>(propiedad)
        );
        columna.setPrefWidth(ancho);
        tablaVentas.getColumns().add(columna);
    }

    private void crearContenido() {
        Label titulo = new Label("GESTIÓN DE VENTAS");
        titulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #063B00;"
        );

        HBox datosCliente = new HBox(
                10,
                txtNombreCliente,
                txtDocumentoCliente,
                cmbMetodoPago
        );

        HBox agregarProducto = new HBox(
                10,
                cmbProducto,
                txtCantidad,
                btnAgregarDetalle,
                btnQuitarDetalle
        );

        HBox accionesVenta = new HBox(
                10,
                btnRegistrar,
                btnActualizar,
                btnEliminar,
                btnNuevaVenta
        );

        lblTotal.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        for (Button boton : new Button[]{
                btnAgregarDetalle,
                btnQuitarDetalle,
                btnRegistrar,
                btnActualizar,
                btnEliminar,
                btnNuevaVenta
        }) {
            boton.setStyle(
                    "-fx-background-color: #063B00;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
            );
        }

        VBox contenido = new VBox(
                12,
                titulo,
                new Label("Datos de la venta"),
                datosCliente,
                new Label("Agregar productos"),
                agregarProducto,
                new Label("Productos de esta venta"),
                tablaDetalles,
                lblTotal,
                accionesVenta,
                new Label("Historial de ventas"),
                tablaVentas
        );
        contenido.setPadding(new Insets(20));

        setContent(contenido);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    public void mostrarProductos(List<Producto> productos) {
        cmbProducto.setItems(
                FXCollections.observableArrayList(productos)
        );
    }

    public void mostrarVentas(List<Venta> ventas) {
        tablaVentas.setItems(
                FXCollections.observableArrayList(ventas)
        );
    }

    public void mostrarDetalles(List<DetalleVenta> detalles) {
        tablaDetalles.setItems(
                FXCollections.observableArrayList(detalles)
        );
        actualizarTotal(detalles);
    }

    public List<DetalleVenta> obtenerDetalles() {
        return new ArrayList<>(tablaDetalles.getItems());
    }

    public void agregarDetalle(DetalleVenta detalle) {
        tablaDetalles.getItems().add(detalle);
        actualizarTotal(tablaDetalles.getItems());
    }

    public void quitarDetalleSeleccionado() {
        DetalleVenta seleccionado = tablaDetalles
                .getSelectionModel()
                .getSelectedItem();

        if (seleccionado != null) {
            tablaDetalles.getItems().remove(seleccionado);
            actualizarTotal(tablaDetalles.getItems());
        }
    }

    private void actualizarTotal(List<DetalleVenta> detalles) {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : detalles) {
            if (detalle.getSubtotal() != null) {
                total = total.add(detalle.getSubtotal());
            }
        }

        lblTotal.setText("Total: S/ " + total.setScale(2));
    }

    public void limpiarFormulario() {
        txtNombreCliente.clear();
        txtDocumentoCliente.clear();
        txtCantidad.setText("1");
        cmbMetodoPago.setValue(null);
        cmbProducto.setValue(null);
        tablaDetalles.getItems().clear();
        lblTotal.setText("Total: S/ 0.00");
        tablaVentas.getSelectionModel().clearSelection();
    }

    public Button getBtnAgregarDetalle() {
        return btnAgregarDetalle;
    }

    public Button getBtnQuitarDetalle() {
        return btnQuitarDetalle;
    }

    public Button getBtnRegistrar() {
        return btnRegistrar;
    }

    public Button getBtnActualizar() {
        return btnActualizar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Button getBtnNuevaVenta() {
        return btnNuevaVenta;
    }

    public TableView<DetalleVenta> getTablaDetalles() {
        return tablaDetalles;
    }

    public TableView<Venta> getTablaVentas() {
        return tablaVentas;
    }

    public Producto getProductoSeleccionado() {
        return cmbProducto.getValue();
    }

    public Venta getVentaSeleccionada() {
        return tablaVentas.getSelectionModel().getSelectedItem();
    }

    public String getNombreCliente() {
        return txtNombreCliente.getText();
    }

    public String getDocumentoCliente() {
        return txtDocumentoCliente.getText();
    }

    public String getMetodoPago() {
        return cmbMetodoPago.getValue();
    }

    public String getCantidad() {
        return txtCantidad.getText();
    }

    public void setNombreCliente(String valor) {
        txtNombreCliente.setText(valor);
    }

    public void setDocumentoCliente(String valor) {
        txtDocumentoCliente.setText(valor);
    }

    public void setMetodoPago(String valor) {
        cmbMetodoPago.setValue(valor);
    }

    public void setCantidad(String valor) {
        txtCantidad.setText(valor);
    }
}