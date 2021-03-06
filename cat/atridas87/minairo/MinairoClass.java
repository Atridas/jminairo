package cat.atridas87.minairo;

import java.util.List;
import java.util.Map;

public class MinairoClass implements MinairoCallable {
    final String name;
    private final Map<String, MinairoFunction> methods;
    final MinairoClass superclass;

    MinairoClass(String name, MinairoClass superclass, Map<String, MinairoFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superclass = superclass;
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }

    MinairoFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null;
    }

    // BEGIN MinairoCallable
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        MinairoInstance instance = new MinairoInstance(this);
        MinairoFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public int arity() {
        MinairoFunction initializer = findMethod("init");
        if (initializer == null)
            return 0;
        else
            return initializer.arity();
    }
    // END MinairoCallable
}
