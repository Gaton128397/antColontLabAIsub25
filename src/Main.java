import java.util.*;

/*
Contexto del problema:
Se intentara hacer el problema del Viajante de Comercio (o TSP, por sus siglas en inglés, Traveling Salesperson Problem).
Dado el siguiente contexto y archivo a280.txt . . .
Implementar un algoritmo de colonia de hormigas para resolver el problema del viajante de comercio.
Dado un conjunto de n nodos y las distancias de cada par de nodos, encuentre un viaje
de ida y vuelta con la longitud total mínima que visite cada nodo exactamente una vez.
La distancia del nodo i al nodo j es la misma que la del nodo j al nodo i.
¿Cuánto tiempo tarda de media? Si pruebas diferentes parámetros (número de hormigas, velocidad de evaporación), ¿mejora?
*/

public class Main {
    // Parámetros del algoritmo de colonia de hormigas
    /*
    cada parametro se inicializa estaticamente para facilitar la ejecucion ya que se puede alterar y cambia en Tdo el codigo
    */
    static double alpha;
    static double beta;
    static double rho;
    static double Q;
    static int numHormigas;
    static int maxIteraciones;


    static double[][] distancias;
    static double[][] feromonas;
    static int numCiudades;


    static List<Hormiga> hormigas; //simplemente una lista de hormigas
    static List<Integer> mejorTour;   // va guardando el mejor tour encontrado
    static double mejorDistancia = Double.MAX_VALUE;
    static List<Double> historialMejores = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("colonia de hormigas para el problema del viajante de comercio (TSP)");
        System.out.println("Problema: a280 (280 ciudades)\n");
        // se definenen los parametros de ejecucion antes de ejecutar el algoritmo
        alpha = 1.0;            // Importancia de la feromona
        beta = 2.0;             // Importancia de la heurística/distancia
        rho = 0.5;              // Tasa de evaporación de feromona
        Q = 100.0;              // Constante para deposición de feromona
        numHormigas = 30;       // Número de hormigas
        maxIteraciones = 300;   // Número máximo de iteraciones

        ejecutarAlgoritmo();

