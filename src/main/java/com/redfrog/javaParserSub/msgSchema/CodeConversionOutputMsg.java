package com.redfrog.javaParserSub.msgSchema;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CodeConversionOutputMsg {
  public final String codeOutput;
  public final String errorName;
  public final String errorMsg;
}
