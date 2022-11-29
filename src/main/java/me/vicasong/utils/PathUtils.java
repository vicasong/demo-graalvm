package me.vicasong.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Path tool
 *
 * @author vicasong
 * @since 2022-08-09 15:14
 */
public class PathUtils {

	/** log recorder */
	private static final Logger log = LoggerFactory.getLogger(PathUtils.class);


	/** 获取指定文件path下所有子文件path（排除目录） */
	public static List<Path> getAllSubFile(Path path) {
		File file = path.toFile();
		if (file.exists()) {
			if (file.isFile()) {
				return Collections.singletonList(path);
			}
			List<Path> dirs = new ArrayList<>();
			List<Path> list;
			try (Stream<Path> ls = Files.list(path)) {
				list = ls.filter(p -> !p.toFile().isHidden())
						.filter(p -> !p.getFileName().toString().startsWith("."))
						.peek(p -> {
							if (p.toFile().isDirectory()) {
								dirs.add(p);
							}
						})
						.filter(p -> p.toFile().isFile())
						.collect(Collectors.toList());
			}
			catch (IOException e) {
				log.error("error list files at: " + path, e);
				throw new RuntimeException(e.getMessage());
			}
			if (!dirs.isEmpty()) {
				for (Path dir : dirs) {
					list.addAll(getAllSubFile(dir));
				}
			}
			return list;
		}
		return Collections.emptyList();
	}
}
