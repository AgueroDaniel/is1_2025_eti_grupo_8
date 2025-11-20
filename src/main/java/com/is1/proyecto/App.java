package com.is1.proyecto; // Define el paquete de la aplicación, debe coincidir con la estructura de carpetas.

// Importaciones necesarias para la aplicación Spark
import com.fasterxml.jackson.databind.ObjectMapper; // Utilidad para serializar/deserializar objetos Java a/desde JSON.
import static spark.Spark.*; // Importa los métodos estáticos principales de Spark (get, post, before, after, etc.).

// Importaciones específicas para ActiveJDBC (ORM para la base de datos)
import org.javalite.activejdbc.Base; // Clase central de ActiveJDBC para gestionar la conexión a la base de datos.
import org.javalite.activejdbc.Model;
import org.mindrot.jbcrypt.BCrypt; // Utilidad para hashear y verificar contraseñas de forma segura.

// Importaciones de Spark para renderizado de plantillas
import spark.ModelAndView; // Representa un modelo de datos y el nombre de la vista a renderizar.
import spark.template.mustache.MustacheTemplateEngine; // Motor de plantillas Mustache para Spark.

// Importaciones estándar de Java
import java.util.HashMap; // Para crear mapas de datos (modelos para las plantillas).
import java.util.Map; // Interfaz Map, utilizada para Map.of() o HashMap.

// Importaciones de clases del proyecto
import com.is1.proyecto.config.DBConfigSingleton; // Clase Singleton para la configuración de la base de datos.
import com.is1.proyecto.models.User; // Modelo de ActiveJDBC que representa la tabla 'users'.
import com.is1.proyecto.models.Docente;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.Materia;

/**
 * Clase principal de la aplicación Spark.
 * Configura las rutas, filtros y el inicio del servidor web.
 */
public class App {

