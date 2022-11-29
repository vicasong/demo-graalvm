package me.vicasong.handle;

import java.nio.file.Path;
import java.util.Comparator;

/**
 * Path sorter
 *
 * @author vicasong
 * @since 2022-09-28 14:34
 */
public class PathComparator implements Comparator<Path> {
	@Override
	public int compare(Path o1, Path o2) {
		return Compare(o1, o2);
	}

	public static int Compare(Path o1, Path o2) {
		if (o2 != null) {
			if (o1.equals(o2)) {
				return 0;
			}
			int r = Integer.compare(o1.getNameCount(), o2.getNameCount());
			if (r == 0) {
				return o1.toString().compareTo(o2.toString());
			}
			return r;
		}
		return -1;
	}
}
