package docs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle task that scans the sources and writes {@code JavaDocs.md} at the repo root.
 *
 * <p>Running the task twice with unchanged source produces no diff.</p>
 */
public abstract class GenerateJavaDocsTask extends DefaultTask {

	/**
	 * The {@code src/main/java} directory to scan.
	 *
	 * @return the source-root property
	 */
	@InputDirectory
	public abstract DirectoryProperty getSourceRoot();

	/**
	 * The {@code JavaDocs.md} file to write.
	 *
	 * @return the output-file property
	 */
	@OutputFile
	public abstract RegularFileProperty getOutputFile();

	/**
	 * Scans, renders, and writes the Markdown file as UTF-8 with LF line endings.
	 */
	@TaskAction
	public void generate() {
		Path sourceRoot = getSourceRoot().get().getAsFile().toPath();
		List<JavaDocModel.TypeDoc> types = new JavaDocScanner(sourceRoot).scan();
		String markdown = new MarkdownRenderer().render(types);
		Path output = getOutputFile().get().getAsFile().toPath();
		try {
			Files.write(output, markdown.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			throw new UncheckedIOException("Failed to write " + output, e);
		}
		getLogger().lifecycle("Wrote {} ({} types)", output, types.size());
	}
}
