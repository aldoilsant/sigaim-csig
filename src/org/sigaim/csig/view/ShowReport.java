package org.sigaim.csig.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.model.CSIGConcept;
import org.sigaim.csig.persistence.PersistenceManager;
import org.sigaim.csig.persistence.PersistentObject;
import org.sigaim.csig.theme.CSIGDialog;
import org.sigaim.csig.theme.CSIGTheme;
import org.sigaim.csig.theme.ThemedWindow;

public class ShowReport /*extends JPanel*/ implements PersistentObject {
	
	private long uuid = (new Date()).getTime();
	final ShowReport self;
	
	private static final String ELEM = AbstractDocument.ElementNameAttribute;
    private static final String COMP = StyleConstants.ComponentElementName;

	private ThemedWindow frame;
	private CSIGReport report;
	private ViewController controller;
	private ShowReportPanel pnlVistaInformes;
	
	private ResourceBundle lang;
	
	private Font conceptFont;
	
	private boolean edited = false; //Is any concept edited?
	private DocumentListener textChangeListener = new DocumentListener() {
		
        @Override
        public void removeUpdate(DocumentEvent e) {
        	onChange();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
        	onChange();
        }

        @Override
        public void changedUpdate(DocumentEvent arg0) {
        	onChange();
        }
    };
	
	public void onChange(){
		if(!edited){
			edited = true;
			btnAnalyze.setEnabled(true);
			//btnFinalize.setEnabled(false);
		}
	}
    
	//String constants
	static String strTitle = "Ver informe";
	private JButton btnFinalize;
	private JButton btnAnalyze;
	
	private class ConceptLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private CSIGConcept concept;
		private String originalText;
		
