# Laboratorio 2: Algoritmo de Colonia de Hormigas para el Problema del Viajante de Comercio
Un algoritmo de fuerza bruta para resolver el problema del viajante de comercio

# Descripcion del laboratorio
El problema del viajante de Comercio (o TSP, por sus siglas en inglés, Traveling Salesperson Problem).
en el que se busca la ruta más corta posible para un vendedor que debe visitar un conjunto de ciudades y regresar a la ciudad de origen, pasando por cada ciudad una sola vez.
Dado el siguiente contexto y archivo a280.txt . . . se buscara la implementacion de un algoritmo de colonia de hormigas para resolver el problema del viajante de comercio.
En donde se parte de un punto inicial y se busca la mejor ruta posible para visitar todas las ciudades y regresar al punto de inicio. 
<br>H<br>
    / \ <br>
    A---C
y asi sucesivamente.
# Características
- Implementación del algoritmo de colonia de hormigas para resolver el problema del viajante de comercio.
- Visualización de la ruta óptima encontrada por el algoritmo.
- Análisis del rendimiento del algoritmo en términos de tiempo de ejecución y calidad de la solución (distancia total recorrida).

# Estructura del Proyecto
- `src/`: Contiene el código fuente del proyecto.
  - Ciudad.java: Clase que representa una ciudad con sus coordenadas.
  - CreadorCiudades.java: Clase para manejar las ciudades, usando una matriz con el id de la ciudad, otra matriz para las coordenadas y otra para las distancias y un singleton para manejar la clase en todo el proyecto.
  - Hormiga.java: Clase que representa una hormiga en el algoritmo en base a la formula de probabilidad. probabilidad de ir de i a j = (feromona(i,j)^alpha) * (heurística(i,j)^beta) / sum((feromona(i,k)^alpha) * (heurística(i,k)^beta))
  - ManejoDatos.java: Clase para manejar la lectura y serialización de datos (cache)
  - Main.java: Clase principal que ejecuta el algoritmo de colonia de hormigas.

# Instrucciones de Compilación y Ejecución
1. Asegúrate de tener Java instalado en tu sistema.
2. cargar en un IDE como Eclipse o IntelliJ IDEA incluso visual studio code.
3. Compila el proyecto.
4. Cambia parametros (rho, alpha, beta, numero de hormigas, numero de iteraciones) en la clase Main.java

# Algoritmos Implementados
- Algoritmo de Colonia de Hormigas (Ant Colony Optimization, ACO) para el problema del viajante de comercio.
- Algoritmo de Fuerza Bruta para comparación de resultados.

# Licencia
Este proyecto es de uso académico y educativo.

