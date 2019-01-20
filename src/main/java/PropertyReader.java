import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public enum PropertyReader {
    // Singleton instance
    INSTANCE;

    private EnumMap<PropertyKey, Object> propertiesValueMap = new EnumMap<>(PropertyKey.class);

    // Private constructor restricted to this class itself
    private PropertyReader()
    {
        loadPropertyValues();
    }

    /**
     * Used to load all of the values from the properties file.
     */
    public void loadPropertyValues() {
        String propertiesFileName = "config.properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);){
            Properties propertiesFile = new Properties();

            if (inputStream != null){
                propertiesFile.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found!");
            }

            for(PropertyKey propKey : PropertyKey.values()){
                try{
                    String stringValue = propertiesFile.getProperty(propKey.key);
                    if (stringValue == null) {
                        // Property not found in the file
                        if (propKey.defaultValue == null) {
                            // No default value
                            propertiesValueMap.put(propKey, null);
                        } else {
                            // Use the default value
                            propertiesValueMap.put(propKey, propKey.defaultValue);
                        }
                    } else {
                        // Found a value in the file
                        if (String.class.equals(propKey.valueType)) {
                            // This property is of type string, add the value to the map
                            propertiesValueMap.put(propKey, stringValue);
                        } else {
                            // We need to make sure the value in the file is the correct type
                            Object value = PropertyKey.parse(propKey.valueType, stringValue);
                            if (propKey.valueType.isInstance(value)) {
                                // Parsed the string into the correct value type
                                propertiesValueMap.put(propKey, value);
                            } else {
                                // The parsed string is not correct
                                throw new IllegalStateException("Exception while reading property ");
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Exception while reading property: " + propKey.toString(), e);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception while reading property file: " + propertiesFileName, e);
        }
    }

    /**
     * @return All of the loaded property values
     */
    public ImmutableMap<PropertyKey, Object> getPropertyValues(){
        ImmutableMap.Builder<PropertyKey, Object> builder = ImmutableMap.builder();
        for (PropertyKey propKey : propertiesValueMap.keySet()) {
            Object value = propertiesValueMap.get(propKey);
            if (value != null) {
                builder.put(propKey, value);
            }
        }
        return builder.build();
    }

    public Object getPropertyValue(String key){
        PropertyKey propKey = PropertyKey.valueOf(key);
        return getPropertyValues().get(propKey);
    }

    /**
     * @param key -
     * @param value
     */
    public void setPropertyValue(String key, String value) {
        propertiesValueMap.put(PropertyKey.valueOf(key), value);
    }

    // Below is the list of getters for each property

    public Object getExamplePropertyValue1(){
        return PropertyKey.VALUE1.valueType.cast(propertiesValueMap.get(PropertyKey.VALUE1));
    }

    public Object getExamplePropertyValue2(){
        return propertiesValueMap.get(PropertyKey.VALUE2);
    }

    /**
     * This is the list of properties
     */
    private enum PropertyKey {
        VALUE1("example.value1", Integer.class, 1),
        VALUE2("example.value2", Integer.class, 2),
        VALUE3("example.value3", Integer.class, null),
        VALUE4("example.value4", Integer.class, null),
        VALUE5("example.value5", Integer.class, 5),
        VALUE6("example.value6", Boolean.class, true);

        private String key;
        private Class<?> valueType;
        private Object defaultValue;
        // TODO: Add name and description

        private <V extends Object> PropertyKey(String key, Class<V> valueType, V defaultValue) {
            Preconditions.checkNotNull(key);
            this.key = key;

            Preconditions.checkNotNull(valueType);
            this.valueType = valueType;
            if (!valueType.isAssignableFrom(String.class)) {
                // If not a type of string, then parse needs to be able to handle it...
                V test = this.parse(valueType, (String)null);
            }

            this.defaultValue = defaultValue;
        }

        static private <O extends Object> O parse(Class<O> valueType, String value){
            if (value == null){
                return null;
            }
            if (Integer.class.isAssignableFrom(valueType)) {
                // We know valueType extends Integer because its assignable from Integer
                Class<? extends Integer> clazz = valueType.asSubclass(Integer.class);
                // Casting back to object to return
                @SuppressWarnings("unchecked")
                O returnObj = (O) PropertyKey.parseInt(clazz, value);
                return returnObj;
            } else if (Boolean.class.isAssignableFrom(valueType)) {
                // We know valueType extends Integer because its assignable from Integer
                Class<? extends Boolean> clazz = valueType.asSubclass(Boolean.class);
                // Casting back to object to return
                @SuppressWarnings("unchecked")
                O returnObj = (O) PropertyKey.parseBoolean(clazz, value);
                return returnObj;
            }
            return valueType.cast(String.valueOf(value));
        }

        static private <I extends Integer> I parseInt(Class<I> valueType, String value){
            // Parse this string into an int
            int v = I.parseInt(value);
            // Now cast the int into the correct type
            return valueType.cast(v);
        }
        static private <B extends Boolean> B parseBoolean(Class<B> valueType, String value){
            // Parse this string into an int
            Boolean v = B.parseBoolean(value);
            // Now cast the int into the correct type
            return valueType.cast(v);
        }

        @Override
        public String toString() {
            return "PropertyKey{" +
                    "key='" + key + '\'' +
                    ", valueType=" + valueType.getSimpleName() +
                    ", defaultValue=" + String.valueOf(defaultValue) +
                    '}';
        }}
}