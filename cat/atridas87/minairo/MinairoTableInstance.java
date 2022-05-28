package cat.atridas87.minairo;

import java.util.Map;

public class MinairoTableInstance {
    private final MinairoTable table;
    private final Map<String, Object> fields;
    
    MinairoTableInstance(MinairoTable table, Map<String, Object> fields)
    {
        this.table = table;
        this.fields = fields;
    }
}
