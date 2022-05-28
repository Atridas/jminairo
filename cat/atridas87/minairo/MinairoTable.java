package cat.atridas87.minairo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MinairoTable implements MinairoCallable {
    final String name;
    private final Map<String, TableFieldType> fields;
    private final List<String> fieldCanonicalOrder;

    MinairoTable(String name, List<String> fields, List<TableFieldType> types) {
        this.name = name;
        this.fieldCanonicalOrder = fields;
        this.fields = new HashMap<>();
        for (int i = 0; i < fields.size(); ++i) {
            this.fields.put(fields.get(i), types.get(i));
        }
    }

    @Override
    public String toString() {
        return "<table " + name + ">";
    }

    public TableFieldType getFieldType(String field) {
        return fields.get(field);
    }

    // BEGIN MinairoCallable
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Map<String, Object> instanceFields = new HashMap<>();
        for (Map.Entry<String, TableFieldType> field : fields.entrySet()) {
            switch (field.getValue()) {
                case BOOLEAN:
                    instanceFields.put(field.getKey(), new Vector<Boolean>());
                    break;
                case NUMBER:
                    instanceFields.put(field.getKey(), new Vector<Double>());
                    break;
                case STRING:
                    instanceFields.put(field.getKey(), new Vector<String>());
                    break;
            }
        }
        return new MinairoTableInstance(this, instanceFields);
    }

    @Override
    public int arity() {
        return 0;
    }
    // END MinairoCallable
}