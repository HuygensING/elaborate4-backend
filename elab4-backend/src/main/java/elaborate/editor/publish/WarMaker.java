package elaborate.editor.publish;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.knaw.huygens.Log;

import com.google.common.collect.Lists;

public class WarMaker {
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

			Log.debug("Output to Zip : {}", zipFile);
			for (String file : fileList) {
				Log.debug("File Added : {}", file);
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

			Log.debug("Done");
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
