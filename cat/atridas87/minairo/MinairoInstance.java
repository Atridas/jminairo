package cat.atridas87.minairo;

import java.util.HashMap;
import java.util.Map;

public class MinairoInstance {
    private final MinairoClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    MinairoInstance(MinairoClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return "<instance of " + klass.name + ">";
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        MinairoFunction method = klass.findMethod(name.lexeme);
        if (method != null)
            return method.bind(this);

        throw new RuntimeError(name,
                "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }
}
