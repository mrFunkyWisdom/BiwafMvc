package io.github.ensyb.biwaf.application.dispatch.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;

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
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            try {
                jsonBuilder.append(this.marshalToJson(object));
                if (i + 1 < objects.length) {
                    jsonBuilder.append(",");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        jsonBuilder.append("]");
        this.jsonContent = jsonBuilder.toString();
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
            PrintWriter writter = response.getWriter();
            writter.write(this.jsonContent);
            writter.flush();
            writter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String marshalToJson(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (int i = 0; i < fields.length; i++) {
            // Append key
            Field field = fields[i];
            jsonBuilder.append("\"").append(field.getName()).append("\"");
            // Append value
            field.setAccessible(true);
            jsonBuilder.append(":");
            if (isQuotedType(field)) {
                jsonBuilder.append("\"").append(field.get(object).toString()).append("\"");
            } else {
                jsonBuilder.append(field.get(object).toString());
            }
            if (i + 1 < fields.length) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("}");

        return jsonBuilder.toString();
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