        // Opción 2: Probar diferentes configuraciones
        // probarDiferentesParametros();
    }

    /**
     * Ejecuta el algoritmo de colonia de hormigas con los parámetros actuales
     */
    public static void ejecutarAlgoritmo() {
        CreadorCiudades creador = CreadorCiudades.getInstancia(); // cargar ciudades
        distancias = creador.getDistanciasEntreCiudades();//obtener matriz de distancias entre ciudades
        numCiudades = creador.getNumeroCiudades();
        if (distancias == null || numCiudades == 0) {
            System.err.println("Error: No se pudieron cargar las ciudades. Revisar el archivo a280.txt");
            return;
        }
        inicializarFeromonas();
        crearHormigas();
        mejorDistancia = Double.MAX_VALUE;
        mejorTour = null;
        historialMejores.clear();
        System.out.println("Iniciando algoritmo de colonia de hormigas...");
        long tiempoInicio = System.currentTimeMillis();

        // Bucle principal del algoritmo
        for (int iteracion = 0; iteracion < maxIteraciones; iteracion++) {
            //cada hormiga hace su tour en base a las feromonas y distancias (clase Hormiga.java para mas detalles)
            for (Hormiga hormiga : hormigas) {
                hormiga.construirTour(feromonas, distancias);
            }
            actualizarMejorSolucion();
            evaporarFeromonas();
            depositarFeromonas();
            if ((iteracion + 1) % 50 == 0) { // mostrar cada 50 iteraciones el progreso de la ejecución
                System.out.printf("Iteración %d: Mejor distancia = %.2f\n",
                        iteracion + 1, mejorDistancia);
            }
            if (detectarConvergencia()) {
                System.out.println("Convergencia detectada en iteración " + (iteracion + 1));
                break;
            }
        }

        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
        mostrarResultados(tiempoTotal);
    }
    /**
     * Inicializa la matriz de feromonas con valores uniformes
     */
    public static void inicializarFeromonas() {
        feromonas = new double[numCiudades][numCiudades];
        double feromonaInicial = 1.0;

        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j) {
                    feromonas[i][j] = feromonaInicial;
                } else {
                    feromonas[i][j] = 0.0; // No hay feromona en la diagonal
                }
            }
        }
    }

    /**
     * Crea la colonia de hormigas
     */
    public static void crearHormigas() {
        hormigas = new ArrayList<>();
        for (int i = 0; i < numHormigas; i++) {
            hormigas.add(new Hormiga(numCiudades, alpha, beta));
        }
    }

    /**
     * Actualiza la mejor solución encontrada hasta el momento
     */
    public static void actualizarMejorSolucion() {
        for (Hormiga hormiga : hormigas) {
            if (hormiga.getDistanciaTotal() < mejorDistancia) {
                mejorDistancia = hormiga.getDistanciaTotal();
                mejorTour = new ArrayList<>(hormiga.getTour());
            }
        }
        historialMejores.add(mejorDistancia);
    }

    /**
     * Aplica evaporación a todas las feromonas
     * Nueva_feromona = (1 - ρ) × Feromona_actual
     */
    public static void evaporarFeromonas() {
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j) {
                    feromonas[i][j] = (1.0 - rho) * feromonas[i][j];

                    // Evitar que la feromona se vuelva demasiado pequeña
                    if (feromonas[i][j] < 0.001) {
                        feromonas[i][j] = 0.001;
                    }
                }
            }
        }
    }

    /**
     * Las hormigas depositan feromona en las aristas que usaron
     */
    public static void depositarFeromonas() {
        for (Hormiga hormiga : hormigas) {
            double feromonaADepositar = Q / hormiga.getDistanciaTotal();
            List<Integer> tour = hormiga.getTour();

            // Depositar feromona en cada arista del tour
            for (int i = 0; i < tour.size(); i++) {
                int ciudadA = tour.get(i);
                int ciudadB = tour.get((i + 1) % tour.size()); // Vuelta al origen

                feromonas[ciudadA][ciudadB] += feromonaADepositar;
                feromonas[ciudadB][ciudadA] += feromonaADepositar; // Grafo no dirigido
            }
        }
    }

    /**
     * Detecta si el algoritmo ha convergido
     */
    public static boolean detectarConvergencia() {
        if (historialMejores.size() < 100) return false;

        // Verificar si no ha mejorado en las últimas 100 iteraciones
        int ultimas = Math.min(100, historialMejores.size());
        double ultimaMejor = historialMejores.get(historialMejores.size() - 1);

        for (int i = historialMejores.size() - ultimas; i < historialMejores.size(); i++) {
            if (Math.abs(historialMejores.get(i) - ultimaMejor) > 0.01) {
                return false; // Aún hay mejoras
            }
        }

        return true; // Convergencia detectada
    }

    /**
     * Muestra los resultados finales del algoritmo
     */
    public static void mostrarResultados(long tiempoTotal) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESULTADOS DEL ALGORITMO DE COLONIA DE HORMIGAS");
        System.out.println("=".repeat(60));
        System.out.printf("Mejor distancia encontrada: %.2f\n", mejorDistancia);
        System.out.printf("Tiempo de ejecución: %.3f segundos\n", tiempoTotal / 1000.0);
        System.out.printf("Iteraciones promedio: %.1f por segundo\n",
                (maxIteraciones * 1000.0) / tiempoTotal);

        if (mejorTour != null) {
            System.out.println("\nMejor tour encontrado:");
            mostrarTour(mejorTour);
        }

        System.out.println("=".repeat(60));
    }

    /**
     * Muestra el tour de forma compacta
     */
    public static void mostrarTour(List<Integer> tour) {
        System.out.print("Ciudades: ");

        // Mostrar solo los primeros 15 y últimos 5 para evitar spam
        int mostrarInicio = Math.min(15, tour.size());
        for (int i = 0; i < mostrarInicio; i++) {
            System.out.print((tour.get(i) + 1) + " -> ");
        }

        if (tour.size() > 15) {
            System.out.print("... -> ");
            int empezarFinal = Math.max(15, tour.size() - 5);
            for (int i = empezarFinal; i < tour.size(); i++) {
                System.out.print((tour.get(i) + 1) + " -> ");
            }
        }

        System.out.println((tour.get(0) + 1)); // Vuelta al origen
    }

    /**
     * Función para probar diferentes configuraciones de parámetros
     */
    public static void probarDiferentesParametros() {
        System.out.println("Probando diferentes configuraciones de parámetros...\n");

        // Guardar parámetros originales
        double alphaOriginal = alpha;
        double betaOriginal = beta;
        double rhoOriginal = rho;
        int hormigasOriginal = numHormigas;
        int iteracionesOriginal = maxIteraciones;

        // Configuraciones a probar
        Object[][] configuraciones = {
                {"Estándar", 30, 1.0, 2.0, 0.5, 300},
                {"Pocas hormigas", 15, 1.0, 2.0, 0.5, 300},
                {"Muchas hormigas", 50, 1.0, 2.0, 0.5, 300},
                {"Baja evaporación", 30, 1.0, 2.0, 0.3, 300},
                {"Alta evaporación", 30, 1.0, 2.0, 0.7, 300},
                {"Más feromona", 30, 2.0, 1.5, 0.5, 300},
                {"Más heurística", 30, 0.5, 3.0, 0.5, 300}
        };

        List<Double> resultados = new ArrayList<>();
        List<String> nombres = new ArrayList<>();

        for (Object[] config : configuraciones) {
            String nombre = (String) config[0];
            numHormigas = (Integer) config[1];
            alpha = (Double) config[2];
            beta = (Double) config[3];
            rho = (Double) config[4];
            maxIteraciones = (Integer) config[5];

            System.out.println("=== Configuración: " + nombre + " ===");
            ejecutarAlgoritmo();

            resultados.add(mejorDistancia);
            nombres.add(nombre);
            System.out.println();
        }

        // Mostrar comparación final
        System.out.println("=".repeat(60));
        System.out.println("COMPARACIÓN DE CONFIGURACIONES");
        System.out.println("=".repeat(60));
        for (int i = 0; i < nombres.size(); i++) {
            System.out.printf("%-20s: %.2f\n", nombres.get(i), resultados.get(i));
        }

        // Encontrar la mejor configuración
        double mejorResultado = Collections.min(resultados);
        int indiceMejor = resultados.indexOf(mejorResultado);
        System.out.println("\nMejor configuración: " + nombres.get(indiceMejor) +
                " (distancia: " + mejorResultado + ")");

        // Restaurar parámetros originales
        alpha = alphaOriginal;
        beta = betaOriginal;
        rho = rhoOriginal;
        numHormigas = hormigasOriginal;
        maxIteraciones = iteracionesOriginal;
    }
}