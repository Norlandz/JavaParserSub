package com.redfrog.javaParserSub.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePathUtil {

  // https://stackoverflow.com/questions/4746671/how-to-check-if-a-given-path-is-possible-child-of-another-path
  public static boolean isAncestorOf(final Path parent, final Path child) {
    final Path absoluteParent = parent.toAbsolutePath().normalize();
    final Path absoluteChild = child.toAbsolutePath().normalize();
  
    if (absoluteParent.getNameCount() >= absoluteChild.getNameCount()) { return false; }
  
    final Path immediateParent = absoluteChild.getParent();
    if (immediateParent == null) { return false; }
  
    return FilePathUtil.isSameFileAs(absoluteParent, immediateParent) || isAncestorOf(absoluteParent, immediateParent);
  }

  public static boolean isSameFileAs(final Path path, final Path path2) {
    try {
      return Files.isSameFile(path, path2);
    } catch (final IOException ioe) {
      return path.toAbsolutePath().normalize().equals(path2.toAbsolutePath().normalize());
    }
  }

}
