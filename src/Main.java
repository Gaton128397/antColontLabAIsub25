
/*
Contexto del problema:
Se intentara hacer el problema del Viajante de Comercio (o TSP, por sus siglas en inglés, Traveling Salesperson Problem). Dado el siguiente contexto y archivo a280.txt . . .
Implementar un algoritmo de colonia de hormigas para resolver el problema del viajante de comercio. Dado un conjunto de n nodos y las distancias de cada par de nodos, encuentre un viaje
de ida y vuelta con la longitud total mínima que visite cada nodo exactamente una vez.
La distancia del nodo i al nodo j es la misma que la del nodo j al nodo i. ¿Cuánto tiempo tarda de media? Si pruebas diferentes parámetros (número de hormigas, velocidad de evaporación), ¿mejora?
*/
public class Main {
    public static void main(String[] args) {
        CreadorCiudades creador = CreadorCiudades.getInstancia();
        double[][] distancias = creador.getDistanciasEntreCiudades();
        int numCiudades = creador.getNumeroCiudades();

        // Crear matriz de feromonas inicial
        double[][] feromonas = new double[numCiudades][numCiudades];
        double feromonaInicial = 1.0;
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j) {
                    feromonas[i][j] = feromonaInicial;
                }
            }
        }

        // Crear y probar una hormiga
        Hormiga hormiga = new Hormiga(numCiudades, 1.0, 2.0);
        hormiga.construirTour(feromonas, distancias);

        System.out.println("Tour construido:");
        hormiga.mostrarTour();
        System.out.println("Feromona a depositar: " + hormiga.calcularFeromonaADepositar());
    }
}