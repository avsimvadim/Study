package reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionFormatter {

    public String format(Object obj) {
        Class struc = obj.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%s\n", "type", struc.getName()));
        Field[] fields = struc.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.isAnnotationPresent(Save.class)) {
                    Save save = field.getDeclaredAnnotation(Save.class);
                    String fieldName = field.getName();
                    String firstUpperCaseLetter = fieldName.substring(0, 1).toUpperCase();
                    Method getFieldValue = struc.getMethod("get" + firstUpperCaseLetter + fieldName.substring(1));
                    sb.append(String.format("%s:%s\n", save.name(), getFieldValue.invoke(obj)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public Object parse(String src){
        String[] lines = src.split("\n");
        String type = lines[0].split(":")[1];

        Map<String,String> keyValuesMap = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String[] keyValue = lines[i].split(":");
            keyValuesMap.put(keyValue[0], keyValue[1]);
        }

        try{
            Class cl = Class.forName(type);
            Object instance = cl.newInstance();

            for(String key : keyValuesMap.keySet()){
                Field field = cl.getDeclaredField(key);

                Class<?> fieldType = field.getType();
                String firstUpperCaseLetter = key.substring(0, 1).toUpperCase();
                Method setMethod = cl.getMethod("set" + firstUpperCaseLetter + key.substring(1), fieldType);
                if (int.class == fieldType){
                    setMethod.invoke(instance, Integer.parseInt(keyValuesMap.get(key)));
                } else if (double.class == fieldType){
                    setMethod.invoke(instance, Double.parseDouble(keyValuesMap.get(key)));
                } else if (String.class == fieldType){
                    setMethod.invoke(instance, keyValuesMap.get(key));
                }
            }
            return instance;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
