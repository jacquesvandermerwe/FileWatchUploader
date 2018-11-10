package com.box.bc.FileWatchUploader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

/**
 * Hello world!
 *
 */
public class App {
	static Path inputPath = Paths.get("c:\\test\\input");
	static Path outputPath = Paths.get("c:\\test\\output");

	public static void main(String[] args) {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();

			inputPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

			WatchKey key;
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					Path dir = (Path)key.watchable();
					Path filePath = dir.resolve((Path) event.context());
					
					
					Path fullPath = dir.resolve((Path) event.context());
					
					System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + "." + " "
							+ fullPath);
					File file = new File(filePath.toAbsolutePath().toString());
					moveToDirectory(file);

				}
				key.reset();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void moveToDirectory(File source) {
		if (source.exists() && source.isDirectory()) {
			throw new RuntimeException(String.format("Target '%s' is  a directory", source));
		}
		while (!source.exists() && source.renameTo(source)) {
			try {
				Thread.sleep(500);
				System.out.println("Sleeping");
				System.out.println("Source Exist:" + source.exists() + " " + source.getAbsolutePath());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Copy
		try {
			FileUtils.moveToDirectory(source, new File(outputPath.toAbsolutePath().toString()), true);
		} catch (FileNotFoundException e)
		{
			moveToDirectory(source);
		} catch (FileExistsException e)
		{

		} catch (IOException e) {
			throw new RuntimeException(String.format("Could not move test file '%s' to directory '%s'", source,
					new File(outputPath.toAbsolutePath().toString())), e);
		} 

	}
}
