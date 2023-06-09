package osmedile.intellij.stringmanip;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Nullable;
import osmedile.intellij.stringmanip.utils.IdeUtils;

import java.util.ArrayList;
import java.util.List;
@Deprecated
/**
 * @see MultiCaretHandlerHandler2
 */
public abstract class MultiCaretHandlerHandler<T> extends MyEditorWriteActionHandler<T> {

	public MultiCaretHandlerHandler(Class aClass) {
		super(aClass);
	}

	@Override
	protected void executeWriteAction(Editor editor, DataContext dataContext, @Nullable T additionalParameter) {
		List<CaretState> caretsAndSelections = editor.getCaretModel().getCaretsAndSelections();
		if (caretsAndSelections.size() > 1) {
			multiSelection(editor, caretsAndSelections, additionalParameter);
		} else if (caretsAndSelections.size() == 1) {
			singleSelection(editor, caretsAndSelections, additionalParameter);
		}
	}

	protected void singleSelection(Editor editor, List<CaretState> caretsAndSelections, T additionalParameter) {
		CaretState caretsAndSelection = caretsAndSelections.get(0);
		LogicalPosition selectionStart = caretsAndSelection.getSelectionStart();
		LogicalPosition selectionEnd = caretsAndSelection.getSelectionEnd();
		String text = editor.getDocument().getText(
				new TextRange(editor.logicalPositionToOffset(selectionStart),
						editor.logicalPositionToOffset(selectionEnd)));

		String charSequence = processSingleSelection(editor, text, additionalParameter);

		editor.getDocument().replaceString(editor.logicalPositionToOffset(selectionStart),
				editor.logicalPositionToOffset(selectionEnd), charSequence);
	}

	protected void multiSelection(Editor editor, List<CaretState> caretsAndSelections, T additionalParameter) {
		IdeUtils.sort(caretsAndSelections);
		List<String> lines = new ArrayList<String>();
		for (CaretState caretsAndSelection : caretsAndSelections) {
			LogicalPosition selectionStart = caretsAndSelection.getSelectionStart();
			LogicalPosition selectionEnd = caretsAndSelection.getSelectionEnd();
			String text = editor.getDocument().getText(
					new TextRange(editor.logicalPositionToOffset(selectionStart),
							editor.logicalPositionToOffset(selectionEnd)));
			lines.add(text);
		}

		lines = processMultiSelections(editor, lines, additionalParameter);

		for (int i = caretsAndSelections.size() - 1; i >= 0; i--) {
			CaretState caretsAndSelection = caretsAndSelections.get(i);
			LogicalPosition selectionStart = caretsAndSelection.getSelectionStart();
			LogicalPosition selectionEnd = caretsAndSelection.getSelectionEnd();

			if (lines.size() > i) {
				String line = lines.get(i);
				if (line == null) {
					line = "";
				}
				editor.getDocument().replaceString(editor.logicalPositionToOffset(selectionStart),
						editor.logicalPositionToOffset(selectionEnd), line);
			} else {
				editor.getDocument().deleteString(editor.logicalPositionToOffset(selectionStart), editor.logicalPositionToOffset(selectionEnd));
				Caret caretAt = editor.getCaretModel().getCaretAt(editor.logicalToVisualPosition(selectionStart));
				editor.getCaretModel().removeCaret(caretAt);
			}
		}
	}

	protected abstract String processSingleSelection(Editor editor, String text, T additionalParameter);

	protected abstract List<String> processMultiSelections(Editor editor, List<String> lines, T additionalParameter);
}