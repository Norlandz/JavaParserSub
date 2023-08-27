package com.redfrog.javaParserSub.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.github.javaparser.GeneratedJavaParserConstants;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.utils.SourceRoot;
import com.redfrog.javaParserSub.util.FileContentUtil;
import com.redfrog.javaParserSub.util.FilePathUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JavaCommentService {

  // ;learn; // Getting Started with JavaParser: What you can and what you shouldn’t do. | by Ridhi Jain | Towards Cleaner MicroServices | Medium
  // ;learn; // https://medium.com/getting-started-with-javaparser/getting-started-with-javaparser-what-you-can-and-what-you-shouldnt-do-21b64eb6305c
  // ;learn; // 
  // ;learn; // Getting started with JavaParser: a tutorial on processing Java Code
  // ;learn; // https://tomassetti.me/getting-started-with-javaparser-analyzing-java-code-programmatically/
  // ;learn; // 
  // ;learn; // JavaParser to generate, analyze and modify Java code - Java Code Geeks - 2023
  // ;learn; // https://www.javacodegeeks.com/2017/12/javaparser-generate-analyze-modify-java-code.html
  // ;learn; //
  // ;learn; // JavaParser
  // ;learn; // https://tomassetti.me/wp-content/uploads/2017/12/JavaParser-JUG-Milano.pdf
  // ;learn; 
  // ;learn; private static void demo__remove_Comment(Path pathAbs_ProjectToRemoveComment) {
  // ;learn; 
  // ;learn;   SourceRoot sourceRoot = new SourceRoot(pathAbs_ProjectToRemoveComment);
  // ;learn;   //    sourceRoot.setParserConfiguration(parserConfiguration);
  // ;learn;   List<ParseResult<CompilationUnit>> parseResults;
  // ;learn;   try {
  // ;learn;     parseResults = sourceRoot.tryToParse("");
  // ;learn;   } catch (IOException e) {
  // ;learn;     throw new Error(e);
  // ;learn;   }
  // ;learn; 
  // ;learn;   // Now get all compilation unitsList 
  // ;learn;   // ConcurrentModificationException?
  // ;learn;   for (ParseResult<CompilationUnit> parseResult : parseResults) {
  // ;learn;     if (parseResult.isSuccessful()) {
  // ;learn;       CompilationUnit cu_curr = parseResult.getResult().get();
  // ;learn;       System.out.println("########");
  // ;learn;       System.out.println(cu_curr.getPrimaryTypeName().get());
  // ;learn;       System.out.println(cu_curr.getStorage().get().getPath().toAbsolutePath().toString());
  // ;learn; 
  // ;learn;       List<Comment> arr_comment_currCu = cu_curr.getAllComments(); //? dk design
  // ;learn;       for (Comment comment_curr_currCu : arr_comment_currCu) {
  // ;learn;         System.out.println(comment_curr_currCu);
  // ;learn;         //          // @note: if use  comment.remove() -- the code position will change & the indentation will change when cu.toString()
  // ;learn;         //          comment_curr_currCu.remove();
  // ;learn;         comment_curr_currCu.setContent(" @comment_removed");
  // ;learn;       }
  // ;learn; 
  // ;learn;       System.out.println(">>>>>");
  // ;learn;       System.out.println(cu_curr);
  // ;learn;     }
  // ;learn;     else {
  // ;learn;       System.err.println("Java Parse failed :: " + parseResult);
  // ;learn;     }
  // ;learn;   }
  // ;learn; 
  // ;learn; }

  private static String removeCodeComment_givenParseResult(ParseResult<CompilationUnit> parseResult, String fileCode_ori) throws ParseException {
    if (parseResult.isSuccessful()) {
      CompilationUnit cu_curr = parseResult.getResult().get();

      ArrayList<Range> arr_range = new ArrayList<>();
      List<Comment> arr_comment_currCu = cu_curr.getAllComments();
      for (Comment comment_curr_currCu : arr_comment_currCu) {
        //        System.out.println(comment_curr_currCu);
        Range range = comment_curr_currCu.getRange().get(); // .orElse(null);
        arr_range.add(range);
      }

      StringBuffer sb = new StringBuffer(fileCode_ori);

      for (Range range_curr : arr_range) {
        // @messy int long????????
        int indConcrete_begin = (int) FileContentUtil.get_Index_given_RowCol(fileCode_ori, range_curr.begin.line, range_curr.begin.column);
        int indConcrete_end = (int) FileContentUtil.get_Index_given_RowCol(fileCode_ori, range_curr.end.line, range_curr.end.column);
        int indConcrete_end_plusOne = indConcrete_end + 1; // @;

        //            sb.replace(indConcrete_begin, indConcrete_end_plusOne, "//" + "_".repeat(indConcrete_end_plusOne - indConcrete_begin - 2));

        String comment_Whole = sb.substring(indConcrete_begin, indConcrete_end_plusOne);
        //            System.out.println(sb.toString());
        //            System.out.println(comment_Whole);

        final String underscore = "_";
        Matcher matcher = Pattern.compile("(?s)\\A(?<start>\\/\\/|\\/\\*\\*?)(?<main>.*?)(?<end>\\*\\/)?\\z").matcher(comment_Whole);
        matcher.find();
        String comment_SteleStart = matcher.group("start");
        String comment_Content = matcher.group("main");
        String comment_SteleEnd = matcher.group("end");
        if (comment_SteleStart == null || comment_Content == null) { throw new Error("Cant be null, must match."); }
        if (comment_SteleEnd == null) {
          comment_SteleEnd = "";
        }
        else if (comment_SteleEnd != null && comment_SteleStart.equals("//")) { comment_SteleEnd = underscore.repeat(2); } // just regex match, dont wanna do complex
        if (matcher.find() != false) { throw new Error("Matched twice, should only once."); }

        String comment_Whole_replaced = comment_SteleStart + comment_Content.replaceAll(".", underscore) + comment_SteleEnd;
        if (comment_Whole_replaced.length() != indConcrete_end_plusOne - indConcrete_begin) { throw new Error("Length not match."); }
        sb.replace(indConcrete_begin, indConcrete_end_plusOne, comment_Whole_replaced);
      }
      final String fileCode_CommentRemoved = sb.toString();

      return fileCode_CommentRemoved;
    }
    else {
      //      log.error("Java Parse failed :: " + parseResult); // must throw .. only in project may suppress .. 
      //      return null;
      // dk proper way to throw from that result
      throw new ParseException("Java Parse failed :: " + parseResult);
    }
  }

  public static String removeCodeComment_givenFileCode(String fileCode_ori) throws ParseException {
    JavaParser javaParser = new JavaParser();
    ParserConfiguration parserConfiguration = javaParser.getParserConfiguration();
    parserConfiguration.setLanguageLevel(LanguageLevel.JAVA_17);

    ParseResult<CompilationUnit> parseResult = javaParser.parse(fileCode_ori);
    return removeCodeComment_givenParseResult(parseResult, fileCode_ori);
  }

  private static void removeCodeComment_givenDirOfProject(final Path pathFolderAbs_ToRemoveComment_in, final Path pathFolderAbs_out) {
    // #>>
    if (!Files.exists(pathFolderAbs_ToRemoveComment_in)) { throw new Error("Not Exist :: " + pathFolderAbs_ToRemoveComment_in); }
    if (!Files.exists(pathFolderAbs_out)) {
      // throw new Error("Not Exist :: " + pathAbs_out); 
      try {
        Files.createDirectories(pathFolderAbs_out);
      } catch (IOException e) {
        throw new Error(e);
      }
    }
    if (!Files.isDirectory(pathFolderAbs_ToRemoveComment_in)) { throw new Error("Not a folder :: " + pathFolderAbs_ToRemoveComment_in); }
    if (!Files.isDirectory(pathFolderAbs_out)) { throw new Error("Not a folder :: " + pathFolderAbs_out); }
    if (FilePathUtil.isSameFileAs(pathFolderAbs_ToRemoveComment_in, pathFolderAbs_out)) { throw new Error("In_Folder & Out_Folder are the same :: " + pathFolderAbs_ToRemoveComment_in + " :: " + pathFolderAbs_out); }
    if (FilePathUtil.isAncestorOf(pathFolderAbs_ToRemoveComment_in, pathFolderAbs_out)) { throw new Error("Out_Folder cannot be inisde In_Folder; vice versa. :: " + pathFolderAbs_ToRemoveComment_in + " :: " + pathFolderAbs_out); }
    if (FilePathUtil.isAncestorOf(pathFolderAbs_out, pathFolderAbs_ToRemoveComment_in)) { throw new Error("Out_Folder cannot be inisde In_Folder; vice versa. :: " + pathFolderAbs_ToRemoveComment_in + " :: " + pathFolderAbs_out); }

    // #>>
    SourceRoot sourceRoot = new SourceRoot(pathFolderAbs_ToRemoveComment_in);
    ParserConfiguration parserConfiguration = new ParserConfiguration();
    //    boolean det_LexicalPreservationEnabled = true;
    //    parserConfiguration.setLexicalPreservationEnabled(det_LexicalPreservationEnabled);
    parserConfiguration.setLanguageLevel(LanguageLevel.JAVA_17); // https://github.com/javaparser/javaparser/issues/3767
    sourceRoot.setParserConfiguration(parserConfiguration);
    List<ParseResult<CompilationUnit>> parseResults;
    try {
      parseResults = sourceRoot.tryToParse("");
    } catch (IOException e) {
      throw new Error(e);
    }

    // ConcurrentModificationException?
    for (ParseResult<CompilationUnit> parseResult : parseResults) {
      CompilationUnit cu_curr = parseResult.getResult().get();
      final Path pathAbs_Cu = cu_curr.getStorage().get().getPath().toAbsolutePath();
      String fileCode_ori;
      try {
        fileCode_ori = new String(Files.readAllBytes(pathAbs_Cu));
      } catch (IOException e) {
        throw new Error(e);
      }

      String fileCode_CommentRemoved = null;
      try {
        fileCode_CommentRemoved = removeCodeComment_givenParseResult(parseResult, fileCode_ori);

        try {
          final Path pathRel_Cu_relToIn = pathFolderAbs_ToRemoveComment_in.relativize(pathAbs_Cu);
          final Path pathAbs_Cu_out = pathFolderAbs_out.resolve(pathRel_Cu_relToIn).toAbsolutePath();
          System.out.println(pathAbs_Cu_out);
          if (!FilePathUtil.isAncestorOf(pathFolderAbs_out, pathAbs_Cu_out)) { throw new Error("The output file is not under the output folder -- which could overwrite existing file. :: " + pathAbs_Cu_out + " :: " + pathFolderAbs_out); }

          Files.createDirectories(pathAbs_Cu_out.getParent());
          Files.write(pathAbs_Cu_out, fileCode_CommentRemoved.getBytes(StandardCharsets.UTF_8)); // StandardOpenOption.
        } catch (IOException e) {
          throw new Error(e);
        }

      } catch (ParseException e) {
        // @messy , cuz before has the fail detection .. now its put inside the func ... (almost have a pb with null return 
        log.error("Java Parse failed :: " + pathAbs_Cu + " :: " + parseResult);
      }
    }
  }

  public static void removeCodeComment_givenDirOfProject(final String pathFolderAbsStr_ToRemoveComment_in, final String pathFolderAbsStr_out) {
    removeCodeComment_givenDirOfProject(Paths.get(pathFolderAbsStr_ToRemoveComment_in).toAbsolutePath().normalize(), Paths.get(pathFolderAbsStr_out).toAbsolutePath().normalize());

  }

  // ########

  //  public final static String pathFolderAbsStr_ToRemoveComment_in = "G:/wsp/eclipse_202304" + "/" + "TrafficSystemMock" + "/src";
  //  public final static String pathFolderAbsStr_out = "G:/wsp/eclipse_202304" + "/" + "TrafficSystemMock-CommentRemoved-out" + "/src";
  //
  //  public static void main(String[] args) {
  //    System.out.println(">---<");
  //
  //    // ;archived;    Path pwd_CurrProj = Paths.get("").toAbsolutePath(); // G:\wsp\eclipse_202304\CommentRemoverProj
  //    // ;archived;    Path path_EclipseWorkspace = pwd_CurrProj.getParent();
  //    // ;archived;    Path pathAbs_ProjectToRemoveComment_src = path_EclipseWorkspace.resolve(name_ProjectToRemoveComment).resolve(src).toAbsolutePath();
  //    // ;archived;    Path pathAbs_out_src = path_EclipseWorkspace.resolve(name_ProjectToRemoveComment + folderName_out_append).resolve(src).toAbsolutePath();
  //
  //    removeCodeComment_givenDirOfProject(pathFolderAbsStr_ToRemoveComment_in, pathFolderAbsStr_out);
  //
  //  }

}
