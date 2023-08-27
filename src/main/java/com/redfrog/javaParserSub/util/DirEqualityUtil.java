package com.redfrog.javaParserSub.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

// @main: https://stackoverflow.com/questions/14522239/test-two-directory-trees-for-equality
// @alternative: http://www.java2s.com/example/java/java.nio.file/compare-the-contents-of-two-directories-to-determine-if-they-are-equal.html
public class DirEqualityUtil {

  /**
   * Verify that
   * <ol>
   * <li>the two folders have the same file structure</li>
   * <li>that all the files have the same size</li>
   * <li>that all files in subtree {@code one} is present in {@code other} and their contents are the same</li>
   * </ol>
   */
  public static void verifySubtreeEquality(Path one, Path other) throws IOException {
    verifyStructure(one, other);
    verifySize(one, other);
    verifyContents(one, other);
  }

  // dont wanna use runtime exception ... but that anony class 
  public static class DirAreNotEqualException extends RuntimeException {

    public DirAreNotEqualException(String string) { super(string); }

  }

  private static void verifyStructure(Path root1, Path root2) throws IOException {
    verifyPresence(root1, root2);
    verifyPresence(root2, root1);
  }

  private static void verifyPresence(Path fileSource, Path examinee) throws IOException {
    Files.walkFileTree(fileSource, new SimpleFileVisitor<Path>()
      {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          FileVisitResult result = super.visitFile(file, attrs);

          Path filePathFromRoot = fileSource.relativize(file);
          File targetFile = examinee.resolve(filePathFromRoot).toFile();

          if (!targetFile.exists()) {
            throw new DirAreNotEqualException(filePathFromRoot + " was present in " + fileSource + " but not in " + examinee
                                              + System.lineSeparator() + "Expected file: " + targetFile.getAbsolutePath());

          }

          return result;
        }
      });
  }

  private static void verifySize(Path one, Path other) throws IOException {
    Files.walkFileTree(one, new SimpleFileVisitor<Path>()
      {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          FileVisitResult result = super.visitFile(file, attrs);

          Path filePathFromRoot = one.relativize(file);
          Path path1 = one.resolve(filePathFromRoot);
          long size1 = Files.size(path1);
          Path path2 = other.resolve(filePathFromRoot);
          long size2 = Files.size(path2);

          if (size1 != size2) {
            throw new DirAreNotEqualException("Expected equal file sizes but "
                                              + System.lineSeparator() + path1.toAbsolutePath() + " is " + size1 + " bytes, whereas "
                                              + System.lineSeparator() + path2.toAbsolutePath() + " is " + size2 + " bytes");

          }
          return result;
        }
      });
  }

  private static void verifyContents(Path one, Path other) throws IOException {
    Files.walkFileTree(one, new SimpleFileVisitor<Path>()
      {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          FileVisitResult result = super.visitFile(file, attrs);
          Path relativize = one.relativize(file);
          Path fileInOther = other.resolve(relativize);

          byte[] otherBytes = Files.readAllBytes(fileInOther);
          byte[] theseBytes = Files.readAllBytes(file);
          if (!Arrays.equals(otherBytes, theseBytes)) {
            throw new DirAreNotEqualException(file + " contents differ from " + fileInOther);

          }
          return result;
        }
      });
  }
}