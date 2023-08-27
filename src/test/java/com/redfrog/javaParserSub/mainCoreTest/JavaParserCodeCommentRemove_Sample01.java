// first line, some comment
package com.redfrog.javaParserSub.mainCoreTest;

// some comment

/**
java doc
more
*/
public class JavaParserCodeCommentRemove_Sample01 {

  public static String aa = "AAA";
  public static String bb = "AAA // fake comment";
  public static String cc = //
      """
          AAA
          // fake comment
          """;

  /*
  block comment
  */
  public static String demo(/* block comment*/ String in) { // inline comment
    /*
    block comment
    "fake string"
    // nest comment
    block comment
    /* another fake nest
    block comment
    */
    return "TTT"; // inline comment
  }

  // comment /* nest comment */ AAA

}
