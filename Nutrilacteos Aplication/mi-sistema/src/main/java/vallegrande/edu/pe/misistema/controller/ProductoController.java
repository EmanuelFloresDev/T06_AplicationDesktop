package vallegrande.edu.pe.misistema.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import vallegrande.edu.pe.misistema.model.Producto;
import vallegrande.edu.pe.misistema.model.ProductoDAO;
import vallegrande.edu.pe.misistema.view.ProductoView;

public class ProductoController {

    private final ProductoView view;
    private final ProductoDAO productoDAO;
    private Producto productoSeleccionado;

    public ProductoController(ProductoView view) {
        this.view = view;
        this.productoDAO = new ProductoDAO();
        configurarEventos();
    }

    private void configurarEventos() {
        view.getBtnRegistrar().setOnAction(e -> registrarProducto());
        view.getBtnActualizar().setOnAction(e -> actualizarProducto());
        view.getBtnEliminar().setOnAction(e -> eliminarProducto());

        view.getTablaProductos()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    productoSeleccionado = seleccionado;

                    if (seleccionado != null) {
                        mostrarProductoSeleccionado(seleccionado);
                    }
                });
    }

    public void cargarProductos() {
        List<Producto> productos = productoDAO.listar();
        view.mostrarDatos(productos);
    }

    private void registrarProducto() {
        Producto producto = leerFormulario();

        if (producto == null) {
            return;
        }

        if (productoDAO.insertar(producto)) {
            cargarProductos();
            limpiarSeleccionYFormulario();
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto registrado",
                    "El producto se guardó correctamente."
            );
        } else {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar",
                    "Revisa los datos y que el código del producto no esté repetido."
            );
        }
    }

    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Selecciona un producto de la tabla para actualizarlo."
            );
            return;
        }

        Producto producto = leerFormulario();

        if (producto == null) {
            return;
        }

        long id = productoSeleccionado.getId();
        producto.setId(id);

        if (productoDAO.actualizar(producto)) {
            cargarProductos();
            seleccionarProductoPorId(id);
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto actualizado",
                    "Los cambios se guardaron correctamente."
            );
        } else {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo actualizar",
                    "Revisa los datos y que el código no esté usado por otro producto."
            );
        }
    }

    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Selecciona un producto de la tabla para eliminarlo."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar el producto seleccionado?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            long id = productoSeleccionado.getId();

            if (productoDAO.eliminar(id)) {
                cargarProductos();
                limpiarSeleccionYFormulario();
                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto se eliminó correctamente."
                );
            } else {
                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "No se pudo eliminar",
                        "Puede que el producto ya esté incluido en una venta."
                );
            }
        }
    }

    private Producto leerFormulario() {
        if (view.getCodigo().isBlank()
                || view.getNombre().isBlank()
                || view.getPrecio().isBlank()
                || view.getStock().isBlank()
                || view.getUnidadMedida().isBlank()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Faltan datos",
                    "Completa código, nombre, precio, stock y unidad de medida."
            );
            return null;
        }

        try {
            BigDecimal precio = new BigDecimal(view.getPrecio().trim());
            int stock = Integer.parseInt(view.getStock().trim());

            if (precio.compareTo(BigDecimal.ZERO) < 0 || stock < 0) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Dato no válido",
                        "El precio y el stock no pueden ser negativos."
                );
                return null;
            }

            Producto producto = new Producto();
            producto.setCodigo(view.getCodigo().trim());
            producto.setNombre(view.getNombre().trim());
            producto.setDescripcion(
                    view.getDescripcion().isBlank()
                            ? null
                            : view.getDescripcion().trim()
            );
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setUnidadMedida(view.getUnidadMedida().trim());
            producto.setActivo(view.isActivo());

            return producto;

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Número no válido",
                    "Escribe un precio válido (por ejemplo, 12.50) " +
                            "y un stock entero (por ejemplo, 20)."
            );
            return null;
        }
    }

    private void mostrarProductoSeleccionado(Producto producto) {
        view.setCodigo(producto.getCodigo());
        view.setNombre(producto.getNombre());
        view.setPrecio(producto.getPrecio().toPlainString());
        view.setStock(String.valueOf(producto.getStock()));
        view.setUnidadMedida(producto.getUnidadMedida());
        view.setDescripcion(producto.getDescripcion());
        view.setActivo(producto.isActivo());
    }

    private void seleccionarProductoPorId(long id) {
        for (Producto producto : view.getTablaProductos().getItems()) {
            if (producto.getId() == id) {
                view.getTablaProductos()
                        .getSelectionModel()
                        .select(producto);
                return;
            }
        }
    }

    private void limpiarSeleccionYFormulario() {
        productoSeleccionado = null;
        view.getTablaProductos().getSelectionModel().clearSelection();
        view.limpiarFormulario();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}