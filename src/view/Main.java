package view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import dataobject.Report;
import dataobject.SnomedConcept;


public class Main implements ViewController {

	public JFrame frame;
	private JDialog login;
	private ReportList reportList;
	
	private String t1 = "Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 1500, cuando un impresor (N. del T. persona que se dedica a la imprenta) desconocido usó una galería de textos y los mezcló de tal manera que logró hacer un libro de textos especimen. No sólo sobrevivió 500 años, sino que tambien ingresó como texto de relleno en documentos electrónicos, quedando esencialmente igual al original. Fue popularizado en los 60s con la creación de las hojas \"Letraset\", las cuales contenian pasajes de Lorem Ipsum, y más recientemente con software de autoedición, como por ejemplo Aldus PageMaker, el cual incluye versiones de Lorem Ipsum.";
	private String t2 = "Es un hecho establecido hace demasiado tiempo que un lector se distraerá con el contenido del texto de un sitio mientras que mira su diseño. El punto de usar Lorem Ipsum es que tiene una distribución más o menos normal de las letras, al contrario de usar textos como por ejemplo \"Contenido aquí, contenido aquí\". Estos textos hacen parecerlo un español que se puede leer. Muchos paquetes de autoedición y editores de páginas web usan el Lorem Ipsum como su texto por defecto, y al hacer una búsqueda de \"Lorem Ipsum\" va a dar por resultado muchos sitios web que usan este texto si se encuentran en estado de desarrollo. Muchas versiones han evolucionado a través de los años, algunas veces por accidente, otras veces a propósito (por ejemplo insertándole humor y cosas por el estilo).";
	private String tBiased = "Acude a urgencias: refiere sensación distérmica de 3 días evolución sin fiebre termometrada.\nMolestias faríngeas con tos improductiva.";
	private String tUnbiased = "Buen estado general, consciente, orientado y colaborador, eupneico. Sin signos distrés respiratorio. No exantemas ni petequias. Bien hidratado y bien perfundido. Afebril 36 grados. Otorrinolaringología: Hiperemia faríngea sin evidencia de exudados. Auscultación cardiorrespiratoria: Murmullo vesicular conservado sin estertores.";
	private String tImpressions = "Síndrome catarral.";
	private String tPlan = "Hidratación + Ibuprofeno 600 (1-1-1) + Iniston antitusivo.";
	
	
	private ArrayList<Object> biasedExample1(){
		ArrayList<Object> rtn = new ArrayList<Object>();
		rtn.add(new String("Acude a urgencias: refiere sensación distérmica de 3 días evolución sin"));
		rtn.add(new SnomedConcept(271897009, "fiebre", "fiebre"));
		rtn.add(new String("termometrada\nMolestias faríngeas con tos improductiva."));
		return rtn;
	}
	
	
	public List<Report> getInforms() {
		ArrayList<Report> rtn = new ArrayList<Report>();
		Report i = new Report();
		i.setBiased(biasedExample1());
		i.setUnbiased(new ArrayList<Object>());
		i.setPlan(new ArrayList<Object>());
		i.setImpressions(new ArrayList<Object>());
		rtn.add(i);
		/*i = new Report();
		i.setBiased(t2);
		i.setUnbiased(t1);
		i.setPlan(t2);
		i.setImpressions(t1);
		rtn.add(i);*/
		return rtn;
	}
	
	public void doLogin(String user, char[] password) {
		login.setVisible(false);
		login.dispose();
		login = null;
		//frame.removeAll();
		if(reportList==null)
			reportList = new ReportList(frame, this);
		
		//reportList.show();
		
	}
	
	private Main(){
		frame = new JFrame();
		login = new LoginDialog(this);
		login.setVisible(true);
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void newReport() {
		new Dictation(null,this);
	}

	@Override
	public void openReport(Report r) {
		new Dictation(r, this);		
	}

	@Override
	public void showReport(Report r) {
		new ShowReport(r, this);		
	}
}
