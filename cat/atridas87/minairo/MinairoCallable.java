package cat.atridas87.minairo;

import java.util.List;

interface MinairoCallable {
    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}