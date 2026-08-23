package docs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle task that fails the build when {@code JavaDocs.md} is stale or coverage is incomplete.
 *
 * <p>Both checks run and their failures aggregate: a single run reports drift and
 * missing-doc problems together rather than failing on the first.</p>
 */
public abstract class CheckJavaDocsTask extends DefaultTask {

	private static final int DIFF_PREVIEW_LINES = 20;

	/**
	 * The {@code src/main/java} directory to scan.
	 *
	 * @return the source-root property
	 */
	@InputDirectory
	public abstract DirectoryProperty getSourceRoot();

	/**
	 * The committed {@code JavaDocs.md} to compare against.
	 *
	 * @return the output-file property
	 */
	@Internal
	public abstract RegularFileProperty getOutputFile();

	/**
	 * Whether {@code @Override} methods are exempt from the coverage requirement.
	 *
	 * @return the exclude-overrides property (defaults to {@code false})
	 */
	@Internal
	public abstract Property<Boolean> getExcludeOverrides();

	/**
	 * Runs the drift and coverage checks and fails once if either reports a problem.
	 */
	@TaskAction
	public void check() {
		Path sourceRoot = getSourceRoot().get().getAsFile().toPath();
		List<JavaDocModel.TypeDoc> types = new JavaDocScanner(sourceRoot).scan();
		List<String> failures = new ArrayList<>();
		checkDrift(types, failures);
		checkCoverage(types, failures);
		if (!failures.isEmpty()) {
			throw new GradleException(String.join("\n\n", failures));
		}
		getLogger().lifecycle("JavaDocs.md is fresh and all public/protected members are documented.");
	}

	/**
	 * Compares freshly rendered Markdown against the committed file byte-for-byte.
	 *
	 * @param types the scanned type records
	 * @param failures the accumulating failure-message list
	 */
	private void checkDrift(List<JavaDocModel.TypeDoc> types, List<String> failures) {
		String expected = new MarkdownRenderer().render(types);
		Path output = getOutputFile().get().getAsFile().toPath();
		if (!Files.exists(output)) {
			failures.add("JavaDocs.md is missing. Run ./gradlew generateJavaDocs and commit the result.");
			return;
		}
		String actual = readLf(output);
		if (!expected.equals(actual)) {
			StringBuilder message = new StringBuilder(
					"JavaDocs.md is out of date. Run ./gradlew generateJavaDocs and commit the result.");
			message.append("\n").append(firstDiff(expected, actual));
			failures.add(message.toString());
		}
	}

	/**
	 * Runs the coverage checker and records any offenders.
	 *
	 * @param types the scanned type records
	 * @param failures the accumulating failure-message list
	 */
	private void checkCoverage(List<JavaDocModel.TypeDoc> types, List<String> failures) {
		boolean excludeOverrides = getExcludeOverrides().getOrElse(false);
		List<String> offenders = new CoverageChecker(excludeOverrides).findOffenders(types);
		if (!offenders.isEmpty()) {
			StringBuilder message = new StringBuilder(
					"The following public/protected declarations are missing Javadoc:");
			for (String offender : offenders) {
				message.append("\n  ").append(offender);
			}
			failures.add(message.toString());
		}
	}

	/**
	 * Reads a file as UTF-8, normalizing line endings to LF for comparison.
	 *
	 * @param path the file to read
	 * @return the file content with {@code \n} line endings
	 */
	private String readLf(Path path) {
		try {
			String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			return content.replace("\r\n", "\n").replace("\r", "\n");
		}
		catch (IOException e) {
			throw new UncheckedIOException("Failed to read " + path, e);
		}
	}

	/**
	 * Builds a short preview of the first differing lines to aid debugging.
	 *
	 * @param expected the freshly rendered content
	 * @param actual the committed content
	 * @return a unified-style preview of the first divergence
	 */
	private String firstDiff(String expected, String actual) {
		String[] expectedLines = expected.split("\n", -1);
		String[] actualLines = actual.split("\n", -1);
		int max = Math.max(expectedLines.length, actualLines.length);
		StringBuilder preview = new StringBuilder("First difference:");
		int shown = 0;
		for (int i = 0; i < max && shown < DIFF_PREVIEW_LINES; i++) {
			String expectedLine = i < expectedLines.length ? expectedLines[i] : "<end of file>";
			String actualLine = i < actualLines.length ? actualLines[i] : "<end of file>";
			if (!expectedLine.equals(actualLine)) {
				preview.append("\n  line ").append(i + 1);
				preview.append("\n  - committed: ").append(actualLine);
				preview.append("\n  + expected:  ").append(expectedLine);
				shown++;
			}
		}
		return preview.toString();
	}
}
