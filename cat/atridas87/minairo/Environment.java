package cat.atridas87.minairo;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment _enclosing) {
        enclosing = _enclosing;
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            assignInternal (name, value);
        } else if (enclosing != null) {
            enclosing.assign(name, value);
        } else {
            throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
        }
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).assignInternal(name, value);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return getInternal(name.lexeme);
        } else if (enclosing != null) {
            return enclosing.get(name);
        } else {
            throw new RuntimeError(name,
                    "Undefined variable '" + name.lexeme + "'.");
        }
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).getInternal(name);
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    
    private void assignInternal (Token name, Object value) {
        Object obj = values.get(name.lexeme);
            if (obj instanceof MinairoTableTupleReferenceInterface) {
                ((MinairoTableTupleReferenceInterface) obj).set(name, value);
            } else {
                values.put(name.lexeme, value);
            }
    }

    private Object getInternal(String name) {
        Object obj = values.get(name);
        if (obj instanceof MinairoTableTupleReferenceInterface) {
            return ((MinairoTableTupleReferenceInterface) obj).get();
        } else {
            return obj;
        }
    }

}