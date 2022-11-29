package me.vicasong.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Path valid
 *
 * @author vicasong
 * @since 2022-08-09 14:31
 */
public class PathFormatValidator implements ConstraintValidator<PathFormat, String> {

	private static final Pattern normalPattern = Pattern.compile("[\\t\\n|:\"?*<>]");
	private static final Pattern strictPattern = Pattern.compile("[\\t\\n\\s|:\"?*<>]");

	private static final Pattern windowsPattern = Pattern.compile("[\\t\\n\\r|\"?*<>]");

	private PathFormat constraintAnnotation;

	@Override
	public void initialize(PathFormat constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value != null && !value.isEmpty()) {
			Pattern pattern;
			boolean windows = false;
			if (constraintAnnotation.dependsOnOs() && System.getProperty("os.name").toLowerCase().contains("windows")) {
				pattern = windowsPattern;
				windows = true;
			}
			else if (constraintAnnotation.blankAllow()) {
				pattern = normalPattern;
			}
			else {
				pattern = strictPattern;
			}
			String[] split = pattern.split(value);
			if (split.length > 1) {
				int index = split[0].length();
				if (constraintAnnotation.throwInvalidError()) {
					String msg = "Illegal char <" +
							value.charAt(index) +
							">";
					throw new InvalidPathException(value, msg, index);
				}
				return false;
			}
			else if (windows) {
				try {
					Paths.get(value);
				}
				catch (InvalidPathException e) {
					if (constraintAnnotation.throwInvalidError()) {
						throw e;
					}
					return false;
				}
			}
			int length = constraintAnnotation.length();
			if (length > 0 && value.length() > length) {
				if (constraintAnnotation.throwInvalidError()) {
					throw new InvalidPathException(value, "Illegal path length(" + value.length() + ") that cannot exceed " + length);
				}
				return false;
			}
			if (value.endsWith(".")) {
				if (constraintAnnotation.throwInvalidError()) {
					throw new InvalidPathException(value, "Illegal char <.>", value.length() - 1);
				}
				return false;
			}
		}
		return true;
	}
}
