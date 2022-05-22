package cat.atridas87.minairo;

class Return extends RuntimeException {
    final Token keyword;
    final Object value;
  
    Return(Token keyword, Object value) {
      super(null, null, false, false);
      this.keyword = keyword;
      this.value = value;
    }
  }
