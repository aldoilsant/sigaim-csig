import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

import org.sigaim.csig.model.CSIGPatient;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;


public class TestingFrame extends JPanel {

	/**
	 * Create the panel.
	 */
	public TestingFrame(DocumentListener textChangeListener) {
		JPanel pnlReportInfo = this;

		pnlReportInfo.setLayout(new FormLayout(new ColumnSpec[] {
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("5dlu", true), Sizes.constant("5dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:max(31dlu;min):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("100dlu", true), Sizes.constant("100dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("5dlu", true), Sizes.constant("5dlu", true)), 0),},
			new RowSpec[] {
				RowSpec.decode("4px"),
				RowSpec.decode("bottom:max(21dlu;default)"),
				RowSpec.decode("max(4dlu;default)"),}));
		
		JLabel lblPatient = new JLabel("lblPatient");
		pnlReportInfo.add(lblPatient, "3, 2, center, bottom");
		
		final JComboBox<String> ddlPatient = new JComboBox<String>();
		pnlReportInfo.add(ddlPatient, "5, 2, fill, default");
		
		final JButton btnNewPatient = new JButton("Dictation.btnCreatePatient");
		btnNewPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CSIGPatient newPat = null;//controller.getModelController().createPatient();
				if(newPat != null) {
					ddlPatient.addItem(newPat.toString());
					ddlPatient.setSelectedItem(newPat.toString());
					ddlPatient.setEnabled(false);
					btnNewPatient.setEnabled(false);
					//askSave = true;
				}
			}
		});
		pnlReportInfo.add(btnNewPatient, "7, 2, default, bottom");
		
		JButton btnPause = new JButton("Dictation.btnPauseRecord");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {/*switchPause();*/}
		});
		btnPause.setEnabled(false);
		pnlReportInfo.add(btnPause, "11, 2");
		
		JButton btnRecord = new JButton("Dictation.btnStartRecord");
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//switchRecord();
			}
		});
		btnRecord.setEnabled(false);
		pnlReportInfo.add(btnRecord, "13, 2");
	}

}
