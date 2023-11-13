package com.redfrog.javaParserSub.mainCoreTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.javaparser.ParseException;
import com.redfrog.javaParserSub.service.JavaCommentService;
import com.redfrog.javaParserSub.util.DirEqualityUtil;
import com.redfrog.javaParserSub.util.DirEqualityUtil.DirAreNotEqualException;

public class JavaCommentModifierTest {

  @Nested
  class SimpleCase_T01 {

    private static String getSampleFile() {
      Path pwd = Paths.get("").toAbsolutePath(); // G:\wsp\eclipse_202304\CommentRemoverProj // ProjThis
      String src_test_java = "src/test/java";
      String pkgPath = JavaCommentModifierTest.class.getPackageName().replaceAll("\\.", "/");
      String fileName_sample = "JavaParserCodeCommentRemove_Sample01.java";
      Path pathFile_TestSample = pwd.resolve(src_test_java + "/" + pkgPath + "/" + fileName_sample);
      //      System.out.println(pathFile_Test.toString());
      //      System.out.println(pkgPath);
      String fileCode;
      try {
        fileCode = new String(Files.readAllBytes(pathFile_TestSample));
      } catch (IOException e) {
        throw new Error(e);
      }
      return fileCode;
    }

    @Test
    public void simpleCase__fileCode() {

      String fileCode_ori = getSampleFile();

      String fileCode_CommentRemoved;
      try {
        fileCode_CommentRemoved = JavaCommentService.removeCodeComment_givenFileCode(fileCode_ori);
      } catch (ParseException e) {
        throw new Error(e);
      }
      fileCode_CommentRemoved = fileCode_CommentRemoved.replaceAll("\\r\\n?", "\n");
      //      System.out.println(fileCode_CommentRemoved);

      String expect = "//_________________________\n"
                      + "package com.redfrog.javaParserSub.mainCoreTest;\n"
                      + "\n"
                      + "//_____________\n"
                      + "\n"
                      + "/**\n"
                      + "________\n"
                      + "____\n"
                      + "*/\n"
                      + "public class JavaParserCodeCommentRemove_Sample01 {\n"
                      + "\n"
                      + "  public static String aa = \"AAA\";\n"
                      + "  public static String bb = \"AAA // fake comment\";\n"
                      + "  public static String cc = //\n"
                      + "      \"\"\"\n"
                      + "          AAA\n"
                      + "          // fake comment\n"
                      + "          \"\"\";\n"
                      + "\n"
                      + "  /*\n"
                      + "_______________\n"
                      + "__*/\n"
                      + "  public static String demo(/*______________*/ String in) { //_______________\n"
                      + "    /*\n"
                      + "_________________\n"
                      + "_________________\n"
                      + "___________________\n"
                      + "_________________\n"
                      + "________________________\n"
                      + "_________________\n"
                      + "____*/\n"
                      + "    return \"TTT\"; //_______________\n"
                      + "  }\n"
                      + "\n"
                      + "  //_______________________________\n"
                      + "\n"
                      + "}\n"
                      + "";

      Assertions.assertEquals(expect, fileCode_CommentRemoved);

    }

    //    // cuz the TestSample_Folder will not be committed to Github - this Test will fail 
    //    // please manually examinate the output before using that output as the Test_Expect 
    //    @Test
    //    public void simpleCase__folder() {
    //      String eclipse_workspace = "G:/wsp/eclipse_202304";
    //      String projectName_in = "TestSample-TrafficSystemMock-ToRemoveComment";
    //      String projectName_out = projectName_in + "-CommentRemoved-out";
    //      String pathFolderAbsStr_ToRemoveComment_in = eclipse_workspace + "/" + projectName_in;
    //      String pathFolderAbsStr_out = eclipse_workspace + "/" + projectName_out;
    //
    //      JavaCommentService.removeCodeComment_givenDirOfProject(pathFolderAbsStr_ToRemoveComment_in, pathFolderAbsStr_out);
    //
    //      String projectName_in_ori = projectName_in + " - ori";
    //      String pathFolderAbsStr_ToRemoveComment_in_ori = eclipse_workspace + "/" + projectName_in_ori;
    //      try {
    //        DirEqualityUtil.verifySubtreeEquality(Paths.get(pathFolderAbsStr_ToRemoveComment_in), Paths.get(pathFolderAbsStr_ToRemoveComment_in_ori));
    //      } catch (IOException e) {
    //        throw new Error(e);
    //      } catch (DirAreNotEqualException e) {
    //        Assertions.fail("the original In_Folder should never be changed during the process :: \n" + e);
    //      }
    //
    //      String projectName_out_expect = projectName_out + " - expectToBe";
    //      String pathFolderAbsStr_out_expect = eclipse_workspace + "/" + projectName_out_expect;
    //      try {
    //        DirEqualityUtil.verifySubtreeEquality(Paths.get(pathFolderAbsStr_out), Paths.get(pathFolderAbsStr_out_expect));
    //      } catch (IOException e) {
    //        throw new Error(e);
    //      } catch (DirAreNotEqualException e) {
    //        Assertions.fail("(Out_Folder) Result of Remove Comment - Fail to meet expectation. :: \n" + e);
    //      }
    //
    //    }

  }
}