		public ConceptLabel(final ShowReport listener, String ot, final CSIGReport report, CSIGConcept c){
			concept = c;
			this.originalText = ot;
			//final JLabel rtn = new JLabel(concept.text);
			this.setText(c.text);
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			this.setFont(conceptFont);
			if(!originalText.toLowerCase().equals(c.text.toLowerCase()))
				this.setForeground(new Color(124, 90, 0));
			this.setAlignmentY(.80f);
			
			final JLabel self = this;
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Click on concept: "+ concept.text);
					//CSIGConcept con = concept;
					try {
					/*ConceptView c = */new ConceptView(listener, concept, originalText, self, 
							report.getSynonyms().get(concept.getConceptId()));
					} catch(NullPointerException npe) {
						System.err.println("Synonyms for report are not set (ShowReport:"+
								Thread.currentThread().getStackTrace()[1].getLineNumber()+")" );
					}
					
				}
			});
		}
		
		public CSIGConcept getConcept() {
			return concept;
		}
	}
	
	//PersistenceManager constructor
	public ShowReport(ViewController _controller){
		self = this;
		frame = new ThemedWindow();//new JFrame();
		//frame.setVisible(false);
		//frame.setAutoRequestFocus(false);
		//frame.setTitle(strTitle);
		controller = _controller;
		lang = controller.getLang();
		initialize();
	}
	
	/**
	 * Create the panel.
	 */
	public ShowReport(final CSIGReport r, ViewController _controller) {
		this(_controller);
		report = r;
		
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				long endTime;
				long startTime = System.currentTimeMillis();
				r.fillConcepts();
				endTime = System.currentTimeMillis();
				System.out.println("Getting concepts took(ms): "+ (endTime-startTime));
				return null;
			}
			
			@Override
			protected void done() {
				updateReportView(r);
				frame.setVisible(true);
				WaitModal.close(frame);
				frame.toFront();
			}
			
		};
		
		if(r!=null)
			worker.execute();
		
		PersistenceManager.watch(this);
		
		//frame.toFront();
		//this.requestFocus();
	}
	
	private void updateReportView(CSIGReport r) {
		setTextPane(pnlVistaInformes.txtBiased, r.getBiased(), r.getBiasedConcepts());
		setTextPane(pnlVistaInformes.txtUnbiased, r.getUnbiased(), r.getUnbiasedConcepts());
		setTextPane(pnlVistaInformes.txtImpression, r.getImpressions(), r.getImpressionsConcepts());
		setTextPane(pnlVistaInformes.txtPlan, r.getPlan(), r.getPlanConcepts());
		edited = false;
		btnAnalyze.setEnabled(false);
		btnFinalize.setEnabled(true);
	}
	private void showSaveDialog(){
		int response;
		if(report != null)
			response = CSIGDialog.showYesNoCancel(
					lang.getString("Yes"), lang.getString("No"), lang.getString("Cancel"),
					lang.getString("ShowReport.strAskSave") + "<br />" +
					lang.getString("ShowReport.strNewVersion"), CSIGTheme.iconHelp());
		else
			response = CSIGDialog.showYesNoCancel(
					lang.getString("Yes"), lang.getString("No"), lang.getString("Cancel"),
					lang.getString("ShowReport.strAskSave"), CSIGTheme.iconHelp());
		
		switch(response) {
			case JOptionPane.YES_OPTION:
				saveReport();
				frame.dispose();
				break;
			case JOptionPane.NO_OPTION:
				PersistenceManager.discard(self);
				frame.dispose();
			/*case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:*/// Do nothing				
		}
	}
	
	
	/*
	 * Recovering JLabels and full text from JTextPane is a bit tricky and may change in new
	 * JDK versions, so it remains separated here.
	 * 
	 * This will update the List<CSIGConcept> with the actual (non deleted from TextPane) concepts,
	 * update their position and...
	 * returns the String with the full string (concatenating plain text and labels text).
	 */
	private String updatePart(JTextPane pane, String text, List<CSIGConcept> concepts){
		
		concepts.clear(); //Reset the list, we will introduce only non deleted concepts
		
		StringBuilder sb = new StringBuilder(text.length());
		StyledDocument doc = pane.getStyledDocument();
		String paneText = pane.getText(); //Not same as real text in the pane due to JLabel considered as 1 empty char.
		int paneTextPos = 0;

		ElementIterator iterator = new ElementIterator(doc);
        Element element;
        while ((element = iterator.next()) != null) {
            AttributeSet as = element.getAttributes();
            if (as.containsAttribute(ELEM, COMP)) {
            	if(StyleConstants.getComponent(as) instanceof ConceptLabel){
            		sb.append(paneText.substring(paneTextPos, element.getStartOffset()));
            		paneTextPos = element.getEndOffset(); //This is different from actual position in full text
            		ConceptLabel label = (ConceptLabel)StyleConstants.getComponent(as);
            		CSIGConcept c = label.getConcept();
            		c.start = sb.length();
            		sb.append(label.getText());
            		c.end = sb.length();
            		concepts.add(c);
            	}
            }
        }
        sb.append(paneText.substring(paneTextPos, paneText.length()));
        return sb.toString();
	}
	
	private void saveReport(){		
		SwingWorker<Boolean,Void> updateWorker = new SwingWorker<Boolean,Void>(){
			@Override
			protected Boolean doInBackground() throws Exception {
				report.setBiased(
						updatePart(pnlVistaInformes.txtBiased, report.getBiased(), report.getBiasedConcepts()));
				report.setUnbiased(
						updatePart(pnlVistaInformes.txtUnbiased, report.getUnbiased(), report.getUnbiasedConcepts()));
				report.setPlan(
						updatePart(pnlVistaInformes.txtPlan, report.getPlan(), report.getPlanConcepts()));
				report.setImpressions(
						updatePart(pnlVistaInformes.txtImpression, report.getImpressions(), report.getImpressionsConcepts()));
				return controller.updateReport(report, false);
			}
			
			@Override
			protected void done(){
				try {
					if(this.get()){
						WaitModal.close();
						PersistenceManager.discard(self);
						frame.dispose();
					} else{
						WaitModal.close();
						frame.setVisible(true);
						CSIGDialog.showError(lang.getString("Error.CouldNotUpdateReport"), lang.getString("OK"));
					}
				} catch (HeadlessException | InterruptedException
						| ExecutionException e) {
					e.printStackTrace();
					WaitModal.close();
					frame.setVisible(true);
					CSIGDialog.showError(lang.getString("Error.InternalError"), lang.getString("OK"));
				}
			}
		};
		
		WaitModal.open("Actualizando informe");
		updateWorker.execute();
	}
	
	private void setTextPane(JTextPane pane, String text, List<CSIGConcept>concepts){
		int textPointer = 0;
		if(text.length() <= 0)
			return;
//		concepts.sort(new ConceptsOrderer()); 
		pane.setText("");
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>)f.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        conceptFont = f.deriveFont(attributes);
        //conceptFontRed = f.deriveFont(attributes);
		StyledDocument doc = pane.getStyledDocument();
		pane.setFont(f);
		//Style style = doc.addStyle("JLabel", null);
		try{
			for(CSIGConcept c : concepts){
				if(c.start < 0){
					System.err.println("[Error] Concept "+c.getConceptId()+" ("+c.text+") starts at "+c.start+". Are model and client same version?");
					continue;
				}
				if(textPointer < c.start)
					doc.insertString(doc.getLength(), text.substring(textPointer, c.start), null);
				if(c.start > text.length() || c.end > text.length()){
					System.err.println("[Error] Concept out of bounds, skipping: "+c.getCode());
					continue;
				}
				pane.insertComponent(new ConceptLabel(self, text.substring(c.start, c.end), report, c));
				textPointer = c.end;
			}
			if(textPointer < text.length())
				doc.insertString(doc.getLength(), text.substring(textPointer, text.length()), null);
		} catch(BadLocationException ble) {
			throw new IllegalStateException("Trying to append text to JTextPane", ble);
		}
	}

	private void initialize() {
		//Prepare Title bar
		JPanel pnlActions = ThemedWindow.getDefaultTitleBar();
		FlowLayout fl_pnlActions = (FlowLayout) pnlActions.getLayout();
		fl_pnlActions.setAlignment(FlowLayout.RIGHT);
		
		btnAnalyze = new JButton(lang.getString("btnReanalize"));
		btnAnalyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveReport();
			}
		});
		pnlActions.add(btnAnalyze);
		
		btnFinalize = new JButton(lang.getString("btnFinalize"));
		btnFinalize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				if(edited)
					showSaveDialog();
				else
					frame.dispose();
			}
		});
		pnlActions.add(btnFinalize);
		frame.setTitleBar(pnlActions);
		
		//On exit ask saving if content changed
		frame.getMainFrame().addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if(edited)
		        	showSaveDialog();
		        else
		        	frame.dispose();
		    }
		});
		frame.getMainFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
		frame.setSize(900,550);
		
		pnlVistaInformes = new ShowReportPanel(textChangeListener);
		pnlVistaInformes.setVisible(true);
		frame.add(pnlVistaInformes);
	}
	
	@Override
	public byte[] toData() {
		
		Hashtable<String, Object> status = new Hashtable<String,Object>();
		status.put("uuid", new Long(uuid));
		status.put("reportId", new Long(report.getId()));
		
		List<CSIGConcept> clist = new ArrayList<CSIGConcept>();
		
		status.put("txtBiased", updatePart(pnlVistaInformes.txtBiased, report.getBiased(), clist));
		status.put("clistBiased", clist);
		clist = new ArrayList<CSIGConcept>();
		status.put("txtUnbiased", updatePart(pnlVistaInformes.txtUnbiased, report.getUnbiased(), clist));
		status.put("clistUnbiased", clist);
		clist = new ArrayList<CSIGConcept>();
		status.put("txtImpression", updatePart(pnlVistaInformes.txtImpression, report.getImpressions(), clist));
		status.put("clistImpression", clist);
		clist = new ArrayList<CSIGConcept>();
		status.put("txtPlan", updatePart(pnlVistaInformes.txtPlan, report.getPlan(), clist));
		status.put("clistPlan", clist);
			
		ByteArrayOutputStream bs= new ByteArrayOutputStream();
		try {			
			ObjectOutputStream os = new ObjectOutputStream (bs);
			os.writeObject(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bs.toByteArray();
	}
	
	@Override
	public boolean changed(){
		return edited;
	}

	@Override
	public void restore(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		Hashtable<String, Object> status;
		
		try {
			ObjectInputStream os = new ObjectInputStream(is);
			status = (Hashtable<String, Object>) os.readObject();
		} catch(IOException e){
			e.printStackTrace();
			return;
		} catch(ClassNotFoundException e){
			e.printStackTrace();
			//TODO message, discard object & wrong version?
			return;
		}
		this.uuid = ((Long)status.get("uuid")).longValue();
		Long reportId = (Long) status.get("reportId");
		report = controller.getReport(reportId.longValue());
		
		setTextPane(pnlVistaInformes.txtBiased, 
				(String) status.get("txtBiased"), 
				(List<CSIGConcept>) status.get("clistBiased"));
		setTextPane(pnlVistaInformes.txtUnbiased, 
				(String) status.get("txtUnbiased"), 
				(List<CSIGConcept>) status.get("clistUnbiased"));
		setTextPane(pnlVistaInformes.txtImpression, 
				(String) status.get("txtImpression"), 
				(List<CSIGConcept>) status.get("clistImpression"));
		setTextPane(pnlVistaInformes.txtPlan, 
				(String) status.get("txtPlan"), 
				(List<CSIGConcept>) status.get("clistPlan"));
		
		edited = false;
		btnAnalyze.setEnabled(false);
		//btnFinalize.setEnabled(true);
		
		frame.setVisible(true);
		frame.requestFocus();
	}

	@Override
	public String getUID() {
		if(report != null)
			return "RID"+report.getId()/*+"TS"+uuid Want to differentiate windows from same report?*/;
		else
			return "TS"+uuid;
	}
}
