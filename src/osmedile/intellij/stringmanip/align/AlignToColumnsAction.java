package osmedile.intellij.stringmanip.align;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import osmedile.intellij.stringmanip.MultiCaretHandlerHandler;
import osmedile.intellij.stringmanip.MyEditorAction;
import osmedile.intellij.stringmanip.StringManipulationBundle;
import osmedile.intellij.stringmanip.config.PluginPersistentStateComponent;

import java.util.List;

public class AlignToColumnsAction extends MyEditorAction {


	protected AlignToColumnsAction() {
		this(true);
	}

	protected AlignToColumnsAction(boolean setupHandler) {
		super(null);
		if (setupHandler) {
			this.setupHandler(new MultiCaretHandlerHandler<ColumnAlignerModel>(getActionClass()) {

				@NotNull
				@Override
				protected Pair<Boolean, ColumnAlignerModel> beforeWriteAction(Editor editor, DataContext dataContext) {
					SelectionModel selectionModel = editor.getSelectionModel();
					if (!selectionModel.hasSelection()) {
						selectionModel.setSelection(0, editor.getDocument().getTextLength());
					}

					PluginPersistentStateComponent stateComponent = PluginPersistentStateComponent.getInstance();
					final AlignToColumnsForm alignToColumnsForm = new AlignToColumnsForm(stateComponent.guessModel(editor), editor);

					if (!alignToColumnsForm.showAndGet(editor.getProject(), StringManipulationBundle.message("align.to.columns"), "StringManipulation.AlignToColumnsAction")) {
						return stopExecution();
					}
					ColumnAlignerModel model = alignToColumnsForm.getModel();
					stateComponent.addToHistory(alignToColumnsForm.getModel());
					return continueExecution(model);
				}

				@Override
				protected String processSingleSelection(Editor editor, String text, ColumnAlignerModel model) {
					return new ColumnAligner(model).align(text);
				}

				@Override
				protected List<String> processMultiSelections(Editor editor, List<String> lines, ColumnAlignerModel model) {
					return new ColumnAligner(model).align(lines);
				}

			});
		}

	}


}
