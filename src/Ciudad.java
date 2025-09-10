import java.io.Serializable;
public class Ciudad implements  Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    double x;
    double y;
    public Ciudad(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double distancia(Ciudad otra) {
        return Math.sqrt(Math.pow(this.x - otra.x, 2) + Math.pow(this.y - otra.y, 2));
    }
    public int getId() {
        return id;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}
