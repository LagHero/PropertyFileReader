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

    private EnumMap<PropertyKey, String> propertiesValueMap = new EnumMap<>(PropertyKey.class);

    // Private constructor restricted to this class itself
    PropertyReader()
    {
        loadPropertyValues();
    }

    /**
     * Used to load all of the values from the properties file.
     */
    public void loadPropertyValues() {
        String propertiesFileName = "config.properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName)){
            Properties propertiesFile = new Properties();

            if (inputStream != null){
                propertiesFile.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found!");
            }

            for(PropertyKey propKey : PropertyKey.values()){
                try{
                    String stringValue = propertiesFile.getProperty(propKey.key, propKey.defaultValue);
                    propertiesValueMap.put(propKey, stringValue);
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

    public void setPropertyValue(String key, String value) {
        propertiesValueMap.put(PropertyKey.valueOf(key), value);
    }

    // Below is the list of getters for each property

    public String getExamplePropertyValue1(){
        return propertiesValueMap.get(PropertyKey.VALUE1);
    }

    public String getExamplePropertyValue2(){
        return propertiesValueMap.get(PropertyKey.VALUE2);
    }

    /**
     * This is the list of properties
     */
    private enum PropertyKey {
        VALUE1("example.value1", "1"),
        VALUE2("example.value2", "2"),
        VALUE3("example.value3", null),
        VALUE4("example.value4", null),
        VALUE5("example.value5", "5"),
        VALUE6("example.value6", "true");

        private String key;
        private String defaultValue;
        // TODO: Add name and description

        <V extends String> PropertyKey(String key, V defaultValue) {
            Preconditions.checkNotNull(key);
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return "PropertyKey{" +
                    "key='" + key + '\'' +
                    ", defaultValue=" + defaultValue +
                    '}';
        }}
}
