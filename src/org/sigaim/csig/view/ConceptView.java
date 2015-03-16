package org.sigaim.csig.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.sigaim.csig.model.CSIGConcept;
import org.sigaim.csig.view.helper.ConceptPositioner;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.BalloonTip.AttachLocation;
import net.java.balloontip.BalloonTip.Orientation;
import net.java.balloontip.CustomBalloonTip;
import net.java.balloontip.positioners.BalloonTipPositioner;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConceptView extends JPanel {

	//private final JPanel contentPanel = new JPanel();
	private final JPanel contentPanel = this;
	private JTextField txtSnomed;
	private JTextField txtDictation;
	private ShowReport listener;
	
	private JDialog glass;
	
	private CSIGConcept concept;
	private List<CSIGConcept> synonyms;
	
	private boolean freezed = false;
	private JCheckBox chkError;
	private JComboBox<String> ddlSynonym;
	
	private JLabel label;
	protected BalloonTip b;

	
	private void updateParentLabel() {
		CSIGConcept selected = synonyms.get(ddlSynonym.getSelectedIndex());
		//label.setText((String)ddlSynonym.getSelectedItem());
		label.setText(selected.toString());
		concept.replace(selected);
		listener.onChange();
		
		if(chkError.isSelected())
			label.setForeground(Color.RED);
		else
			label.setForeground(Color.BLUE);
		
		b.closeBalloon();
	}

	/**
	 * Create the dialog.
	 */
	public ConceptView(ShowReport listener, CSIGConcept concept, String text, final Component parent, List<CSIGConcept> synonyms) {
		this.concept = concept;
		this.synonyms = synonyms;
		this.listener = listener;
		
		label = (JLabel) parent;
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
		
		MouseAdapter mouseStillInside = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                freezed = true;
            }
		};
		
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
		txtSnomed.setText(concept.getCode());
		lblTerminology.setText(concept.getTerminology());
		
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
		ddlSynonym = new JComboBox<String>(ddlList.toArray(new String[ddlList.size()]));
		ddlSynonym.addMouseListener(mouseStillInside);
		for (Component component : ddlSynonym.getComponents()) {
			   component.addMouseListener(mouseStillInside);
		}
		add(ddlSynonym, "4, 6, fill, default");
		
		chkError = new JCheckBox("Error de identificación");
		chkError.addMouseListener(mouseStillInside);
		//chkError.setFocusable(false);
		add(chkError, "4, 8, left, default");
		
		JButton btnSave = new JButton("Actualizar concepto");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateParentLabel();
			}
		});
		btnSave.addMouseListener(mouseStillInside);
		add(btnSave, "4, 10, right, default");
		
		//Full screen transparent container:
		glass = new JDialog();
		glass.setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		glass.setUndecorated(true);
		glass.setBackground(new Color(0,0,0,0));
		//glass.setOpacity(0);
		glass.setVisible(true);
		Rectangle anchor = new Rectangle(parent.getBounds());
		anchor.x = parent.getLocationOnScreen().x;
		anchor.y = parent.getLocationOnScreen().y;
		
		/*BalloonTipStyle edgedLook = new EdgedBalloonStyle(contentPanel.getBackground(), Color.BLUE);*/
		BalloonTipStyle edgedLook = new RoundedBalloonStyle(5, 5, contentPanel.getBackground(), contentPanel.getForeground());

		b = new CustomBalloonTip((JComponent)glass.getContentPane(), this, anchor, edgedLook, BalloonTip.Orientation.LEFT_ABOVE,  BalloonTip.AttachLocation.ALIGNED, 20, 20,false);
		//b =  new BalloonTip((JComponent)glass.getContentPane(), (JComponent) this,	edgedLook, false);
		//b = new BalloonTip((JComponent) parent, (JComponent) this, edgedLook, (BalloonTipPositioner) (new ConceptPositioner()), (JButton) null);
		/*b = new BalloonTip((JComponent) parent, (JComponent) this, edgedLook, Orientation.LEFT_ABOVE,
					AttachLocation.ALIGNED, 20, 20, false);*/
		b.setVisible(true);
		
		contentPanel.requestFocus();
		contentPanel.addFocusListener(new FocusListener() {
				@Override
		        public void focusLost(FocusEvent e) {
					if(!freezed) {
						b.closeBalloon();
						glass.dispose();
					}
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
