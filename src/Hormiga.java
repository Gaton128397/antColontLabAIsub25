import java.util.*;

public class Hormiga {
    private List<Integer> tour;           // Recorrido de la hormiga (índices de ciudades)
    private boolean[] visitadas;          // Ciudades ya visitadas
    private double distanciaTotal;        // Distancia total del tour
    private Random random;
    private int ciudadActual;            // Ciudad donde está actualmente

    // Parámetros del algoritmo (pueden ser configurables)
    private double alpha;                // Importancia de la feromona
    private double beta;                 // Importancia de la distancia (heurística)

    public Hormiga(int numCiudades, double alpha, double beta) {
        this.tour = new ArrayList<>();
        this.visitadas = new boolean[numCiudades];
        this.random = new Random();
        this.alpha = alpha;
        this.beta = beta;
        this.distanciaTotal = 0.0;
    }

    public Hormiga(int numCiudades) {
        this(numCiudades, 1.0, 2.0); // Valores por defecto
    }

    /**
     * Construye un tour completo visitando todas las ciudades
     */
    public void construirTour(double[][] feromonas, double[][] distancias) {
        reset();
        int numCiudades = distancias.length;

        // 1. Elegir ciudad inicial aleatoriamente
        ciudadActual = random.nextInt(numCiudades);
        visitarCiudad(ciudadActual);

        // 2. Construir el resto del tour
        for (int i = 1; i < numCiudades; i++) {
            int siguienteCiudad = elegirSiguienteCiudad(feromonas, distancias);
            visitarCiudad(siguienteCiudad);
            ciudadActual = siguienteCiudad;
        }

        // 3. Calcular distancia total del tour
        calcularDistanciaTotal(distancias);
    }

    /**
     * Elige la siguiente ciudad usando la regla de transición probabilística
     */
    private int elegirSiguienteCiudad(double[][] feromonas, double[][] distancias) {
        List<Integer> ciudadesDisponibles = getCiudadesDisponibles();

        if (ciudadesDisponibles.isEmpty()) {
            System.out.println("Error: No hay ciudades disponibles para visitar.");
        }

        if (ciudadesDisponibles.size() == 1) {
            return ciudadesDisponibles.get(0);
        }
        double[] probabilidades = calcularProbabilidades(ciudadesDisponibles, feromonas, distancias);
        return seleccionRuleta(ciudadesDisponibles, probabilidades);
    }

