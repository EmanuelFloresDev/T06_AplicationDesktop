package vallegrande.edu.pe.misistema.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vallegrande.edu.pe.misistema.model.Usuario;

public class MainView extends BorderPane {

    private Button btnInicio;
    private Button btnUsuarios;
    private Button btnProductos;
    private Button btnVentas;

    private TextField txtNombreOrganizacion;
    private TextField txtTipoDocumento;
    private TextField txtIdentificacionFiscal;
    private TextField txtTelefono;
    private TextField txtDireccion;
    private TextField txtResponsable;
    private TextField txtCorreo;
    private TextField txtDescripcion;

    private Button btnRegistrar;
    private Button btnActualizar;
    private Button btnEliminar;

    private TableView<Usuario> tablaUsuarios;

    public MainView() {
        crearMenu();
        crearTabla();
        crearFormulario();
        mostrarInicio();
    }

    private void crearMenu() {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(25));
        menu.setPrefWidth(220);

        Label titulo = new Label("NUTRILÁCTEOS");
        titulo.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        btnInicio = crearBotonMenu("Inicio");
        btnUsuarios = crearBotonMenu("Consultas");
        btnProductos = crearBotonMenu("Productos");
        btnVentas = crearBotonMenu("Ventas");

        menu.getChildren().addAll(
                titulo,
                btnInicio,
                btnUsuarios,
                btnProductos,
                btnVentas
        );

