package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dataobject.SnomedConcept;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;

public class ConceptView extends JPanel {

	//private final JPanel contentPanel = new JPanel();
	private final JPanel contentPanel = this;
	private JTextField txtSnomed;
	private JTextField txtDictation;


	/**
	 * Create the dialog.
	 */
	public ConceptView(SnomedConcept concept, Component parent) {
		/*setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {
                //do nothing
            }

            public void windowLostFocus(WindowEvent e) {
            	ConceptView.this.dispose();
            }

        });
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 280, 239);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);*/
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblSnomed = new JLabel("SNOMED");
		contentPanel.add(lblSnomed, "2, 2, right, default");
		
		txtSnomed = new JTextField();
		txtSnomed.setEditable(false);
		contentPanel.add(txtSnomed, "4, 2, fill, default");
		txtSnomed.setColumns(10);
		
		JLabel lblDictation = new JLabel("Dictado");
		contentPanel.add(lblDictation, "2, 4, right, default");
		
		txtDictation = new JTextField();
		txtDictation.setEditable(false);
		contentPanel.add(txtDictation, "4, 4, fill, default");
		txtDictation.setColumns(10);
		
		
		//Position
		Point p = parent.getLocationOnScreen();
		this.setLocation(p.x-(this.getWidth()/5), p.y+parent.getHeight());
		//setLocationRelativeTo(parent);
		
		//Load values
		txtDictation.setText(concept.text);
		txtSnomed.setText(Integer.toString(concept.code));
		//this.setVisible(true);
		BalloonTipStyle edgedLook = new EdgedBalloonStyle(contentPanel.getBackground(), Color.BLUE);
		final BalloonTip b =  new BalloonTip((JComponent) parent, (JComponent) this, edgedLook, false);
		contentPanel.requestFocus();
		contentPanel.addFocusListener(new FocusListener() {
				@Override
		        public void focusLost(FocusEvent e) {
					b.closeBalloon();
		        }

				@Override
				public void focusGained(FocusEvent e) {
					//DO nothing
				}
		});
	}

}
