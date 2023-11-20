package com.redfrog.javaParserSub.msgSchema;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CodeConversionInputMsg {
  public final String codeInput;
  @Deprecated // @not_needed
  public final String pglang;
}
