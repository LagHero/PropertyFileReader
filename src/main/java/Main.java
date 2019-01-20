public class Main {
    public static void main(String[] args) {

        Object value1 = PropertyReader.INSTANCE.getExamplePropertyValue1();

        PropertyReader propReader = PropertyReader.INSTANCE;
        Object value2 = propReader.getExamplePropertyValue2();

        System.out.println("Value1 = " + value1);
        System.out.println("Value2 = " + value2);

        System.out.println(" ------- ");

        System.out.println(PropertyReader.INSTANCE.getPropertyValues());

    }
}