    // Instancia estática y final de ObjectMapper para la serialización/deserialización JSON.
    // Se inicializa una sola vez para ser reutilizada en toda la aplicación.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Método principal que se ejecuta al iniciar la aplicación.
     * Aquí se configuran todas las rutas y filtros de Spark.
     */
    public static void main(String[] args) {
        port(8080); // Configura el puerto en el que la aplicación Spark escuchará las peticiones (por defecto es 4567).

        // Obtener la instancia única del singleton de configuración de la base de datos.
        DBConfigSingleton dbConfig = DBConfigSingleton.getInstance();

        // --- Filtro 'before' para gestionar la conexión a la base de datos ---
        // Este filtro se ejecuta antes de cada solicitud HTTP.
        before((req, res) -> {
            try {
                // Abre una conexión a la base de datos utilizando las credenciales del singleton.
                if(!Base.hasConnection()){
                Base.open(dbConfig.getDriver(), dbConfig.getDbUrl(), dbConfig.getUser(), dbConfig.getPass());
                System.out.println(req.url());
                }
            }catch (Exception e) {
                // Si ocurre un error al abrir la conexión, se registra y se detiene la solicitud
                // con un código de estado 500 (Internal Server Error) y un mensaje JSON.
                System.err.println("Error al abrir conexión con ActiveJDBC: " + e.getMessage());
                halt(500, "{\"error\": \"Error interno del servidor: Fallo al conectar a la base de datos.\"}" + e.getMessage());
            }
        });

        // --- Filtro 'after' para cerrar la conexión a la base de datos ---
        // Este filtro se ejecuta después de que cada solicitud HTTP ha sido procesada.
        after((req, res) -> {
            try {
                // Cierra la conexión a la base de datos para liberar recursos.
                if(Base.hasConnection()){
                     Base.close();
                }
            } catch (Exception e) {
                // Si ocurre un error al cerrar la conexión, se registra.
                System.err.println("Error al cerrar conexión con ActiveJDBC: " + e.getMessage());
            }
        });

        // --- Rutas GET para renderizar formularios y páginas HTML ---

        // GET: Muestra el formulario de creación de cuenta.
        // Soporta la visualización de mensajes de éxito o error pasados como query parameters.
        get("/user/create", (req, res) -> {
            Map<String, Object> model = new HashMap<>(); // Crea un mapa para pasar datos a la plantilla.

            // Obtener y añadir mensaje de éxito de los query parameters (ej. ?message=Cuenta creada!)
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }

            // Obtener y añadir mensaje de error de los query parameters (ej. ?error=Campos vacíos)
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }

            // Renderiza la plantilla 'user_form.mustache' con los datos del modelo.
            return new ModelAndView(model, "user_form.mustache");
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.

        // GET: Ruta para mostrar el dashboard (panel de control) del usuario.
        // Requiere que el usuario esté autenticado.
        get("/dashboard", (req, res) -> {
            Map<String, Object> model = new HashMap<>(); // Modelo para la plantilla del dashboard.

            // Intenta obtener el nombre de usuario y la bandera de login de la sesión.
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");

            // 1. Verificar si el usuario ha iniciado sesión.
            // Si no hay un nombre de usuario en la sesión, la bandera es nula o falsa,
            // significa que el usuario no está logueado o su sesión expiró.
            if (currentUsername == null || loggedIn == null || !loggedIn) {
                System.out.println("DEBUG: Acceso no autorizado a /dashboard. Redirigiendo a /login.");
                // Redirige al login con un mensaje de error.
                res.redirect("/login?error=Debes iniciar sesión para acceder a esta página.");
                return null; // Importante retornar null después de una redirección.
            }

            // 2. Si el usuario está logueado, añade el nombre de usuario al modelo para la plantilla.
            model.put("username", currentUsername);

            // 3. Renderiza la plantilla del dashboard con el nombre de usuario.
            return new ModelAndView(model, "dashboard.mustache");
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.

        // GET: Ruta para cerrar la sesión del usuario.
        get("/logout", (req, res) -> {
            // Invalida completamente la sesión del usuario.
            // Esto elimina todos los atributos guardados en la sesión y la marca como inválida.
            // La cookie JSESSIONID en el navegador también será gestionada para invalidarse.
            req.session().invalidate();

            System.out.println("DEBUG: Sesión cerrada. Redirigiendo a /login.");

            // Redirige al usuario a la página de login con un mensaje de éxito.
            res.redirect("/");

            return null; // Importante retornar null después de una redirección.
        });

        // GET: Muestra el formulario de inicio de sesión (login).
        // Nota: Esta ruta debería ser capaz de leer también mensajes de error/éxito de los query params
        // si se la usa como destino de redirecciones. (Tu código de /user/create ya lo hace, aplicar similar).
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }
            return new ModelAndView(model, "login.mustache");
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.

        // GET: Ruta de alias para el formulario de creación de cuenta.
        // En una aplicación real, probablemente querrías unificar con '/user/create' para evitar duplicidad.
        get("/user/new", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "user_form.mustache"); // No pasa un modelo específico, solo el formulario.
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.


        // --- Rutas POST para manejar envíos de formularios y APIs ---

        // POST: Maneja el envío del formulario de creación de nueva cuenta.
        post("/user/new", (req, res) -> {
            String name = req.queryParams("name");
            String password = req.queryParams("password");

            // Validaciones básicas: campos no pueden ser nulos o vacíos.
            if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
                res.status(400); // Código de estado HTTP 400 (Bad Request).
                // Redirige al formulario de creación con un mensaje de error.
                res.redirect("/user/create?error=Nombre y contraseña son requeridos.");
                return ""; // Retorna una cadena vacía ya que la respuesta ya fue redirigida.
            }

            try {
                // Intenta crear y guardar la nueva cuenta en la base de datos.
                User ac = new User(); // Crea una nueva instancia del modelo User.
                // Hashea la contraseña de forma segura antes de guardarla.
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                ac.set("name", name); // Asigna el nombre de usuario.
                ac.set("password", hashedPassword); // Asigna la contraseña hasheada.
                ac.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                res.status(201); // Código de estado HTTP 201 (Created) para una creación exitosa.
                // Redirige al formulario de creación con un mensaje de éxito.
                res.redirect("/user/create?message=Cuenta creada exitosamente para " + name + "!");
                return ""; // Retorna una cadena vacía.

            } catch (Exception e) {
                // Si ocurre cualquier error durante la operación de DB (ej. nombre de usuario duplicado),
                // se captura aquí y se redirige con un mensaje de error.
                System.err.println("Error al registrar la cuenta: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Código de estado HTTP 500 (Internal Server Error).
                res.redirect("/user/create?error=Error interno al crear la cuenta. Intente de nuevo.");
                return ""; // Retorna una cadena vacía.
            }
        });


        // POST: Maneja el envío del formulario de inicio de sesión.
        post("/login", (req, res) -> {
            Map<String, Object> model = new HashMap<>(); // Modelo para la plantilla de login o dashboard.

            String username = req.queryParams("username");
            String plainTextPassword = req.queryParams("password");

            // Validaciones básicas: campos de usuario y contraseña no pueden ser nulos o vacíos.
            if (username == null || username.isEmpty() || plainTextPassword == null || plainTextPassword.isEmpty()) {
                res.status(400); // Bad Request.
                model.put("errorMessage", "El nombre de usuario y la contraseña son requeridos.");
                return new ModelAndView(model, "login.mustache"); // Renderiza la plantilla de login con error.
            }

            // Busca la cuenta en la base de datos por el nombre de usuario.
            User ac = User.findFirst("name = ?", username);

            // Si no se encuentra ninguna cuenta con ese nombre de usuario.
            if (ac == null) {
                res.status(401); // Unauthorized.
                model.put("errorMessage", "Usuario o contraseña incorrectos."); // Mensaje genérico por seguridad.
                return new ModelAndView(model, "login.mustache"); // Renderiza la plantilla de login con error.
            }

            // Obtiene la contraseña hasheada almacenada en la base de datos.
            String storedHashedPassword = ac.getString("password");

            // Compara la contraseña en texto plano ingresada con la contraseña hasheada almacenada.
            // BCrypt.checkpw hashea la plainTextPassword con el salt de storedHashedPassword y compara.
            if (BCrypt.checkpw(plainTextPassword, storedHashedPassword)) {
                // Autenticación exitosa.
                res.status(200); // OK.

                // --- Gestión de Sesión ---
                req.session(true).attribute("currentUserUsername", username); // Guarda el nombre de usuario en la sesión.
                req.session().attribute("userId", ac.getId()); // Guarda el ID de la cuenta en la sesión (útil).
                req.session().attribute("loggedIn", true); // Establece una bandera para indicar que el usuario está logueado.

                System.out.println("DEBUG: Login exitoso para la cuenta: " + username);
                System.out.println("DEBUG: ID de Sesión: " + req.session().id());


                model.put("username", username); // Añade el nombre de usuario al modelo para el dashboard.
                // Renderiza la plantilla del dashboard tras un login exitoso.
                return new ModelAndView(model, "dashboard.mustache");
            } else {
                // Contraseña incorrecta.
                res.status(401); // Unauthorized.
                System.out.println("DEBUG: Intento de login fallido para: " + username);
                model.put("errorMessage", "Usuario o contraseña incorrectos."); // Mensaje genérico por seguridad.
                return new ModelAndView(model, "login.mustache"); // Renderiza la plantilla de login con error.
            }
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta POST.


        // POST: Endpoint para añadir usuarios (API que devuelve JSON, no HTML).
        // Advertencia: Esta ruta tiene un propósito diferente a las de formulario HTML.
        post("/add_users", (req, res) -> {
            res.type("application/json"); // Establece el tipo de contenido de la respuesta a JSON.

            // Obtiene los parámetros 'name' y 'password' de la solicitud.
            String name = req.queryParams("name");
            String password = req.queryParams("password");

            // --- Validaciones básicas ---
            if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
                res.status(400); // Bad Request.
                return objectMapper.writeValueAsString(Map.of("error", "Nombre y contraseña son requeridos."));
            }

            try {
                // --- Creación y guardado del usuario usando el modelo ActiveJDBC ---
                User newUser = new User(); // Crea una nueva instancia de tu modelo User.
                // ¡ADVERTENCIA DE SEGURIDAD CRÍTICA!
                // En una aplicación real, las contraseñas DEBEN ser hasheadas (ej. con BCrypt)
                // ANTES de guardarse en la base de datos, NUNCA en texto plano.
                // (Nota: El código original tenía la contraseña en texto plano aquí.
                // Se recomienda usar `BCrypt.hashpw(password, BCrypt.gensalt())` como en la ruta '/user/new').
                newUser.set("name", name); // Asigna el nombre al campo 'name'.
                newUser.set("password", password); // Asigna la contraseña al campo 'password'.
                newUser.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                res.status(201); // Created.
                // Devuelve una respuesta JSON con el mensaje y el ID del nuevo usuario.
                return objectMapper.writeValueAsString(Map.of("message", "Usuario '" + name + "' registrado con éxito.", "id", newUser.getId()));

            } catch (Exception e) {
                // Si ocurre cualquier error durante la operación de DB, se captura aquí.
                System.err.println("Error al registrar usuario: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Internal Server Error.
                return objectMapper.writeValueAsString(Map.of("error", "Error interno al registrar usuario: " + e.getMessage()));
            }

            
        });
        get("/get_docente", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String success = req.queryParams("message");
            String error = req.queryParams("error");
            if(success != null && !success.isEmpty()){
                model.put("successMessage", success);
            }
            if(error != null && !error.isEmpty()){
                model.put("errorMessage", error);
            }
            return new ModelAndView(model, "get_docente.mustache");
        }, new MustacheTemplateEngine());

        post("/get_docente", (req, res) -> {
            try {
                String dni = req.queryParams("dni");
                String realName = req.queryParams("realName");
                String surname = req.queryParams("surname");
                String departament = req.queryParams("departament");
                String correo = req.queryParams("correo");

                if(dni == null || dni.isEmpty() || realName == null || realName.isEmpty() || surname == null || surname.isEmpty() || departament == null || departament.isEmpty() ||correo == null || correo.isEmpty()) {
                    res.redirect("/get_docente?error=Todos los campos son obligatorios.");
                    return null;
                }
                Integer dniD = Integer.valueOf(dni);
                // Verificmo si ya existe un docente con ese dni
                Persona persona = Persona.findFirst("dni = ?", dniD);
                if(persona == null){
                    persona = new Persona();
                    persona.setDni(dniD);
                    persona.setRealName(realName);
                    persona.setSurname(surname);
                    persona.saveIt();
                }else{
                    //si la persona ya existe, actualizamos su nombre y apellido
                    persona.setRealName(realName);
                    persona.setSurname(surname);
                    persona.saveIt();
                }

                //ahora  creamos el docente
                //lo mismo con el docente verifico si existe
                Docente docente = Docente.findFirst("dni = ?", dniD);
                if(docente == null){
                    docente = new Docente();
                    docente.setDni(dniD);
                    docente.setDepartament(departament);
                    docente.setCorreo(correo);
                    docente.saveIt();
                }else{
                    //si el docente ya existe, actualizamos su departamento y correo
                    docente.setDepartament(departament);
                    docente.setCorreo(correo);
                    docente.saveIt();
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/get_docente?error=Error al registrar el docente");
            }
            res.redirect("/post_docente?message=Docente cargado exitosamente");
            return null;
        });

        //ahora mostrar los docentes
        get("/post_docente", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String success = req.queryParams("message");
            if(success != null && !success.isEmpty()){
                model.put("successMessage", success);
            }
            var docentes = Docente.findAll(); // Obtiene todos los registros de la tabla 'docente'.
            java.util.List<Map<String, Object>> docenteList = new java.util.ArrayList<>();
            for (Model m : docentes) {
                Docente docente = (Docente) m; // casteo al modelo Docente
                Integer dni = docente.getInteger("dni"); // obtiene el dni del docente
                Persona persona = Persona.findFirst("dni = ?", dni); // busca la persona usando el mismo dni
                Map<String, Object> docenteData = new HashMap<>();
                docenteData.put("dni", persona.getDni());
                docenteData.put("nombre", persona.getRealName());
                docenteData.put("apellido", persona.getSurname());
                docenteData.put("departament", docente.getDepartament());
                docenteData.put("correo", docente.getCorreo());

            
                        // Filtrar materias por la columna correcta
        var materias = Materia.where("id_docente = ?", dni); // ajusta el nombre de columna si es distinto
        java.util.List<Map<String, Object>> materiaList = new java.util.ArrayList<>();
        try {
            for (Model m2 : materias) {
                Materia mat = (Materia) m2;
                Map<String, Object> materiaData = new HashMap<>();
                materiaData.put("id_materia", mat.getInteger("id_materia"));
                materiaData.put("nombre", mat.getString("nombre"));
                materiaData.put("id_carrera", mat.getInteger("id_carrera"));
                materiaList.add(materiaData);
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }
        docenteData.put("materias", materiaList);

        docenteList.add(docenteData);
    
            }
            model.put("docentes", docenteList);
            return new ModelAndView(model, "post_docente.mustache");
        }, new MustacheTemplateEngine());

        get("/get_docente", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String success = req.queryParams("message");
            String error = req.queryParams("error");
            if(success != null && !success.isEmpty()){
                model.put("successMessage", success);
            }
            if(error != null && !error.isEmpty()){
                model.put("errorMessage", error);
            }
            return new ModelAndView(model, "get_docente.mustache");
        }, new MustacheTemplateEngine());

        //ahora mostrar los docentes para elegir cual cargar en la materia
        get("/get_materia", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String success = req.queryParams("message");
            if(success != null && !success.isEmpty()){
                model.put("successMessage", success);
            }
            var docentes = Docente.findAll(); // Obtiene todos los registros de la tabla 'docente'.
            java.util.List<Map<String, Object>> docenteList = new java.util.ArrayList<>();
            for (Model m : docentes) {
                Docente docente = (Docente) m; // casteo al modelo Docente
                Integer dni = docente.getInteger("dni"); // obtiene el dni del docente
                Persona persona = Persona.findFirst("dni = ?", dni); // busca la persona usando el mismo dni
                Map<String, Object> docenteData = new HashMap<>();
                docenteData.put("dni", persona.getDni());
                docenteData.put("nombre", persona.getRealName());
                docenteData.put("apellido", persona.getSurname());
                docenteList.add(docenteData);
            }
            model.put("docentes", docenteList);
            return new ModelAndView(model, "get_materia.mustache");
        }, new MustacheTemplateEngine());

        post("/get_materia", (req, res) -> {
            try {
                String id_materiaS = req.queryParams("id_materia");
                String nombre = req.queryParams("nombre");
                String id_profesorS = req.queryParams("id_profesor");
                String id_carreraS = req.queryParams("id_carrera");

                Integer id_carrera = Integer.valueOf(id_carreraS);
                Integer id_profesor = Integer.valueOf(id_profesorS);
                Docente docente = Docente.findById(id_profesor);
                Integer id_materia = Integer.valueOf(id_materiaS);

                if (docente == null) {
                    res.redirect("/get_materia?error=El docente seleccionado no existe.");
                    return null;
                }

    
                if (id_materiaS == null || id_materiaS.isEmpty()
                    || nombre == null || nombre.isEmpty()
                    || id_carreraS == null || id_carreraS.isEmpty()
                    || id_profesorS == null || id_profesorS.isEmpty()) {
    
                    res.redirect("/get_materia?error=Todos los campos son obligatorios.");
                    return null;
                }


                // Verifico si ya existe una materia con ese id y mismo profesor
                Materia materia = Materia.findFirst("id_materia = ? AND id_profesor = ?", id_materia, id_profesor);
                if(materia == null){
                    materia = new Materia();
                    materia.setIdMateria(id_materia);
                    materia.setNombre(nombre);
                    materia.setIdProfesor(id_profesor);
                    materia.setIdCarrera(id_carrera);
                    materia.saveIt();
                }else{
                    //si la materia ya existe, actualizamos su nombre y carrera
                    materia.setNombre(nombre);
                    materia.setIdCarrera(id_carrera);
                    materia.saveIt();
                }

            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/get_materia?error=Error al registrar la materia");
            }
            res.redirect("/get_materia?message=Materia cargado exitosamente");
            return null;
        });

        get("/get_materia", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String success = req.queryParams("message");
            String error = req.queryParams("error");
            if(success != null && !success.isEmpty()){
                model.put("successMessage", success);
            }
            if(error != null && !error.isEmpty()){
                model.put("errorMessage", error);
            }
            return new ModelAndView(model, "get_materia.mustache");
        }, new MustacheTemplateEngine());


    } // Fin del método main
} // Fin de la clase App