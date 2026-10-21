package vallegrande.edu.pe.misistema.controller;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import vallegrande.edu.pe.misistema.model.Usuario;
import vallegrande.edu.pe.misistema.model.UsuarioDAO;
import vallegrande.edu.pe.misistema.view.MainView;
import vallegrande.edu.pe.misistema.view.ProductoView;
import vallegrande.edu.pe.misistema.view.VentaView;

public class MainController {

    private final MainView view;
    private final UsuarioDAO usuarioDAO;

    private final ProductoView productoView;
    private final ProductoController productoController;

    private final VentaView ventaView;
    private final VentaController ventaController;

    private Usuario usuarioSeleccionado;

    public MainController(MainView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();

        this.productoView = new ProductoView();
        this.productoController = new ProductoController(productoView);

        this.ventaView = new VentaView();
        this.ventaController = new VentaController(ventaView);

        configurarEventos();
    }

    private void configurarEventos() {
        view.getBtnInicio().setOnAction(e -> view.mostrarInicio());

        view.getBtnUsuarios().setOnAction(e -> {
            view.mostrarUsuarios();
            cargarUsuarios();
        });

        view.getBtnProductos().setOnAction(e -> {
            view.mostrarProductos(productoView);
            productoController.cargarProductos();
        });

        view.getBtnVentas().setOnAction(e -> {
            view.mostrarVentas(ventaView);
            ventaController.cargarDatos();
        });

        view.getBtnRegistrar().setOnAction(e -> registrarConsulta());
        view.getBtnActualizar().setOnAction(e -> actualizarConsulta());
        view.getBtnEliminar().setOnAction(e -> eliminarConsulta());

        view.getTablaUsuarios()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    usuarioSeleccionado = seleccionado;

                    if (seleccionado != null) {
                        mostrarConsultaSeleccionada(seleccionado);
                    }
                });
    }

    private void cargarUsuarios() {
        List<Usuario> consultas = usuarioDAO.listar();
        view.mostrarDatosUsuarios(consultas);
    }

    private void registrarConsulta() {
        if (!formularioValido()) {
            return;
        }

        Usuario consulta = leerFormulario();

        if (usuarioDAO.insertar(consulta)) {
            cargarUsuarios();
            limpiarSeleccionYFormulario();
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Consulta registrada",
                    "La consulta se guardó correctamente."
            );
        } else {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar",
                    "Revisa los datos y la conexión con MySQL. " +
                            "La identificación fiscal debe ser única."
            );
        }
    }

    private void actualizarConsulta() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Consulta no seleccionada",
                    "Selecciona una consulta de la tabla para actualizarla."
            );
            return;
        }

        if (!formularioValido()) {
            return;
        }

        int id = usuarioSeleccionado.getId();
        Usuario consulta = leerFormulario();
        consulta.setId(id);

        if (usuarioDAO.actualizar(consulta)) {
            cargarUsuarios();
            seleccionarConsultaPorId(id);
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Consulta actualizada",
                    "Los cambios se guardaron correctamente."
            );
        } else {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo actualizar",
                    "Revisa los datos y la conexión con MySQL."
            );
        }
    }

    private void eliminarConsulta() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Consulta no seleccionada",
                    "Selecciona una consulta de la tabla para eliminarla."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar la consulta seleccionada?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            int id = usuarioSeleccionado.getId();

            if (usuarioDAO.eliminar(id)) {
                cargarUsuarios();
                limpiarSeleccionYFormulario();
                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Consulta eliminada",
                        "La consulta se eliminó correctamente."
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

    private Usuario leerFormulario() {
        Usuario consulta = new Usuario();

        consulta.setNombreOrganizacion(
                view.getNombreOrganizacion().trim()
        );
        consulta.setTipoDocumento(
                view.getTipoDocumento().trim()
        );
        consulta.setIdentificacionFiscal(
                view.getIdentificacionFiscal().trim()
        );
        consulta.setTelefono(view.getTelefono().trim());
        consulta.setDireccion(view.getDireccion().trim());
        consulta.setResponsable(view.getResponsable().trim());
        consulta.setCorreo(view.getCorreo().trim());

        String descripcion = view.getDescripcion().trim();
        consulta.setDescripcion(
                descripcion.isEmpty() ? null : descripcion
        );

        return consulta;
    }

    private boolean formularioValido() {
        if (view.getNombreOrganizacion().isBlank()
                || view.getTipoDocumento().isBlank()
                || view.getIdentificacionFiscal().isBlank()
                || view.getTelefono().isBlank()
                || view.getDireccion().isBlank()
                || view.getResponsable().isBlank()
                || view.getCorreo().isBlank()) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Faltan datos",
                    "Completa todos los campos obligatorios. " +
                            "La descripción es opcional."
            );
            return false;
        }

        return true;
    }

    private void mostrarConsultaSeleccionada(Usuario consulta) {
        view.setNombreOrganizacion(consulta.getNombreOrganizacion());
        view.setTipoDocumento(consulta.getTipoDocumento());
        view.setIdentificacionFiscal(consulta.getIdentificacionFiscal());
        view.setTelefono(consulta.getTelefono());
        view.setDireccion(consulta.getDireccion());
        view.setResponsable(consulta.getResponsable());
        view.setCorreo(consulta.getCorreo());
        view.setDescripcion(consulta.getDescripcion());
    }

    private void seleccionarConsultaPorId(int id) {
        for (Usuario consulta : view.getTablaUsuarios().getItems()) {
            if (consulta.getId() == id) {
                view.getTablaUsuarios()
                        .getSelectionModel()
                        .select(consulta);
                return;
            }
        }
    }

    private void limpiarSeleccionYFormulario() {
        usuarioSeleccionado = null;
        view.getTablaUsuarios().getSelectionModel().clearSelection();
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