import java.lang.reflect.Field;

public class Main {

    private static boolean isSideDefined(Drawable shape) {
        try {
            Class<?> thisClass = Class.forName(shape.getClass().getSimpleName());
            Field[] fields = thisClass.getDeclaredFields();
            for (Field field : fields) {
                System.out.println(field.getName());
                if (field.getName().equals("side"))
                    return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(isSideDefined(new RandomShape2()));
    }
}