        menu.setStyle("-fx-background-color: #063B00;");
        setLeft(menu);
    }

    private Button crearBotonMenu(String texto) {
        Button boton = new Button(texto);
        boton.setPrefWidth(170);
        boton.setPrefHeight(40);
        boton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #063B00;" +
                        "-fx-font-weight: bold;"
        );
        return boton;
    }

    public void mostrarInicio() {
        VBox contenido = new VBox(15);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        Label titulo = new Label("SISTEMA DE GESTIÓN");
        titulo.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #063B00;"
        );

        contenido.getChildren().addAll(
                titulo,
                new Label("Administración de Nutrilácteos"),
                new Label("Selecciona un módulo en el menú.")
        );

        setCenter(contenido);
    }

    public void mostrarUsuarios() {
        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(20));

        Label titulo = new Label("GESTIÓN DE CONSULTAS");
        titulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #063B00;"
        );

        Label subtitulo = new Label(
                "Completa el formulario para registrar una consulta. " +
                        "Selecciona una fila para editarla o eliminarla."
        );

        HBox botones = new HBox(
                10,
                btnRegistrar,
                btnActualizar,
                btnEliminar
        );
        botones.setAlignment(Pos.CENTER_LEFT);

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(8);

        formulario.add(new Label("Organización:"), 0, 0);
        formulario.add(txtNombreOrganizacion, 1, 0);
        formulario.add(new Label("Tipo de documento:"), 2, 0);
        formulario.add(txtTipoDocumento, 3, 0);

        formulario.add(new Label("Identificación fiscal:"), 0, 1);
        formulario.add(txtIdentificacionFiscal, 1, 1);
        formulario.add(new Label("Teléfono:"), 2, 1);
        formulario.add(txtTelefono, 3, 1);

        formulario.add(new Label("Dirección:"), 0, 2);
        formulario.add(txtDireccion, 1, 2);
        formulario.add(new Label("Responsable:"), 2, 2);
        formulario.add(txtResponsable, 3, 2);

        formulario.add(new Label("Correo electrónico:"), 0, 3);
        formulario.add(txtCorreo, 1, 3);
        formulario.add(new Label("Descripción:"), 2, 3);
        formulario.add(txtDescripcion, 3, 3);

        contenido.getChildren().addAll(
                titulo,
                subtitulo,
                formulario,
                botones,
                new Label("CONSULTAS REGISTRADAS"),
                tablaUsuarios
        );

        VBox.setVgrow(tablaUsuarios, javafx.scene.layout.Priority.ALWAYS);
        setCenter(contenido);
    }

    public void mostrarProductos(ProductoView productoView) {
        setCenter(productoView);
    }

    public void mostrarVentas(VentaView ventaView) {
        setCenter(ventaView);
    }

    private void crearFormulario() {
        txtNombreOrganizacion = crearCampo("Nombre de la organización");
        txtTipoDocumento = crearCampo("Ejemplo: RUC");
        txtIdentificacionFiscal = crearCampo("Identificación fiscal");
        txtTelefono = crearCampo("Teléfono");
        txtDireccion = crearCampo("Dirección de la empresa");
        txtResponsable = crearCampo("Nombre del responsable");
        txtCorreo = crearCampo("Correo electrónico");
        txtDescripcion = crearCampo("Descripción");

        btnRegistrar = crearBotonAccion("Registrar");
        btnActualizar = crearBotonAccion("Actualizar");
        btnEliminar = crearBotonAccion("Eliminar");
    }

    private TextField crearCampo(String texto) {
        TextField campo = new TextField();
        campo.setPromptText(texto);
        campo.setPrefWidth(190);
        return campo;
    }

    private Button crearBotonAccion(String texto) {
        Button boton = new Button(texto);
        boton.setPrefWidth(120);
        boton.setStyle(
                "-fx-background-color: #063B00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );
        return boton;
    }

    private void crearTabla() {
        tablaUsuarios = new TableView<>();
        tablaUsuarios.setPrefHeight(350);

        agregarColumna("ID", "id", 55);
        agregarColumna("Organización", "nombreOrganizacion", 180);
        agregarColumna("Tipo documento", "tipoDocumento", 120);
        agregarColumna("Identificación fiscal", "identificacionFiscal", 150);
        agregarColumna("Teléfono", "telefono", 110);
        agregarColumna("Dirección", "direccion", 180);
        agregarColumna("Responsable", "responsable", 160);
        agregarColumna("Correo", "correo", 190);
        agregarColumna("Descripción", "descripcion", 220);
        agregarColumna("Fecha de registro", "fechaRegistro", 160);
        agregarColumna("Estado", "estado", 110);
    }

    private void agregarColumna(
            String titulo,
            String propiedad,
            double ancho
    ) {
        TableColumn<Usuario, Object> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        columna.setPrefWidth(ancho);
        tablaUsuarios.getColumns().add(columna);
    }

    public void mostrarDatosUsuarios(List<Usuario> usuarios) {
        tablaUsuarios.setItems(
                FXCollections.observableArrayList(usuarios)
        );
    }

    public void limpiarFormulario() {
        txtNombreOrganizacion.clear();
        txtTipoDocumento.clear();
        txtIdentificacionFiscal.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtResponsable.clear();
        txtCorreo.clear();
        txtDescripcion.clear();
    }

    public Button getBtnInicio() {
        return btnInicio;
    }

    public Button getBtnUsuarios() {
        return btnUsuarios;
    }

    public Button getBtnProductos() {
        return btnProductos;
    }

    public Button getBtnVentas() {
        return btnVentas;
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

    public TableView<Usuario> getTablaUsuarios() {
        return tablaUsuarios;
    }

    public String getNombreOrganizacion() {
        return txtNombreOrganizacion.getText();
    }

    public String getTipoDocumento() {
        return txtTipoDocumento.getText();
    }

    public String getIdentificacionFiscal() {
        return txtIdentificacionFiscal.getText();
    }

    public String getTelefono() {
        return txtTelefono.getText();
    }

    public String getDireccion() {
        return txtDireccion.getText();
    }

    public String getResponsable() {
        return txtResponsable.getText();
    }

    public String getCorreo() {
        return txtCorreo.getText();
    }

    public String getDescripcion() {
        return txtDescripcion.getText();
    }

    public void setNombreOrganizacion(String valor) {
        txtNombreOrganizacion.setText(valor);
    }

    public void setTipoDocumento(String valor) {
        txtTipoDocumento.setText(valor);
    }

    public void setIdentificacionFiscal(String valor) {
        txtIdentificacionFiscal.setText(valor);
    }

    public void setTelefono(String valor) {
        txtTelefono.setText(valor);
    }

    public void setDireccion(String valor) {
        txtDireccion.setText(valor);
    }

    public void setResponsable(String valor) {
        txtResponsable.setText(valor);
    }

    public void setCorreo(String valor) {
        txtCorreo.setText(valor);
    }

    public void setDescripcion(String valor) {
        txtDescripcion.setText(valor);
    }
}