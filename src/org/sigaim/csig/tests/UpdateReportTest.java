package org.sigaim.csig.tests;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.List;
import org.junit.Test;
import org.openehr.am.parser.ContentObject;
import org.sigaim.csig.model.CSIGConcept;
import org.sigaim.csig.model.CSIGModel;
import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.model.IntCSIGModel;
import org.sigaim.siie.dadl.DADLManager;
import org.sigaim.siie.dadl.OpenEHRDADLManager;
import org.sigaim.siie.iso13606.rm.Cluster;
import org.sigaim.siie.rm.ReferenceModelManager;
import org.sigaim.siie.rm.ReflectorReferenceModelManager;

public class UpdateReportTest {

	@Test
	public void testCreateReportSerialization() throws Exception {
		IntCSIGModel csig = new CSIGModel("http://sigaim.siie.cesga.es:8080/SIIEWS3");
		List<CSIGReport> reports = csig.getReports();
		CSIGReport r = reports.get(0);
		System.out.println("Report: "+r.getId());
		csig.fillSoip(r);
		csig.fillSoipConcepts(r);
		for(CSIGConcept c : r.getBiasedConcepts()){
			List<CSIGConcept> synonyms = r.getSynonyms().get(c.getConceptId());
			if(synonyms.size() > 1) {
				c.replace(synonyms.get(1));
			}
		}
		
		Cluster newConcepts = csig.conceptsToCluster(r);
		
		DADLManager dadlManager = new OpenEHRDADLManager();
		ReferenceModelManager model = new ReflectorReferenceModelManager(dadlManager);
		
		ContentObject obj = model.unbind(newConcepts);
		String text = dadlManager.serialize(obj, false);
		
		ContentObject obj2 = dadlManager.parseDADL(new ByteArrayInputStream(text.getBytes()));
		Cluster c = (Cluster)model.bind(obj2);
		
		
		PrintWriter out = new PrintWriter("UpateReportTestDADL.txt");
		
		out.println(text);
	}
}
