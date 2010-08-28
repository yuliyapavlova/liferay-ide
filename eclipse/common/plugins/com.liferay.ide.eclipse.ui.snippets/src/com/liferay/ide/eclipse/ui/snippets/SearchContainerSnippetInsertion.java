
package com.liferay.ide.eclipse.ui.snippets;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.ui.snippets.wizard.LiferayUISearchContainerWizard;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

@SuppressWarnings("restriction")
public class SearchContainerSnippetInsertion extends AbstractSnippetInsertion {

	public SearchContainerSnippetInsertion() {
		super();
	}

	@Override
	public void insert(IEditorPart editorPart) {
		if (this.fEditorPart == null) {
			this.fEditorPart = editorPart;
		}
		super.insert(editorPart);
	}

	protected String getPreparedText(LiferayUISearchContainerWizard wizard) {
		String text = fItem.getContentString();

		text = StringUtils.replace(text, "${modelClass}", wizard.getModelClass());
		text = StringUtils.replace(text, "${model}", wizard.getModel());
		text = StringUtils.replace(text, "${varName}", wizard.getVarName());

		StringBuffer columns = new StringBuffer();
		String[] propColumns = wizard.getPropertyColumns();

		if (!CoreUtil.isNullOrEmpty(propColumns)) {
			for (String prop : propColumns) {
				columns.append("<liferay-ui:search-container-column-text property=\"");
				columns.append(prop);
				columns.append("\" />\n\n\t\t");
			}
		}

		String columnsVal = columns.toString();
		text = StringUtils.replace(text, "${columns}", CoreUtil.isNullOrEmpty(columnsVal) ? "" : columnsVal);

		// Update EOLs (bug 80231)
		String systemEOL = System.getProperty("line.separator"); //$NON-NLS-1$
		text = StringUtils.replace(text, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		text = StringUtils.replace(text, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!"\n".equals(systemEOL) && systemEOL != null) { //$NON-NLS-1$
			text = StringUtils.replace(text, "\n", systemEOL); //$NON-NLS-1$
		}

		return text;
	}

	@Override
	protected String getResolvedString(Shell host) {
		LiferayUISearchContainerWizard wizard = new LiferayUISearchContainerWizard(this.fEditorPart);
		WizardDialog dialog = new WizardDialog(host, wizard);
		dialog.setBlockOnOpen(true);
		int retval = dialog.open();

		if (retval == Window.OK) {
			return getPreparedText(wizard);
		}

		return "";
	}

}
