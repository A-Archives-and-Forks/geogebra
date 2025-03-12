package org.geogebra.common.main.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.syntax.EnglishCommandSyntax;
import org.geogebra.common.main.syntax.LocalizedCommandSyntax;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.ManualPage;
import org.geogebra.common.util.MatchedString;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

public class AutocompleteProvider {
	@NonOwning
	@Weak
	@Nonnull
	private final App app;
	private final boolean isForClassicCAS;
	private LocalizedCommandSyntax englishCommandSyntax;
	private @CheckForNull Set<Operation> filteredOperations;

	/**
	 * @param app application
	 * @param isForClassicCAS whether this is for the classic CAS
	 */
	public AutocompleteProvider(@Nonnull App app, boolean isForClassicCAS) {
		this.app = app;
		this.isForClassicCAS = isForClassicCAS;
	}

	/**
	 * Adds a syntax filter.
	 * @param syntaxFilter a syntax filter.
	 */
	public void addSyntaxFilter(@Nonnull SyntaxFilter syntaxFilter) {
		if (syntaxFilter != null) {
			getEnglishCommandSyntax().addSyntaxFilter(syntaxFilter);
		}
	}

	/**
	 * Removes a previously added syntax filter.
	 * @param syntaxFilter a syntax filter.
	 */
	public void removeSyntaxFilter(@Nonnull SyntaxFilter syntaxFilter) {
		if (syntaxFilter != null) {
			getEnglishCommandSyntax().removeSyntaxFilter(syntaxFilter);
		}
	}

	/**
	 * Sets operations to be filtered out from the results.
	 * @param filteredOperations An optional set of operations to filter out from the results.
	 */
	public void setFilteredOperations(@CheckForNull Set<Operation> filteredOperations) {
		this.filteredOperations = filteredOperations;
	}

	/**
	 * @param localizedCommandName localized command
	 * @return syntaxes of a single command
	 */
	public List<String> getSyntaxes(String localizedCommandName) {
		ArrayList<String> syntaxes = new ArrayList<>();
		addSyntaxes(localizedCommandName, syntaxes);
		return syntaxes;
	}

	/**
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 *
	 * @param commands
	 *            commands
	 * @return syntaxes
	 */
	public List<MatchedString> getSyntaxes(List<MatchedString> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<MatchedString> syntaxes = new ArrayList<>();
		for (MatchedString command : commands) {
			addSyntaxes(command, syntaxes);
		}
		return syntaxes;
	}

	private void addSyntaxes(MatchedString match, ArrayList<MatchedString> syntaxes) {
		String syntaxString = getSyntaxString(match.content);
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(new MatchedString(syntax, match.from, match.to));
		}
	}

	private void addSyntaxes(String localizedCommandName, ArrayList<String> syntaxes) {
		String syntaxString = getSyntaxString(localizedCommandName);
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(syntax);
		}
	}

	private String getSyntaxString(String localizedCommandName) {
		String internalCommandName = app.getInternalCommand(localizedCommandName);
		boolean englishOnly = internalCommandName == null
				&& isFallbackCompletionAllowed();
		if (englishOnly) {
			internalCommandName = app.englishToInternal(localizedCommandName);
		}

		String syntaxString;
		if (isCas()) {
			LocalizedCommandSyntax commandSyntax = app.getLocalization().getCommandSyntax();
			syntaxString = commandSyntax.getCommandSyntaxCAS(internalCommandName);
		} else {
			LocalizedCommandSyntax commandSyntax = englishOnly
					? getEnglishCommandSyntax() : app.getLocalization().getCommandSyntax();
			AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
			syntaxString = algebraProcessor.getSyntax(commandSyntax, internalCommandName,
					app.getSettings());
		}

		if (syntaxString == null || syntaxString.isEmpty()) {
			return "";
		}

		if (syntaxString.endsWith(Localization.syntaxCAS)
				|| syntaxString.endsWith(Localization.syntaxStr)) {
			// command not found, check for macros
			Macro macro = isCas() ? null
					: app.getKernel().getMacro(internalCommandName);
			if (macro != null) {
				return macro.toString();
			} else {
				// syntaxes.add(cmdInt + "[]");
				Log.debug("Can't find syntax for: " + internalCommandName);
			}
			return "";
		}
		return syntaxString;
	}

	private LocalizedCommandSyntax getEnglishCommandSyntax() {
		if (englishCommandSyntax == null) {
			englishCommandSyntax = new EnglishCommandSyntax(app.getLocalization());
		}
		return englishCommandSyntax;
	}

	/**
	 * @return whether to allow English commands as well
	 */
	public boolean isFallbackCompletionAllowed() {
		return app.getLocalization().languageIs("zh");
	}

	/**
	 * @param curWord word to be completed
	 * @return stream of suggestions
	 */
	public Stream<Completion> getCompletions(String curWord) {
		List<String> functionResults = app.getParserFunctions().getCompletions(curWord,
				filteredOperations);
		Stream<Completion> completions = functionResults.stream()
				.map(function -> new Completion(getMatch(function, curWord),
						Collections.singletonList(function),
						ManualPage.OPERATORS, null));

		List<MatchedString> commandResults = getCommandDictionary()
				.getCompletions(curWord.toLowerCase());
		if (commandResults != null) {
			Stream<Completion> commandCompletions = commandResults.stream()
					.map(command -> new Completion(command,
							getSyntaxes(command.content),
							ManualPage.COMMAND,
							app.getInternalCommand(command.content)));
			completions = Stream.concat(completions, commandCompletions);
		}

		return completions.filter(completion -> !completion.syntaxes.isEmpty());
	}

	private MatchedString getMatch(String function, String curWord) {
		return new MatchedString(function.split("\\(")[0], 0, curWord.length());
	}

	private boolean isCas() {
		return isForClassicCAS || app.getConfig().getVersion() == GeoGebraConstants.Version.CAS;
	}

	private LowerCaseDictionary getCommandDictionary() {
		return isCas() ? app.getCommandDictionaryCAS() : app.getCommandDictionary();
	}

	public static class Completion {
		public final MatchedString match;
		public final List<String> syntaxes;
		@CheckForNull
		public final String helpPage;
		public final ManualPage helpType;

		private Completion(MatchedString match, List<String> syntaxes, ManualPage helpType,
				@Nullable String helpPage) {
			this.match = match;
			this.syntaxes = syntaxes;
			this.helpPage = helpPage;
			this.helpType = helpType;
		}

		public MatchedString getMatch() {
			return match;
		}

		public String getCommand() {
			return match.content;
		}

		public List<String> getSyntaxes() {
			return syntaxes;
		}

		@CheckForNull
		public String getHelpPage() {
			return helpPage;
		}

		public ManualPage getHelpType() {
			return helpType;
		}

		public int getOffset() {
			return match.from;
		}

		@Override
		public String toString() {
			return match.content;
		}
	}
}
