import java.util.ArrayList;
import java.util.Comparator;

public class Board<T extends Drawable> {

    protected ArrayList<T> shapes = new ArrayList<>();

    public void addNewShape(T shape) {
        if (!shapes.contains(shape))
            shapes.add(shape);
    }


    public double allPerimeter() {
        double allPerimeter = 0;
        for (T shape : shapes)
            allPerimeter += shape.getPerimeter();
        return allPerimeter;
    }

    public double allSurface() {
        double allSurface = 0;
        for (T shape : shapes)
            allSurface += shape.getSurface();
        return allSurface;
    }

    public double allSide() {
        double allSides = 0;
        for (T shape : shapes) {
            try {
                allSides += shape.getSide();
            } catch (SideNotDefinedException e) {
                e.printStackTrace();
            }

        }
        return allSides;
    }

    public double allSideException() throws SideNotDefinedException {
        double allSides = 0;
        for (T shape : shapes)
            allSides += shape.getSide();
        return allSides;
    }

    public T minimumSurface() {
        int index = 0;
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getSurface() < shapes.get(index).getSurface())
                index = i;
        }

        return shapes.get(index);
    }

    public ArrayList<T> sortedList(double x) {
        ArrayList<T> bigEnoughShapes = new ArrayList<>();
        for (T shape : shapes)
            if (shape.getPerimeter() > x)
                bigEnoughShapes.add(shape);
        bigEnoughShapes.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return Double.compare(o1.getSurface(), o2.getSurface());
            }
        });
        return bigEnoughShapes;
    }

}
