package vallegrande.edu.pe.misistema.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import vallegrande.edu.pe.misistema.model.DetalleVenta;
import vallegrande.edu.pe.misistema.model.Producto;
import vallegrande.edu.pe.misistema.model.ProductoDAO;
import vallegrande.edu.pe.misistema.model.Venta;
import vallegrande.edu.pe.misistema.model.VentaDAO;
import vallegrande.edu.pe.misistema.view.VentaView;

public class VentaController {

    private final VentaView view;
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;

    public VentaController(VentaView view) {
        this.view = view;
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        configurarEventos();
        cargarDatos();
    }

    private void configurarEventos() {
        view.getBtnAgregarDetalle().setOnAction(e -> agregarProducto());
        view.getBtnQuitarDetalle().setOnAction(
                e -> view.quitarDetalleSeleccionado()
        );
        view.getBtnRegistrar().setOnAction(e -> registrarVenta());
        view.getBtnActualizar().setOnAction(e -> actualizarVenta());
        view.getBtnEliminar().setOnAction(e -> eliminarVenta());
        view.getBtnNuevaVenta().setOnAction(e -> view.limpiarFormulario());

        view.getTablaVentas()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionada) -> {
                    if (seleccionada != null) {
                        cargarVentaSeleccionada(seleccionada);
                    }
                });
    }

    public void cargarDatos() {
        List<Producto> productosActivos = productoDAO.listar()
                .stream()
                .filter(Producto::isActivo)
                .toList();

        view.mostrarProductos(productosActivos);
        view.mostrarVentas(ventaDAO.listar());
    }

    private void agregarProducto() {
        Producto producto = view.getProductoSeleccionado();

        if (producto == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Selecciona un producto para agregarlo a la venta."
            );
            return;
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(view.getCantidad().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Cantidad no válida",
                    "Escribe una cantidad entera mayor que cero."
            );
            return;
        }

        if (cantidad <= 0) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Cantidad no válida",
                    "La cantidad debe ser mayor que cero."
            );
            return;
        }

        List<DetalleVenta> detalles = view.obtenerDetalles();
        DetalleVenta detalleExistente = null;

        for (DetalleVenta detalle : detalles) {
            if (detalle.getProductoId() == producto.getId()) {
                detalleExistente = detalle;
                break;
            }
        }

        int cantidadEnCarrito = detalleExistente == null
                ? 0
                : detalleExistente.getCantidad();

        if (cantidadEnCarrito + cantidad > producto.getStock()
                + cantidadEnCarritoEnVentaExistente(producto.getId())) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Stock insuficiente",
                    "La cantidad supera el stock disponible."
            );
            return;
        }

        if (detalleExistente == null) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(producto.getId());
            detalle.setNombreProducto(producto.getNombre());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(
                    producto.getPrecio()
                            .multiply(BigDecimal.valueOf(cantidad))
            );
            view.agregarDetalle(detalle);
        } else {
            int nuevaCantidad = detalleExistente.getCantidad() + cantidad;
            detalleExistente.setCantidad(nuevaCantidad);
            detalleExistente.setPrecioUnitario(producto.getPrecio());
            detalleExistente.setSubtotal(
                    producto.getPrecio()
                            .multiply(BigDecimal.valueOf(nuevaCantidad))
            );
            view.mostrarDetalles(detalles);
        }
    }

    private int cantidadEnCarritoEnVentaExistente(long productoId) {
        Venta seleccionada = view.getVentaSeleccionada();

        if (seleccionada == null) {
            return 0;
        }

        Venta completa = ventaDAO.buscarPorId(seleccionada.getId());

        if (completa == null) {
            return 0;
        }

        for (DetalleVenta detalle : completa.getDetalles()) {
            if (detalle.getProductoId() == productoId) {
                return detalle.getCantidad();
            }
        }

        return 0;
    }

    private void registrarVenta() {
        if (!validarVenta()) {
            return;
        }

        Venta venta = leerFormulario();

        if (ventaDAO.insertar(venta)) {
            view.limpiarFormulario();
            cargarDatos();
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Venta registrada",
                    "La venta se guardó y el stock fue actualizado."
            );
        } else {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar",
                    "Revisa el stock disponible y los datos de la venta."
            );
        }
    }

    private void actualizarVenta() {
        Venta seleccionada = view.getVentaSeleccionada();

        if (seleccionada == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Venta no seleccionada",
                    "Selecciona una venta del historial para actualizarla."
            );
            return;
        }

        if (!validarVenta()) {
            return;
        }

        Venta venta = leerFormulario();
        venta.setId(seleccionada.getId());

        if (ventaDAO.actualizar(venta)) {
            view.limpiarFormulario();
            cargarDatos();
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Venta actualizada",
                    "La venta se actualizó y el stock fue ajustado."
            );
        } else {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo actualizar",
                    "Revisa el stock disponible y los datos de la venta."
            );
        }
    }

    private void eliminarVenta() {
        Venta seleccionada = view.getVentaSeleccionada();

        if (seleccionada == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Venta no seleccionada",
                    "Selecciona una venta del historial para eliminarla."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar esta venta? El stock de sus productos se devolverá.",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            if (ventaDAO.eliminar(seleccionada.getId())) {
                view.limpiarFormulario();
                cargarDatos();
                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Venta eliminada",
                        "La venta se eliminó y el stock fue devuelto."
                );
            } else {
                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "No se pudo eliminar",
                        "Revisa la conexión con MySQL."
                );
            }
        }
    }

    private boolean validarVenta() {
        if (view.getMetodoPago() == null
                || view.getMetodoPago().isBlank()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Falta el método de pago",
                    "Selecciona un método de pago."
            );
            return false;
        }

        if (view.obtenerDetalles().isEmpty()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Venta sin productos",
                    "Agrega por lo menos un producto a la venta."
            );
            return false;
        }

        return true;
    }

    private Venta leerFormulario() {
        Venta venta = new Venta();

        String nombre = view.getNombreCliente().trim();
        String documento = view.getDocumentoCliente().trim();

        venta.setNombreCliente(nombre.isEmpty() ? null : nombre);
        venta.setDocumentoCliente(documento.isEmpty() ? null : documento);
        venta.setMetodoPago(view.getMetodoPago());
        venta.setDetalles(new ArrayList<>(view.obtenerDetalles()));

        return venta;
    }

    private void cargarVentaSeleccionada(Venta resumen) {
        Venta venta = ventaDAO.buscarPorId(resumen.getId());

        if (venta == null) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo cargar la venta",
                    "No se encontró el detalle de la venta seleccionada."
            );
            return;
        }

        view.setNombreCliente(
                venta.getNombreCliente() == null
                        ? ""
                        : venta.getNombreCliente()
        );
        view.setDocumentoCliente(
                venta.getDocumentoCliente() == null
                        ? ""
                        : venta.getDocumentoCliente()
        );
        view.setMetodoPago(venta.getMetodoPago());
        view.mostrarDetalles(venta.getDetalles());
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