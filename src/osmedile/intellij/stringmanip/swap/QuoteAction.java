package osmedile.intellij.stringmanip.swap;

import com.google.common.collect.ImmutableSet;
import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;
import osmedile.intellij.stringmanip.AbstractStringManipAction;

import java.util.Map;
import java.util.Set;

public class QuoteAction extends AbstractStringManipAction<Object> {
	private static final Set<String> QUOTES = ImmutableSet.of("\"");

	@Override
	protected String transformSelection(Editor editor, Map<String, Object> actionContext, DataContext dataContext, String s, Object additionalParam) {
		int textLength = editor.getDocument().getTextLength();
		if (textLength == 0 || s.length() == 0) {
			return s;
		}
		if (!QUOTES.contains(s.substring(0, 1)) && !QUOTES.contains(s.substring(s.length() - 1))) {
			int selectionStart = Math.max(0, editor.getSelectionModel().getSelectionStart() - 1);
			int selectionEnd = Math.min(textLength, editor.getSelectionModel().getSelectionEnd() + 1);

			String start = editor.getDocument().getText(TextRange.create(selectionStart, selectionStart + 1));
			String end = editor.getDocument().getText(TextRange.create(selectionEnd - 1, selectionEnd));

			if (QUOTES.contains(start) && QUOTES.contains(end)) {
				editor.getSelectionModel().setSelection(selectionStart, selectionEnd);
				s = editor.getSelectionModel().getSelectedText();
			}
		}

		if (s == null) {
			return s;
		}

		if (s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		} else {
			return "\"" + s + "\"";
		}
	}

	@Override
	protected boolean selectSomethingUnderCaret(Editor editor, Caret caret, DataContext dataContext, SelectionModel selectionModel) {
		TextRange textRange = SelectWordUtil.getWordOrLexemeSelectionRange(editor, caret.getOffset(), c -> !(Character.isWhitespace(c) || c == '\"'));
		if (textRange != null) {
			caret.setSelection(textRange.getStartOffset(), textRange.getEndOffset());
		}
		return true;
	}

	@Override
	public String transformByLine(Map<String, Object> actionContext, String s) {
		throw new UnsupportedOperationException();
	}
}
