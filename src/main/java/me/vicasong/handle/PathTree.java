package me.vicasong.handle;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;

/**
 * Tree View Tool
 *
 * @author vicasong
 * @since 2022-08-10 10:55
 */
public class PathTree {


	private static final String CORNER_TAIL = "└── ";
	private static final String CORNER_MIDDLE = "├── ";
	private static final String INDENT_MIDDLE = "│   ";
	private static final String INDENT_BLANK = "    ";

	/**
	 * 默认实现 {@code File} 遍历
	 */
	public static final TreeNodeConverter<File> FILE = new TreeNodeConverter<>() {
		@Override
		public String name(File file) {
			return file.getName();
		}

		@Override
		public List<? extends File> children(File file) {
			List<File> files = new ArrayList<>();
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				if (listFiles != null) {
					files.addAll(Arrays.asList(listFiles));
				}
			}
			return files;
		}
	};

	/** 通过文件，列出文件tree */
	@SuppressWarnings("unused")
	public static void listFiles(File dir, Writer writer) throws IOException {
		print(dir, FILE, writer);
	}

	/** 通过根路径和子路径列表构建tree节点 */
	public static PrintableTreeNode buildFromPath(Path rootPath, List<Path> subPaths) {
		Map<Path, DefaultPrintableTreeNode> map = new TreeMap<>(Comparator.comparing(Path::toString));
		DefaultPrintableTreeNode root = new DefaultPrintableTreeNode(rootPath.toString());
		map.put(rootPath, root);
		for (Path subPath : subPaths) {
			Path relativePath = rootPath.relativize(subPath);
			int nameIndex = 0;
			DefaultPrintableTreeNode parent = null;
			do {
				Path name = relativePath.getName(nameIndex);
				Path fullPath = relativePath.subpath(0, nameIndex + 1);
				DefaultPrintableTreeNode node = map.get(fullPath);
				if (node == null) {
					node = new DefaultPrintableTreeNode(name.toString());
					Objects.requireNonNullElse(parent, root).addNode(node);
					map.put(fullPath, node);
				}
				parent = node;
				nameIndex++;
			}
			while (relativePath.getNameCount() > nameIndex);
		}
		return root;
	}

	@SuppressWarnings("unused")
	private static DefaultPrintableTreeNode findNodeByPath(Collection<DefaultPrintableTreeNode> nodes, Path path) {
		if (nodes != null) {
			if (path.getNameCount() > 1) {
				DefaultPrintableTreeNode node = null;
				for (int i = 0; i < path.getNameCount(); i++) {
					Path name = path.getName(i);
					node = findNodeByPath(nodes, name);
					if (node != null) {
						nodes = node.nodes;
					}
				}
				return node;
			}
			String name = path.getFileName().toString();
			for (DefaultPrintableTreeNode node : nodes) {
				if (node.name.equals(name)) {
					return node;
				}
			}
		}
		return null;
	}


	@SuppressWarnings({"SameParameterValue", "unused"})
	private static void process(PrintableTreeNode current,
			String prefix,
			boolean isRoot,
			boolean lastInPeers,
			Writer writer) throws IOException {
		writer.append(prefix);
		if (!isRoot) {
			writer.append((lastInPeers ? CORNER_TAIL : CORNER_MIDDLE));
		}
		writer.append(current.name());
		writer.append('\n');

		List<? extends PrintableTreeNode> children = current.children();
		if (children != null && !children.isEmpty()) {
			String indentation = isRoot ? "" : INDENT_BLANK;
			for (int i = 0; i < children.size() - 1; ++i) {
				process(children.get(i), prefix + (lastInPeers ? indentation : INDENT_MIDDLE), false, false, writer);
			}
			process(children.get(children.size() - 1), prefix + (lastInPeers ? indentation : INDENT_MIDDLE), false, true, writer);
		}
	}

	private static <T> void process(T current,
			TreeNodeConverter<T> treeNodeConverter,
			String prefix,
			boolean isRoot,
			boolean lastInPeers,
			Writer writer) throws IOException {
		writer.append(prefix);
		if (!isRoot) {
			writer.append((lastInPeers ? CORNER_TAIL : CORNER_MIDDLE));
		}
		writer.append(treeNodeConverter.name(current));
		writer.append('\n');

		List<? extends T> children = treeNodeConverter.children(current);

		if (children != null && !children.isEmpty()) {
			String indentation = isRoot ? "" : INDENT_BLANK;
			for (int i = 0; i < children.size() - 1; ++i) {
				process(children.get(i), treeNodeConverter, prefix + (lastInPeers ? indentation : INDENT_MIDDLE), false, false, writer);
			}
			process(children.get(children.size() - 1), treeNodeConverter, prefix + (lastInPeers ? indentation : INDENT_MIDDLE), false, true, writer);
		}
	}

	/**
	 * 将节点输出为字符串格式
	 *
	 * @param root - 根节点
	 */
	public static void print(PrintableTreeNode root, Writer writer) throws IOException {
		TreeNodeConverter<PrintableTreeNode> converter = new TreeNodeConverter<>() {
			@Override
			public String name(PrintableTreeNode printableTreeNode) {
				return printableTreeNode.name();
			}

			@Override
			public List<? extends PrintableTreeNode> children(PrintableTreeNode printableTreeNode) {
				return printableTreeNode.children();
			}
		};

		process(root, converter, "", true, true, writer);
	}

	/**
	 * 将实体转换为tree字符串格式
	 *
	 * @param root              实体
	 * @param treeNodeConverter 转换器，转换实体到tree节点信息
	 * @param <T>               实体类型
	 */
	public static <T> void print(T root, TreeNodeConverter<T> treeNodeConverter, Writer writer) throws IOException {
		process(root, treeNodeConverter, "", true, true, writer);
	}

	public static class DefaultPrintableTreeNode implements PrintableTreeNode {

		private final String name;
		private List<DefaultPrintableTreeNode> nodes;

		public DefaultPrintableTreeNode(String name) {
			this.name = name;
		}

		@SuppressWarnings("UnusedReturnValue")
		public DefaultPrintableTreeNode addNode(DefaultPrintableTreeNode node) {
			if (nodes == null) {
				nodes = new ArrayList<>();
			}
			nodes.add(node);
			return this;
		}

		@SuppressWarnings("unused")
		public DefaultPrintableTreeNode addNodes(Collection<DefaultPrintableTreeNode> nodes) {
			if (this.nodes == null) {
				this.nodes = new ArrayList<>();
			}
			this.nodes.addAll(nodes);
			return this;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public List<DefaultPrintableTreeNode> children() {
			return nodes;
		}
	}


	public interface PrintableTreeNode {
		/**
		 * @return 节点名称
		 */
		String name();

		/**
		 * @return 节点下的子节点
		 */
		List<? extends PrintableTreeNode> children();
	}

	public interface TreeNodeConverter<T> {

		/**
		 * @param t 节点实体
		 * @return 输出的节点名称
		 */
		String name(T t);

		/**
		 * @param t 节点实体
		 * @return 输出的节点子项目列表
		 */
		List<? extends T> children(T t);

	}
}
