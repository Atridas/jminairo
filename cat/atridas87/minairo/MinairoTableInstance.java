package cat.atridas87.minairo;

import java.util.List;

public class MinairoTableInstance {
    final MinairoTable table;
    final List<List<Object>> fields;

    MinairoTableInstance(MinairoTable table, List<List<Object>> fields) {
        this.table = table;
        this.fields = fields;
    }

    Object get(Token name) {
        if (name.lexeme.equals("insert")) {
            return new InsertIntoTable(name, this);
        }

        throw new RuntimeError(name,
                "Undefined method '" + name.lexeme + "'.");
    }

    int getCount() {
        
        if (fields.size() == 0) {
            return 0;
        } else {
            return fields.get(0).size();
        }
    }

    MinairoTableTupleReferenceInterface getTupleReference(final int idx, final Token fieldName) {
        for(int i = 0; i < table.fieldCanonicalOrder.size(); ++i) {
            if(table.fieldCanonicalOrder.get(i).equals(fieldName.lexeme))
            {
                final List<Object> fieldArray = fields.get(i);
                final TableFieldType fieldType = table.fields.get(table.fieldCanonicalOrder.get(i));
                return new MinairoTableTupleReferenceInterface() {

                    @Override
                    public Object get() {
                        return fieldArray.get(idx);
                    }

                    @Override
                    public void set(Token var, Object value) {
                        switch (fieldType) {
                            case BOOLEAN:
                                if (!(value instanceof Boolean))
                                    throw new RuntimeError(var, "Field '" + fieldName.lexeme + "' must be a boolean.");
                                break;
                            case NUMBER:
                                if (!(value instanceof Double))
                                    throw new RuntimeError(var, "Field '" + fieldName.lexeme + "' must be a number.");
                                break;
                            case STRING:
                                if (!(value instanceof String))
                                    throw new RuntimeError(var, "Field '" + fieldName.lexeme + "' must be a string.");
                                break;
                        }
                        fieldArray.set(idx, value);
                    }
                    
                };
            }
        }

        throw new RuntimeError(fieldName, "Table doesn't contain field '" + fieldName.lexeme + "'");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("( ");

        String separator = "";
        for (String field : table.fieldCanonicalOrder) {

            builder.append(separator);
            builder.append(field);
            builder.append(" : ");
            builder.append(table.fields.get(field));
            separator = ", ";
        }

        builder.append(" ) -> { ");

        if (fields.size() > 0) {
            separator = "( ";
            for (int t = 0; t < fields.get(0).size(); ++t) {
                builder.append(separator);

                separator = "";
                for (int i = 0; i < table.fieldCanonicalOrder.size(); ++i) {

                    builder.append(separator);
                    builder.append(fields.get(i).get(t));
                    separator = ", ";
                }

                builder.append(" )");
                separator = ", ( ";
            }
        }

        builder.append(" }");

        return builder.toString();
    }
}

class InsertIntoTable implements MinairoCallable {
    private final Token callToken;
    private final MinairoTableInstance tableInstance;

    InsertIntoTable(Token callToken, MinairoTableInstance tableInstance) {
        this.callToken = callToken;
        this.tableInstance = tableInstance;
    }

    // BEGIN MinairoCallable
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        for (int i = 0; i < tableInstance.table.fieldCanonicalOrder.size(); ++i) {
            final String field = tableInstance.table.fieldCanonicalOrder.get(i);
            final TableFieldType fieldType = tableInstance.table.fields.get(field);
            final Object argument = arguments.get(i);

            switch (fieldType) {
                case BOOLEAN:
                    if (!(argument instanceof Boolean))
                        throw new RuntimeError(callToken, "Field '" + field + "' must be a boolean.");
                    break;
                case NUMBER:
                    if (!(argument instanceof Double))
                        throw new RuntimeError(callToken, "Field '" + field + "' must be a number.");
                    break;
                case STRING:
                    if (!(argument instanceof String))
                        throw new RuntimeError(callToken, "Field '" + field + "' must be a string.");
                    break;
            }
        }

        for (int i = 0; i < tableInstance.table.fieldCanonicalOrder.size(); ++i) {
            Object argument = arguments.get(i);
            List<Object> fieldArray = tableInstance.fields.get(i);

            fieldArray.add(argument);
        }

        return tableInstance;
    }

    @Override
    public int arity() {
        return tableInstance.table.fieldCanonicalOrder.size();
    }
    // END MinairoCallable

}
