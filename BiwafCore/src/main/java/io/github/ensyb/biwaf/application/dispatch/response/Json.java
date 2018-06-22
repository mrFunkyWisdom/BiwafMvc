package io.github.ensyb.biwaf.application.dispatch.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.StringJoiner;

public class Json implements Response {

    private static final String DEFAULT_JSON_ENCODING = "utf-8";
    private String jsonContent;
    private String jsonEncoding;

    public Json(Object object) {
        this(object, DEFAULT_JSON_ENCODING);
    }

    public Json(Object[] objects) {
        this(objects, DEFAULT_JSON_ENCODING);
    }

    public Json(Collection<?> objects) {
        this(objects, DEFAULT_JSON_ENCODING);
    }

    public Json(Object[] objects, String jsonEncoding) {
        this.jsonEncoding = jsonEncoding;
        StringJoiner joiner = new StringJoiner(",");
        for (Object object : objects) {
            try {
                joiner.add(this.marshalToJson(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.jsonContent = "[" + joiner.toString() + "]";
    }

    public Json(Collection<?> objects, String jsonEncoding) {
        this(objects.toArray(), jsonEncoding);
    }

    public Json(Object object, String jsonEncoding) {
        this.jsonEncoding = jsonEncoding;
        try {
            this.jsonContent = this.marshalToJson(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(BiwafContext context) {
        try {
            HttpServletResponse response = context.currentResponse();
            response.setHeader("Content-Type", "application/json; charset=" + this.jsonEncoding);
            PrintWriter writer = response.getWriter();
            writer.write(this.jsonContent);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String marshalToJson(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        StringJoiner joiner = new StringJoiner(",");
        for (Field field : fields) {
            field.setAccessible(true);
            String jsonValue = isQuotedType(field) ? "\"" + field.get(object).toString() + "\"" : field.get(object).toString();
            String jsonKeyValuePair = String.format("\"%s\":%s", field.getName(), jsonValue);
            joiner.add(jsonKeyValuePair);
        }
        return "{" + joiner.toString() + "}";
    }

    private boolean isQuotedType(Field field) {
        Class<?> type = field.getType();
        String typeName = type.getName();
        return !typeName.equals("java.lang.Integer")
                && !typeName.equals("int")
                && !typeName.equals("java.lang.Boolean")
                && !typeName.equals("boolean")
                && !typeName.equals("java.lang.Float")
                && !typeName.equals("float")
                && !typeName.equals("java.lang.Double")
                && !typeName.equals("double");
    }

}
