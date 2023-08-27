package com.redfrog.javaParserSub.util;

public class FileContentUtil {

  public static long get_Index_given_RowCol(String content, int row, int col) {
    if (row <= 0 || col <= 0) { throw new Error(); }

    // ;performance [regex];    int length = content.length();@¦    // ;performance [regex];    if (length < 50000) {@¦    // ;performance [regex];      //    Pattern pattern = Pattern.compile(String.format("((.*?)\\R){%d}.{%d}", row - 1, col));@¦    // ;performance [regex];      Pattern pattern = Pattern.compile(String.format("(?:(?:.*?)\\R){%d}.{%d}", row - 1, col));@¦    // ;performance [regex];      Matcher matcher = pattern.matcher(content);@¦    // ;performance [regex];      matcher.find();@¦    // ;performance [regex];      try {@¦    // ;performance [regex];        return matcher.end() - 1; // cuz: index = length - 1@¦    // ;performance [regex];      } catch (IllegalStateException e) {@¦    // ;performance [regex];        System.out.println(content);@¦    // ;performance [regex];        System.out.println(pattern);@¦    // ;performance [regex];        throw new Error(e);@¦    // ;performance [regex];      }@¦    // ;performance [regex];    }@¦    // ;performance [regex];    else {

    // @: dont use \r?\n -- this cannot det the length anymore 
    String[] arr_line = content.split("\n");

    int sn_line = 0;
    long ind_M = 0;
    for (String line : arr_line) {
      sn_line++;
      if (sn_line == row) {
        ind_M += col - 1; // cuz: index = length - 1 //aga... 
        //        System.out.println("> Last Line: \n" + line + "\n :: " + ind_M + " :: " + col);
        return ind_M;
      }
      ind_M += line.length() + 1; // need put after the if ... 
    }

    // >> unless there is only 1 line
    //    System.out.println("Only 1 super long line?");
    return col - 1;
    
    // TODO if outofbound / index not exist at such position ... 
  }

}
