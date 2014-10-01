package org.sigaim.csig.tests;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observer;

import org.junit.Test;
import org.openehr.am.parser.ContentObject;
import org.sigaim.csig.model.CSIGModel;
import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.model.ModelConstants;
import org.sigaim.siie.clients.ws.WSIntSIIE004ReportManagementClient;
import org.sigaim.siie.dadl.DADLManager;
import org.sigaim.siie.dadl.OpenEHRDADLManager;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.Cluster;
import org.sigaim.siie.iso13606.rm.EHRExtract;
import org.sigaim.siie.iso13606.rm.Element;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.HealthcareFacility;
import org.sigaim.siie.iso13606.rm.II;
import org.sigaim.siie.iso13606.rm.Item;
import org.sigaim.siie.iso13606.rm.Performer;
import org.sigaim.siie.iso13606.rm.ST;
import org.sigaim.siie.rm.ReferenceModelManager;
import org.sigaim.siie.rm.ReflectorReferenceModelManager;
import org.sigaim.siie.rm.exceptions.RejectException;

import es.udc.tic.rnasa.sigaim_transcriptor_client.TranscriptionClientApiImpl;

public class CreateReportTest {

	@Test
	public void testCreateReportSerialization() throws Exception {
		CSIGReport r = new CSIGReport((CSIGModel)null);
		
		r.setBiased("Acude a urgencias: refiere sensación distérmica de 3 días evolución sin fiebre termometrada.\nMolestias faríngeas con tos improductiva.");
		r.setUnbiased("Buen estado general, consciente, orientado y colaborador, eupneico. Sin signos distrés respiratorio. No exantemas ni petequias. Bien hidratado y bien perfundido. Afebril 36 grados. Otorrinolaringología: Hiperemia faríngea sin evidencia de exudados. Auscultación cardiorrespiratoria: Murmullo vesicular conservado sin estertores.");
		r.setImpressions("Síndrome catarral.");
		r.setPlan("Hidratación + Ibuprofeno 600 (1-1-1) + Iniston antitusivo.");
		
		DADLManager dadlManager = new OpenEHRDADLManager();
		ReferenceModelManager model = new ReflectorReferenceModelManager(dadlManager);
		
		ContentObject obj = model.unbind(r.toCluster());
		
		String text = dadlManager.serialize(obj, false);
		
		System.out.println(text);
		
		ContentObject parsed = dadlManager.parseDADL(new ByteArrayInputStream(text.getBytes()));
		Cluster cluster1 = (Cluster)model.bind(parsed);
		//List<Item> items = cluster1.getParts();
		for(Item i : cluster1.getParts()){
			Element e = (Element)i;
			if(e.getMeaning().getCode().equals(ModelConstants.CD_BIAS)){
				System.out.println("Subjetivo: "+((ST)e.getValue()).getValue());
				
			} else if(e.getMeaning().getCode().equals(ModelConstants.CD_UNBIAS)) {
				System.out.println("Objetivo: "+((ST)e.getValue()).getValue());
			} else if(e.getMeaning().getCode().equals(ModelConstants.CD_IMPRESSION)) {
				System.out.println("Impresión: "+((ST)e.getValue()).getValue());
			} else if(e.getMeaning().getCode().equals(ModelConstants.CD_PLAN)) {
				System.out.println("Plan: "+((ST)e.getValue()).getValue());
			}
		}
		
	}
}