    /**
     * Calcula las probabilidades de transición para las ciudades disponibles
     */
    private double[] calcularProbabilidades(List<Integer> ciudadesDisponibles, double[][] feromonas, double[][] distancias) {
        double[] probabilidades = new double[ciudadesDisponibles.size()];
        double sumTotal = 0.0;
        //Fuente de la fórmula:
        //https://cap.stanford.edu/profiles/cwmd?cwmId=10839&fid=301672
        // probabilidad de ir de i a j = (feromona(i,j)^alpha) * (heurística(i,j)^beta) / sum((feromona(i,k)^alpha) * (heurística(i,k)^beta)) para todas las k no visitadas

        for (int i = 0; i < ciudadesDisponibles.size(); i++) {
            int ciudad = ciudadesDisponibles.get(i);
            double feromona = feromonas[ciudadActual][ciudad];
            double heuristica = 1.0 / distancias[ciudadActual][ciudad]; // Inversa de la distancia
            //En caso de que la distancia sea 0 (misma ciudad), evitar división por cero por que no tiene sentido y se puede morir el programa
            if (distancias[ciudadActual][ciudad] == 0) {
                heuristica = Double.MAX_VALUE; //el double max_value es un número muy grande, así que la probabilidad de elegir esa ciudad será muy alta
            }
            probabilidades[i] = Math.pow(feromona, alpha) * Math.pow(heuristica, beta); // numerador de la fórmula
            //cabe mencionar que se obtienen valores muy grandes, pero al final se normalizan para que queden como probabilidades (ejemplo: 0.2, 0.5, 0.3)
            sumTotal += probabilidades[i]; //determinar el denominador de la fórmula
        }
        //aqui se normalizan las probabilidades, para que queden en probabilidades entre 0 y 1
        if (sumTotal > 0) {
            for (int i = 0; i < probabilidades.length; i++) {
                probabilidades[i] = probabilidades[i] / sumTotal;
            }
        } else {
            // En caso de que todas las probabilidades sean 0 (distancias infinitas), asignar probabilidades uniformes para no tener valores NaN (Not a Number) y se muera el programa
            double probUniforme = 1.0 / probabilidades.length; //probabilidad uniforme de 1/n donde n es el número de ciudades disponibles y asi quedan iguales
            Arrays.fill(probabilidades, probUniforme);// esto salva de que se muera el programa
        }

        return probabilidades;
    }
    // Selección por ruleta
    // Fuente: https://es.wikipedia.org/wiki/Selecci%C3%B3n_por_ruleta
    //basicamente divide las probabilidades en segmentos (como una ruleta) y se elige un número aleatorio entre 0 y 1
    //y se ve en qué segmento cae ese número aleatorio
    private int seleccionRuleta(List<Integer> ciudadesDisponibles, double[] probabilidades) {
        double r = random.nextDouble();//se gira la ruleta y se obtiene un número aleatorio entre 0 y 1
        double acumulado = 0.0; //se va acumulando la probabilidad de cada ciudad
        for (int i = 0; i < probabilidades.length; i++) { //recorrer todas las ciudades disponibles
            acumulado += probabilidades[i]; //se va acumulando la probabilidad de cada ciudad
            if (r <= acumulado) {//se va chequeando en qué segmento cae el número aleatorio y si cae en el segmento de la ciudad i
                return ciudadesDisponibles.get(i);//se retorna la ciudad i
            }
        }
        // En caso de que no se haya seleccionado ninguna ciudad (por errores de redondeo), retornar la última ciudad disponible
        return ciudadesDisponibles.get(ciudadesDisponibles.size() - 1);
    }

    /**
     * Obtiene las ciudades que aún no han sido visitadas
     */
    private List<Integer> getCiudadesDisponibles() {
        List<Integer> disponibles = new ArrayList<>();
        for (int i = 0; i < visitadas.length; i++) {
            if (!visitadas[i]) {
                disponibles.add(i);
            }
        }
        return disponibles;
    }

    /**
     * Marca una ciudad como visitada y la añade al tour
     */
    private void visitarCiudad(int ciudad) {
        visitadas[ciudad] = true;
        tour.add(ciudad);
    }

    /**
     * Calcula la distancia total del tour (incluyendo vuelta al origen)
     */
    private void calcularDistanciaTotal(double[][] distancias) {
        distanciaTotal = 0.0;

        if (tour.size() < 2) return;

        // Sumar distancias entre ciudades consecutivas
        for (int i = 0; i < tour.size() - 1; i++) {
            int ciudadA = tour.get(i);
            int ciudadB = tour.get(i + 1);
            distanciaTotal += distancias[ciudadA][ciudadB];
        }

        // Añadir distancia de vuelta al origen
        int ultimaCiudad = tour.get(tour.size() - 1);
        int primeraCiudad = tour.get(0);
        distanciaTotal += distancias[ultimaCiudad][primeraCiudad];
    }

    /**
     * Resetea la hormiga para un nuevo tour
     */
    public void reset() {
        tour.clear();
        Arrays.fill(visitadas, false);
        distanciaTotal = 0.0;
        ciudadActual = -1;
    }

    /**
     * Calcula la cantidad de feromona que deposita esta hormiga en cada arista
     */
    public double calcularFeromonaADepositar() {
        if (distanciaTotal == 0) return 0.0;
        return 100.0 / distanciaTotal; // Q / L(k) donde Q=100 es una constante
    }

    // Getters
    public List<Integer> getTour() {
        return new ArrayList<>(tour); // Copia defensiva
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public boolean haCompletadoTour() {
        return tour.size() == visitadas.length;
    }

    @Override
    public String toString() {
        return String.format("Hormiga[Tour: %d ciudades, Distancia: %.2f]",
                tour.size(), distanciaTotal);
    }

    /**
     * Método para debug: muestra el tour completo
     */
    public void mostrarTour() {
        System.out.println("Tour: " + tour);
        System.out.println("Distancia total: " + distanciaTotal);
    }
}