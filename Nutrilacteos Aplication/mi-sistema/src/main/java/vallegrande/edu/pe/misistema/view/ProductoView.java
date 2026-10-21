package vallegrande.edu.pe.misistema.view;

import java.sql.Timestamp;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vallegrande.edu.pe.misistema.model.Producto;

public class ProductoView extends BorderPane {

    private final TextField txtCodigo = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtPrecio = new TextField();
    private final TextField txtStock = new TextField();
    private final TextField txtUnidadMedida = new TextField();
    private final TextArea txtDescripcion = new TextArea();
    private final CheckBox chkActivo = new CheckBox("Producto activo");

    private final Button btnRegistrar = new Button("Registrar");
    private final Button btnActualizar = new Button("Actualizar");
    private final Button btnEliminar = new Button("Eliminar");

    private final TableView<Producto> tablaProductos = new TableView<>();

    public ProductoView() {
        crearFormulario();
        crearTabla();
        crearContenido();
        chkActivo.setSelected(true);
    }

    private void crearFormulario() {
        txtCodigo.setPromptText("Ejemplo: LEC001");
        txtNombre.setPromptText("Nombre del producto");
        txtPrecio.setPromptText("Ejemplo: 12.50");
        txtStock.setPromptText("Cantidad disponible");
        txtUnidadMedida.setPromptText("Ejemplo: unidad, litro, kg");
        txtDescripcion.setPromptText("Descripción del producto");
        txtDescripcion.setPrefRowCount(2);
    }

    private void crearContenido() {
        Label titulo = new Label("GESTIÓN DE PRODUCTOS");
        titulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #063B00;"
        );

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);

        formulario.add(new Label("Código:"), 0, 0);
        formulario.add(txtCodigo, 1, 0);
        formulario.add(new Label("Nombre:"), 2, 0);
        formulario.add(txtNombre, 3, 0);

        formulario.add(new Label("Precio:"), 0, 1);
        formulario.add(txtPrecio, 1, 1);
        formulario.add(new Label("Stock:"), 2, 1);
        formulario.add(txtStock, 3, 1);

        formulario.add(new Label("Unidad de medida:"), 0, 2);
        formulario.add(txtUnidadMedida, 1, 2);
        formulario.add(chkActivo, 2, 2, 2, 1);

        formulario.add(new Label("Descripción:"), 0, 3);
        formulario.add(txtDescripcion, 1, 3, 3, 1);

        HBox botones = new HBox(
                10,
                btnRegistrar,
                btnActualizar,
                btnEliminar
        );

        for (Button boton : new Button[]{
                btnRegistrar, btnActualizar, btnEliminar
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
                formulario,
                botones,
                new Label("PRODUCTOS REGISTRADOS"),
                tablaProductos
        );
        contenido.setPadding(new Insets(20));

        VBox.setVgrow(tablaProductos, javafx.scene.layout.Priority.ALWAYS);
        setCenter(contenido);
    }

    private void crearTabla() {
        tablaProductos.setPrefHeight(400);

        agregarColumna("ID", "id", 60);
        agregarColumna("Código", "codigo", 100);
        agregarColumna("Nombre", "nombre", 160);
        agregarColumna("Descripción", "descripcion", 200);
        agregarColumna("Precio", "precio", 100);
        agregarColumna("Stock", "stock", 80);
        agregarColumna("Unidad", "unidadMedida", 110);
        agregarColumna("Activo", "activo", 80);
        agregarColumna("Fecha de creación", "fechaCreacion", 160);
    }

    private void agregarColumna(
            String titulo,
            String propiedad,
            double ancho
    ) {
        TableColumn<Producto, Object> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        columna.setPrefWidth(ancho);
        tablaProductos.getColumns().add(columna);
    }

    public void mostrarDatos(List<Producto> productos) {
        tablaProductos.setItems(
                FXCollections.observableArrayList(productos)
        );
    }

    public void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtUnidadMedida.clear();
        txtDescripcion.clear();
        chkActivo.setSelected(true);
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

    public TableView<Producto> getTablaProductos() {
        return tablaProductos;
    }

    public String getCodigo() {
        return txtCodigo.getText();
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public String getPrecio() {
        return txtPrecio.getText();
    }

    public String getStock() {
        return txtStock.getText();
    }

    public String getUnidadMedida() {
        return txtUnidadMedida.getText();
    }

    public String getDescripcion() {
        return txtDescripcion.getText();
    }

    public boolean isActivo() {
        return chkActivo.isSelected();
    }

    public void setCodigo(String valor) {
        txtCodigo.setText(valor);
    }

    public void setNombre(String valor) {
        txtNombre.setText(valor);
    }

    public void setPrecio(String valor) {
        txtPrecio.setText(valor);
    }

    public void setStock(String valor) {
        txtStock.setText(valor);
    }

    public void setUnidadMedida(String valor) {
        txtUnidadMedida.setText(valor);
    }

    public void setDescripcion(String valor) {
        txtDescripcion.setText(valor);
    }

    public void setActivo(boolean valor) {
        chkActivo.setSelected(valor);
    }
}