package elaborate.editor.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.knaw.huygens.LoggableObject;

import com.google.common.collect.Lists;

public class WarMaker extends LoggableObject {
  private final String basename;
  private final File sourceDir;
  private final File destinationDir;
  private final List<String> fileList;

  public WarMaker(String basename, File sourceDir, File destinationDir) {
    this.basename = basename;
    this.sourceDir = sourceDir;
    this.destinationDir = destinationDir;
    fileList = Lists.newArrayList();
  }

  public File make() {
    addToFileList(sourceDir);
    byte[] buffer = new byte[1024];
    File zipFile = new File(destinationDir, basename + ".war");
    try {
      FileOutputStream fos = new FileOutputStream(zipFile);
      ZipOutputStream zos = new ZipOutputStream(fos);

      LOG.debug("Output to Zip : {}", zipFile);
      for (String file : fileList) {
        LOG.debug("File Added : {}", file);
        ZipEntry ze = new ZipEntry(file.replaceAll("\\\\", "/"));
        zos.putNextEntry(ze);
        FileInputStream in = new FileInputStream(new File(sourceDir, file));

        int len;
        while ((len = in.read(buffer)) > 0) {
          zos.write(buffer, 0, len);
        }

        in.close();
      }

      zos.closeEntry();
      zos.close();

      LOG.debug("Done");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return zipFile;
  }

  /**
   * Traverse a directory and get all files,
   * and add the file into fileList  
   * @param node file or directory
   */
  public void addToFileList(File node) {
    if (node.isFile()) {
      fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));

    } else if (node.isDirectory()) {
      for (String filename : node.list()) {
        addToFileList(new File(node, filename));
      }
    }

  }

  /**
   * Format the file path for zip
   * @param file file path
   * @return Formatted file path
   */
  private String generateZipEntry(String file) {
    return file.substring(sourceDir.getAbsolutePath().length() + 1, file.length());
  }
}
