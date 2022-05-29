package cat.atridas87.minairo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MinairoTable implements MinairoCallable {
    final Map<String, TableFieldType> fields;
    final List<String> fieldCanonicalOrder;

    MinairoTable(List<String> fields, List<TableFieldType> types) {
        this.fieldCanonicalOrder = fields;
        this.fields = new HashMap<>();
        for (int i = 0; i < fields.size(); ++i) {
            this.fields.put(fields.get(i), types.get(i));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("( ");

        String separator = "";
        for (String field : fieldCanonicalOrder) {

            builder.append(separator);
            builder.append(field);
            builder.append(" : ");
            builder.append(fields.get(field));
            separator = ", ";
        }

        builder.append(" )");

        return builder.toString();
    }

    public TableFieldType getFieldType(String field) {
        return fields.get(field);
    }

    // BEGIN MinairoCallable
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        List<List<Object>> instanceFields = new Vector<>();

        for(int i = 0; i < fieldCanonicalOrder.size(); ++i) {
            instanceFields.add(new Vector<Object>());
        }
        return new MinairoTableInstance(this, instanceFields);
    }

    @Override
    public int arity() {
        return 0;
    }
    // END MinairoCallable
}