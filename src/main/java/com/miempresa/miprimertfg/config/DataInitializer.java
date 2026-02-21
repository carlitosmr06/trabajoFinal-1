package com.miempresa.miprimertfg.config;

import com.miempresa.miprimertfg.model.*;
import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.ThemeService;
import com.miempresa.miprimertfg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UserService userService;
        private final ThemeService themeService;
        private final QuestionService questionService;

        @Override
        public void run(String... args) throws Exception {
                // Create default admin user if not exists
                if (!userService.existsByUsername("admin")) {
                        userService.registerAdmin("admin", "admin@example.com", "admin");
                        System.out.println("Admin user created: admin / admin");
                }

                // Create default regular user if not exists
                if (!userService.existsByUsername("user")) {
                        userService.register("user", "user@example.com", "user123");
                        System.out.println("Regular user created: user / user123");
                }

                // Create some themes
                Theme java = themeService.findOrCreate("Java");
                Theme spring = themeService.findOrCreate("Spring Boot");
                Theme database = themeService.findOrCreate("Bases de Datos");

                // Add some sample questions if database is empty
                if (questionService.countAll() == 0) {
                        // True/False questions
                        TrueFalseQuestion tfq1 = new TrueFalseQuestion();
                        tfq1.setQuestionText("¿Java es un lenguaje orientado a objetos?");
                        tfq1.setTheme(java);
                        tfq1.setDifficulty(Difficulty.EASY);
                        tfq1.setCreatedBy("admin");
                        tfq1.setCorrectAnswer(true);
                        questionService.saveTrueFalse(tfq1);

                        TrueFalseQuestion tfq2 = new TrueFalseQuestion();
                        tfq2.setQuestionText("¿Spring Boot requiere configuración XML?");
                        tfq2.setTheme(spring);
                        tfq2.setDifficulty(Difficulty.MEDIUM);
                        tfq2.setCreatedBy("admin");
                        tfq2.setCorrectAnswer(false);
                        questionService.saveTrueFalse(tfq2);

                        // Single choice questions
                        SingleChoiceQuestion scq1 = new SingleChoiceQuestion();
                        scq1.setQuestionText("¿Qué es Spring Boot?");
                        scq1.setTheme(spring);
                        scq1.setDifficulty(Difficulty.EASY);
                        scq1.setCreatedBy("admin");
                        scq1.setOptions(Arrays.asList(
                                        "Un framework para Java",
                                        "Un lenguaje de programación",
                                        "Una base de datos",
                                        "Un IDE"));
                        scq1.setCorrectAnswerIndex(0);
                        questionService.saveSingleChoice(scq1);

                        SingleChoiceQuestion scq2 = new SingleChoiceQuestion();
                        scq2.setQuestionText("¿Qué tipo de base de datos es MySQL?");
                        scq2.setTheme(database);
                        scq2.setDifficulty(Difficulty.EASY);
                        scq2.setCreatedBy("admin");
                        scq2.setOptions(Arrays.asList(
                                        "NoSQL",
                                        "Relacional",
                                        "Gráfica",
                                        "Documental"));
                        scq2.setCorrectAnswerIndex(1);
                        questionService.saveSingleChoice(scq2);

                        // Multiple choice questions
                        MultipleChoiceQuestion mcq1 = new MultipleChoiceQuestion();
                        mcq1.setQuestionText("¿Cuáles son tipos primitivos en Java?");
                        mcq1.setTheme(java);
                        mcq1.setDifficulty(Difficulty.MEDIUM);
                        mcq1.setCreatedBy("admin");
                        mcq1.setOptions(Arrays.asList(
                                        "int",
                                        "String",
                                        "boolean",
                                        "double"));
                        mcq1.setCorrectAnswerIndices(Arrays.asList(0, 2, 3));
                        questionService.saveMultipleChoice(mcq1);

                        // Additional questions to reach 10 total

                        // True/False question #3
                        TrueFalseQuestion tfq3 = new TrueFalseQuestion();
                        tfq3.setQuestionText("¿SQL es un lenguaje de consulta estructurado?");
                        tfq3.setTheme(database);
                        tfq3.setDifficulty(Difficulty.EASY);
                        tfq3.setCreatedBy("admin");
                        tfq3.setCorrectAnswer(true);
                        questionService.saveTrueFalse(tfq3);

                        // Single choice question #3
                        SingleChoiceQuestion scq3 = new SingleChoiceQuestion();
                        scq3.setQuestionText(
                                        "¿Qué anotación se usa para marcar una clase como controlador REST en Spring?");
                        scq3.setTheme(spring);
                        scq3.setDifficulty(Difficulty.MEDIUM);
                        scq3.setCreatedBy("admin");
                        scq3.setOptions(Arrays.asList(
                                        "@Controller",
                                        "@RestController",
                                        "@Service",
                                        "@Component"));
                        scq3.setCorrectAnswerIndex(1);
                        questionService.saveSingleChoice(scq3);

                        // Single choice question #4
                        SingleChoiceQuestion scq4 = new SingleChoiceQuestion();
                        scq4.setQuestionText("¿Cuál es la palabra clave para heredar de una clase en Java?");
                        scq4.setTheme(java);
                        scq4.setDifficulty(Difficulty.EASY);
                        scq4.setCreatedBy("admin");
                        scq4.setOptions(Arrays.asList(
                                        "implements",
                                        "extends",
                                        "inherits",
                                        "super"));
                        scq4.setCorrectAnswerIndex(1);
                        questionService.saveSingleChoice(scq4);

                        // Multiple choice question #2
                        MultipleChoiceQuestion mcq2 = new MultipleChoiceQuestion();
                        mcq2.setQuestionText("¿Cuáles de estas son anotaciones válidas de Spring Boot?");
                        mcq2.setTheme(spring);
                        mcq2.setDifficulty(Difficulty.HARD);
                        mcq2.setCreatedBy("admin");
                        mcq2.setOptions(Arrays.asList(
                                        "@SpringBootApplication",
                                        "@Autowired",
                                        "@Bean",
                                        "@Main"));
                        mcq2.setCorrectAnswerIndices(Arrays.asList(0, 1, 2));
                        questionService.saveMultipleChoice(mcq2);

                        // Multiple choice question #3
                        MultipleChoiceQuestion mcq3 = new MultipleChoiceQuestion();
                        mcq3.setQuestionText("¿Qué operaciones son parte del CRUD en bases de datos?");
                        mcq3.setTheme(database);
                        mcq3.setDifficulty(Difficulty.MEDIUM);
                        mcq3.setCreatedBy("admin");
                        mcq3.setOptions(Arrays.asList(
                                        "Create",
                                        "Read",
                                        "Update",
                                        "Delete"));
                        mcq3.setCorrectAnswerIndices(Arrays.asList(0, 1, 2, 3));
                        questionService.saveMultipleChoice(mcq3);

                        System.out.println("Initial 10 questions created");

                        // Add 90 more questions
                        createAdditionalQuestions(java, spring, database);

                        System.out.println("100 sample questions created successfully!");
                }
        }

        private void createAdditionalQuestions(Theme java, Theme spring, Theme database) {
                // JAVA QUESTIONS (30 more)
                createSCQ(java, "¿Qué palabra clave se usa para crear una constante en Java?",
                                Arrays.asList("final", "const", "static", "immutable"), 0, Difficulty.EASY);
                createSCQ(java, "¿Cuál es el tamaño de un int en Java?",
                                Arrays.asList("16 bits", "32 bits", "64 bits", "8 bits"), 1, Difficulty.MEDIUM);
                createSCQ(java, "¿Qué interfaz deben implementar las clases para ser comparables?",
                                Arrays.asList("Comparable", "Comparator", "Serializable", "Cloneable"), 0,
                                Difficulty.MEDIUM);
                createSCQ(java, "¿Cuál es el valor por defecto de un boolean en Java?",
                                Arrays.asList("true", "false", "null", "0"), 1, Difficulty.EASY);
                createSCQ(java, "¿Qué método se usa para obtener la longitud de un array?",
                                Arrays.asList("size()", "length()", "length", "getSize()"), 2, Difficulty.EASY);

                createTF(java, "¿Una clase abstracta puede tener métodos concretos?", true, Difficulty.MEDIUM);
                createTF(java, "¿Java permite herencia múltiple de clases?", false, Difficulty.MEDIUM);
                createTF(java, "¿El operador == compara referencias de objetos?", true, Difficulty.HARD);
                createTF(java, "¿String es un tipo primitivo en Java?", false, Difficulty.EASY);
                createTF(java, "¿Los métodos static pueden ser sobrescritos?", false, Difficulty.HARD);

                createMCQ(java, "¿Cuáles son palabras reservadas en Java?",
                                Arrays.asList("goto", "const", "class", "interface"), Arrays.asList(0, 1, 2, 3),
                                Difficulty.MEDIUM);
                createMCQ(java, "¿Qué colecciones permiten duplicados?",
                                Arrays.asList("ArrayList", "HashSet", "LinkedList", "TreeSet"), Arrays.asList(0, 2),
                                Difficulty.MEDIUM);
                createMCQ(java, "¿Cuáles son tipos de datos numéricos en Java?",
                                Arrays.asList("int", "float", "char", "double"), Arrays.asList(0, 1, 3),
                                Difficulty.EASY);

                createSCQ(java, "¿Qué excepción se lanza al dividir por cero en enteros?",
                                Arrays.asList("ArithmeticException", "NullPointerException", "DivisionException",
                                                "MathException"),
                                0, Difficulty.MEDIUM);
                createSCQ(java, "¿Cuál es el modificador para que una variable sea accesible solo dentro del paquete?",
                                Arrays.asList("public", "private", "protected", "default"), 3, Difficulty.HARD);
                createSCQ(java, "¿Qué método se llama cuando se crea un objeto?",
                                Arrays.asList("init()", "constructor", "create()", "new()"), 1, Difficulty.EASY);
                createSCQ(java, "¿Cuál es la clase padre de todas las clases en Java?",
                                Arrays.asList("Object", "Class", "Super", "Root"), 0, Difficulty.EASY);
                createSCQ(java, "¿Qué interfaz funcional representa una operación sin argumentos?",
                                Arrays.asList("Supplier", "Consumer", "Function", "Predicate"), 0, Difficulty.HARD);

                createTF(java, "¿Los arrays en Java son objetos?", true, Difficulty.MEDIUM);
                createTF(java, "¿Se puede usar break en un bucle for?", true, Difficulty.EASY);
                createTF(java, "¿Las variables locales tienen valor por defecto?", false, Difficulty.MEDIUM);
                createTF(java, "¿Una interfaz puede extender múltiples interfaces?", true, Difficulty.MEDIUM);

                createMCQ(java, "¿Cuáles son modificadores de acceso válidos?",
                                Arrays.asList("public", "private", "protected", "package"), Arrays.asList(0, 1, 2),
                                Difficulty.EASY);
                createMCQ(java, "¿Qué estructuras de control existen en Java?",
                                Arrays.asList("if", "for", "switch", "loop"), Arrays.asList(0, 1, 2), Difficulty.EASY);

                createSCQ(java, "¿Qué palabra clave se usa para referirse a la clase padre?",
                                Arrays.asList("this", "super", "parent", "base"), 1, Difficulty.EASY);
                createSCQ(java, "¿Cuál es el operador lógico AND en Java?",
                                Arrays.asList("&", "&&", "AND", "and"), 1, Difficulty.EASY);
                createSCQ(java, "¿Qué método se usa para comparar Strings?",
                                Arrays.asList("compare()", "equals()", "==", "compareTo()"), 1, Difficulty.MEDIUM);
                createSCQ(java, "¿Cuál es el rango de un byte en Java?",
                                Arrays.asList("-128 a 127", "0 a 255", "-256 a 255", "-127 a 128"), 0, Difficulty.HARD);
                createSCQ(java, "¿Qué colección mantiene el orden de inserción?",
                                Arrays.asList("HashSet", "TreeSet", "LinkedHashSet", "HashMap"), 2, Difficulty.MEDIUM);
                createSCQ(java, "¿Cuál es la extensión de un archivo Java compilado?",
                                Arrays.asList(".java", ".class", ".jar", ".exe"), 1, Difficulty.EASY);

                // SPRING BOOT QUESTIONS (30 more)
                createSCQ(spring, "¿Qué anotación marca una clase como componente de Spring?",
                                Arrays.asList("@Component", "@Bean", "@Service", "@Autowired"), 0, Difficulty.EASY);
                createSCQ(spring, "¿Cuál es el puerto por defecto de Spring Boot?",
                                Arrays.asList("8000", "8080", "3000", "9090"), 1, Difficulty.EASY);
                createSCQ(spring, "¿Qué anotación se usa para mapear una petición GET?",
                                Arrays.asList("@Get", "@GetMapping", "@RequestGet", "@HttpGet"), 1, Difficulty.EASY);
                createSCQ(spring, "¿Cuál es el archivo de configuración principal de Spring Boot?",
                                Arrays.asList("config.properties", "application.properties", "spring.properties",
                                                "settings.properties"),
                                1, Difficulty.EASY);
                createSCQ(spring, "¿Qué anotación marca un método como transaccional?",
                                Arrays.asList("@Transaction", "@Transactional", "@Tx", "@Atomic"), 1,
                                Difficulty.MEDIUM);

                createTF(spring, "¿Spring Boot incluye un servidor embebido?", true, Difficulty.EASY);
                createTF(spring, "¿@Service es una especialización de @Component?", true, Difficulty.MEDIUM);
                createTF(spring, "¿Spring Boot requiere un servidor de aplicaciones externo?", false, Difficulty.EASY);
                createTF(spring, "¿@Repository se usa para la capa de persistencia?", true, Difficulty.EASY);
                createTF(spring, "¿Spring Boot soporta configuración con anotaciones?", true, Difficulty.EASY);

                createMCQ(spring, "¿Cuáles son anotaciones de mapeo de peticiones?",
                                Arrays.asList("@GetMapping", "@PostMapping", "@PutMapping", "@HttpMapping"),
                                Arrays.asList(0, 1, 2), Difficulty.MEDIUM);
                createMCQ(spring, "¿Qué componentes forman parte de Spring MVC?",
                                Arrays.asList("Controller", "Service", "Repository", "Component"),
                                Arrays.asList(0, 1, 2), Difficulty.MEDIUM);
                createMCQ(spring, "¿Cuáles son scopes de beans en Spring?",
                                Arrays.asList("singleton", "prototype", "request", "global"), Arrays.asList(0, 1, 2),
                                Difficulty.HARD);

                createSCQ(spring, "¿Qué anotación se usa para validar datos de entrada?",
                                Arrays.asList("@Valid", "@Validate", "@Check", "@Verify"), 0, Difficulty.MEDIUM);
                createSCQ(spring, "¿Cuál es el contenedor de IoC de Spring?",
                                Arrays.asList("BeanFactory", "ApplicationContext", "Container", "Context"), 1,
                                Difficulty.HARD);
                createSCQ(spring, "¿Qué anotación configura propiedades desde archivos?",
                                Arrays.asList("@Value", "@Property", "@Config", "@Setting"), 0, Difficulty.MEDIUM);
                createSCQ(spring, "¿Cuál es la anotación para manejar excepciones globalmente?",
                                Arrays.asList("@ExceptionHandler", "@ControllerAdvice", "@ErrorHandler",
                                                "@GlobalException"),
                                1, Difficulty.HARD);
                createSCQ(spring, "¿Qué anotación marca una clase de configuración?",
                                Arrays.asList("@Config", "@Configuration", "@Settings", "@Setup"), 1, Difficulty.EASY);

                createTF(spring, "¿Spring Data JPA facilita el acceso a datos?", true, Difficulty.EASY);
                createTF(spring, "¿@Autowired puede inyectar por constructor?", true, Difficulty.MEDIUM);
                createTF(spring, "¿Spring Boot Actuator proporciona endpoints de monitoreo?", true, Difficulty.MEDIUM);
                createTF(spring, "¿@PathVariable extrae valores de la URL?", true, Difficulty.EASY);

                createMCQ(spring, "¿Qué módulos incluye Spring Framework?",
                                Arrays.asList("Spring Core", "Spring MVC", "Spring Data", "Spring UI"),
                                Arrays.asList(0, 1, 2), Difficulty.MEDIUM);
                createMCQ(spring, "¿Cuáles son tipos de inyección de dependencias?",
                                Arrays.asList("Constructor", "Setter", "Field", "Method"), Arrays.asList(0, 1, 2),
                                Difficulty.MEDIUM);

                createSCQ(spring, "¿Qué anotación marca un parámetro de consulta?",
                                Arrays.asList("@QueryParam", "@RequestParam", "@Param", "@Query"), 1, Difficulty.EASY);
                createSCQ(spring, "¿Cuál es el starter para aplicaciones web?",
                                Arrays.asList("spring-boot-starter-web", "spring-web-starter", "spring-boot-web",
                                                "web-starter"),
                                0, Difficulty.EASY);
                createSCQ(spring, "¿Qué anotación habilita el autoconfiguración?",
                                Arrays.asList("@EnableAutoConfiguration", "@AutoConfig", "@SpringBootApplication",
                                                "@Auto"),
                                0, Difficulty.MEDIUM);
                createSCQ(spring, "¿Cuál es la anotación para programar tareas?",
                                Arrays.asList("@Scheduled", "@Schedule", "@Task", "@Cron"), 0, Difficulty.MEDIUM);
                createSCQ(spring, "¿Qué anotación marca un método de inicialización?",
                                Arrays.asList("@Init", "@PostConstruct", "@OnInit", "@Start"), 1, Difficulty.HARD);
                createSCQ(spring, "¿Cuál es el patrón de diseño principal de Spring?",
                                Arrays.asList("Singleton", "Factory", "Dependency Injection", "Observer"), 2,
                                Difficulty.MEDIUM);

                // DATABASE QUESTIONS (30 more)
                createSCQ(database, "¿Qué comando SQL se usa para crear una tabla?",
                                Arrays.asList("CREATE TABLE", "NEW TABLE", "MAKE TABLE", "ADD TABLE"), 0,
                                Difficulty.EASY);
                createSCQ(database, "¿Cuál es la cláusula para filtrar resultados en SQL?",
                                Arrays.asList("FILTER", "WHERE", "HAVING", "IF"), 1, Difficulty.EASY);
                createSCQ(database, "¿Qué tipo de JOIN devuelve todas las filas de ambas tablas?",
                                Arrays.asList("INNER JOIN", "LEFT JOIN", "FULL OUTER JOIN", "CROSS JOIN"), 2,
                                Difficulty.MEDIUM);
                createSCQ(database, "¿Cuál es el comando para eliminar una tabla?",
                                Arrays.asList("DELETE TABLE", "DROP TABLE", "REMOVE TABLE", "DESTROY TABLE"), 1,
                                Difficulty.EASY);
                createSCQ(database, "¿Qué restricción asegura valores únicos?",
                                Arrays.asList("PRIMARY KEY", "UNIQUE", "NOT NULL", "CHECK"), 1, Difficulty.EASY);

                createTF(database, "¿Una clave primaria puede ser NULL?", false, Difficulty.EASY);
                createTF(database, "¿SQL es case-sensitive por defecto?", false, Difficulty.MEDIUM);
                createTF(database, "¿Un índice mejora la velocidad de búsqueda?", true, Difficulty.EASY);
                createTF(database, "¿DELETE elimina la estructura de la tabla?", false, Difficulty.MEDIUM);
                createTF(database, "¿Una transacción puede ser revertida con ROLLBACK?", true, Difficulty.MEDIUM);

                createMCQ(database, "¿Cuáles son comandos DDL?",
                                Arrays.asList("CREATE", "SELECT", "DROP", "ALTER"), Arrays.asList(0, 2, 3),
                                Difficulty.MEDIUM);
                createMCQ(database, "¿Qué funciones agregadas existen en SQL?",
                                Arrays.asList("COUNT", "SUM", "AVG", "TOTAL"), Arrays.asList(0, 1, 2), Difficulty.EASY);
                createMCQ(database, "¿Cuáles son tipos de relaciones en bases de datos?",
                                Arrays.asList("Uno a Uno", "Uno a Muchos", "Muchos a Muchos", "Todos a Todos"),
                                Arrays.asList(0, 1, 2), Difficulty.MEDIUM);

                createSCQ(database, "¿Qué comando actualiza datos existentes?",
                                Arrays.asList("MODIFY", "UPDATE", "CHANGE", "ALTER"), 1, Difficulty.EASY);
                createSCQ(database, "¿Cuál es la cláusula para ordenar resultados?",
                                Arrays.asList("SORT BY", "ORDER BY", "ARRANGE BY", "RANK BY"), 1, Difficulty.EASY);
                createSCQ(database, "¿Qué comando inicia una transacción?",
                                Arrays.asList("START TRANSACTION", "BEGIN", "INIT TRANSACTION", "NEW TRANSACTION"), 1,
                                Difficulty.MEDIUM);
                createSCQ(database, "¿Cuál es la función para contar filas?",
                                Arrays.asList("COUNT()", "ROWS()", "NUMBER()", "TOTAL()"), 0, Difficulty.EASY);
                createSCQ(database, "¿Qué restricción evita valores NULL?",
                                Arrays.asList("NOT NULL", "REQUIRED", "MANDATORY", "FILLED"), 0, Difficulty.EASY);

                createTF(database, "¿INNER JOIN devuelve solo coincidencias?", true, Difficulty.MEDIUM);
                createTF(database, "¿GROUP BY agrupa filas con valores similares?", true, Difficulty.MEDIUM);
                createTF(database, "¿Una clave foránea puede referenciar múltiples tablas?", false, Difficulty.HARD);
                createTF(database, "¿TRUNCATE es más rápido que DELETE?", true, Difficulty.HARD);

                createMCQ(database, "¿Cuáles son comandos DML?",
                                Arrays.asList("INSERT", "UPDATE", "DELETE", "CREATE"), Arrays.asList(0, 1, 2),
                                Difficulty.MEDIUM);
                createMCQ(database, "¿Qué tipos de índices existen?",
                                Arrays.asList("Clustered", "Non-Clustered", "Unique", "Foreign"),
                                Arrays.asList(0, 1, 2), Difficulty.HARD);

                createSCQ(database, "¿Cuál es el comando para insertar datos?",
                                Arrays.asList("ADD", "INSERT", "PUT", "APPEND"), 1, Difficulty.EASY);
                createSCQ(database, "¿Qué operador se usa para buscar patrones?",
                                Arrays.asList("MATCH", "LIKE", "SEARCH", "FIND"), 1, Difficulty.EASY);
                createSCQ(database, "¿Cuál es la cláusula para limitar resultados?",
                                Arrays.asList("LIMIT", "TOP", "MAX", "RESTRICT"), 0, Difficulty.MEDIUM);
                createSCQ(database, "¿Qué nivel de aislamiento es el más estricto?",
                                Arrays.asList("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE"),
                                3, Difficulty.HARD);
                createSCQ(database, "¿Cuál es el comando para confirmar una transacción?",
                                Arrays.asList("SAVE", "COMMIT", "CONFIRM", "APPLY"), 1, Difficulty.EASY);
                createSCQ(database, "¿Qué tipo de dato almacena fechas?",
                                Arrays.asList("DATE", "TIME", "DATETIME", "TIMESTAMP"), 0, Difficulty.EASY);
        }

        // Helper methods
        private void createSCQ(Theme theme, String text, List<String> options, int correctIndex,
                        Difficulty difficulty) {
                SingleChoiceQuestion q = new SingleChoiceQuestion();
                q.setQuestionText(text);
                q.setTheme(theme);
                q.setDifficulty(difficulty);
                q.setCreatedBy("admin");
                q.setOptions(options);
                q.setCorrectAnswerIndex(correctIndex);
                questionService.saveSingleChoice(q);
        }

        private void createTF(Theme theme, String text, boolean answer, Difficulty difficulty) {
                TrueFalseQuestion q = new TrueFalseQuestion();
                q.setQuestionText(text);
                q.setTheme(theme);
                q.setDifficulty(difficulty);
                q.setCreatedBy("admin");
                q.setCorrectAnswer(answer);
                questionService.saveTrueFalse(q);
        }

        private void createMCQ(Theme theme, String text, List<String> options, List<Integer> correctIndices,
                        Difficulty difficulty) {
                MultipleChoiceQuestion q = new MultipleChoiceQuestion();
                q.setQuestionText(text);
                q.setTheme(theme);
                q.setDifficulty(difficulty);
                q.setCreatedBy("admin");
                q.setOptions(options);
                q.setCorrectAnswerIndices(correctIndices);
                questionService.saveMultipleChoice(q);
        }
}
