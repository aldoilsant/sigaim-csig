package org.sigaim.csig.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.sigaim.csig.model.CSIGConcept;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import com.jgoodies.forms.layout.Sizes;

public class ConceptView extends JPanel {

	//private final JPanel contentPanel = new JPanel();
	private final JPanel contentPanel = this;
	private JTextField txtSnomed;
	private JTextField txtDictation;
	
	private boolean freezed = false;


	/**
	 * Create the dialog.
	 */
	public ConceptView(CSIGConcept concept, String text, Component parent, List<CSIGConcept> synonyms) {
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min(200dlu;pref):grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblTerminology = new JLabel("SNOMED");
		contentPanel.add(lblTerminology, "2, 2, right, default");
		
		txtSnomed = new JTextField();
		txtSnomed.setEditable(false);
		contentPanel.add(txtSnomed, "4, 2, fill, default");
		txtSnomed.setColumns(10);
		
		JLabel lbl = new JLabel("Dictado");
		contentPanel.add(lbl, "2, 4, right, default");
		
		txtDictation = new JTextField();
		txtDictation.setAutoscrolls(false);
		txtDictation.setEditable(false);
		contentPanel.add(txtDictation, "4, 4, fill, default");
		txtDictation.setColumns(10);
		
		
		//Position
		Point p = parent.getLocationOnScreen();
		this.setLocation(p.x-(this.getWidth()/5), p.y+parent.getHeight());
		//setLocationRelativeTo(parent);
		
		//Load values
		txtDictation.setText(text);
		txtSnomed.setText(concept.code);
		lblTerminology.setText(concept.terminology);
		
		JLabel lblSinonym = new JLabel("Sinónimos");
		add(lblSinonym, "2, 6, right, default");
		
		ArrayList<String> ddlList;
		if(synonyms != null) {
			ddlList = new ArrayList<String>(synonyms.size());
			for(CSIGConcept s : synonyms){
				ddlList.add(s.toString());
			}
		} else {
			ddlList = new ArrayList<String>(0);
		}
		JComboBox ddlSynonym = new JComboBox(ddlList.toArray());
		ddlSynonym.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                freezed = true;
            }
		});
		for (Component component : ddlSynonym.getComponents()) {
			   component.addMouseListener(ddlSynonym.getMouseListeners()[0]);
		}
		add(ddlSynonym, "4, 6, fill, default");
		
		JCheckBox chkError = new JCheckBox("Error de identificación");
		chkError.setFocusable(false);
		add(chkError, "4, 8, left, default");
		
		JButton btnSave = new JButton("Actualizar concepto");
		btnSave.addFocusListener(ddlSynonym.getFocusListeners()[0]);
		add(btnSave, "4, 10, right, default");
		
		
		BalloonTipStyle edgedLook = new EdgedBalloonStyle(contentPanel.getBackground(), Color.BLUE);
		final BalloonTip b =  new BalloonTip((JComponent) parent, (JComponent) this, edgedLook, false);
		
		contentPanel.requestFocus();
		contentPanel.addFocusListener(new FocusListener() {
				@Override
		        public void focusLost(FocusEvent e) {
					if(!freezed)
						b.closeBalloon();
		        }

				@Override
				public void focusGained(FocusEvent e) {
					freezed = false;
				}
		});
		contentPanel.addMouseListener(new MouseAdapter(){
			@Override
            public void mouseEntered(MouseEvent e) {
                freezed = false;
                requestFocus();
            }
		});
	}

}
