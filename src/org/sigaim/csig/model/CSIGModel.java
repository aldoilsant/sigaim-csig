package org.sigaim.csig.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.sigaim.csig.view.ReportList;
import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.clients.IntSIIE004ReportManagementClient;
import org.sigaim.siie.clients.IntSIIEReportSummary;
import org.sigaim.siie.clients.ws.WSIntSIIE001EQLClient;
import org.sigaim.siie.clients.ws.WSIntSIIE003TerminologiesClient;
import org.sigaim.siie.clients.ws.WSIntSIIE004ReportManagementClient;
import org.sigaim.siie.dadl.DADLManager;
import org.sigaim.siie.dadl.OpenEHRDADLManager;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.Cluster;
import org.sigaim.siie.iso13606.rm.Composition;
import org.sigaim.siie.iso13606.rm.EHRExtract;
import org.sigaim.siie.iso13606.rm.Element;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.HealthcareFacility;
import org.sigaim.siie.iso13606.rm.HealthcareProfessionalRole;
import org.sigaim.siie.iso13606.rm.II;
import org.sigaim.siie.iso13606.rm.INT;
import org.sigaim.siie.iso13606.rm.Item;
import org.sigaim.siie.iso13606.rm.Performer;
import org.sigaim.siie.iso13606.rm.ST;
import org.sigaim.siie.iso13606.rm.SubjectOfCare;
import org.sigaim.siie.rm.ReferenceModelManager;
import org.sigaim.siie.rm.ReflectorReferenceModelManager;
import org.sigaim.siie.rm.exceptions.ReferenceModelException;
import org.sigaim.siie.rm.exceptions.RejectException;
import org.sigaim.siie.dadl.OpenEHRDADLManager;

public class CSIGModel implements IntCSIGModel {

	private IntSIIE001EQLClient eqlClient;
	private IntSIIE004ReportManagementClient reportClient;
	private WSIntSIIE003TerminologiesClient termiClient;
	private DADLManager dadlManager;
	private ReferenceModelManager model;
	
	public CSIGModel(String url) {
		eqlClient = new WSIntSIIE001EQLClient(url+"/services/INTSIIE001EQLImplService");
		reportClient = new WSIntSIIE004ReportManagementClient(url+"/services/INTSIIE004ReportManagementImplService");
		termiClient = new WSIntSIIE003TerminologiesClient(url+"/services/INTSIIE003TerminologiesImplService");
		dadlManager= new OpenEHRDADLManager();
		model = new ReflectorReferenceModelManager(dadlManager);
	}	
	
	@Override
	public List<Report> getReports() {
		ArrayList<Report> rtn = new ArrayList<Report>();
		try {
			List<IntSIIEReportSummary> list = eqlClient.getAllReportSummaries();
			for(IntSIIEReportSummary rs : list){
				Report r = new Report(this);
				r.setCreation(rs.getCreationDate().toGregorianCalendar());
				r.setPatient(new CSIGPatient(rs.getSubject()));
				r.setFacultative(rs.getPerformer().getRoot() + "/" + rs.getPerformer().getExtension());
				r.setId(rs.getId());
				rtn.add(r);
			}
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return rtn;
	}
	
	

	@Override
	public Report fillSoip(Report report) {
		List<Element> soip = new ArrayList<Element>();
		String bias=null, unbias=null, impress=null, plan=null;
		
		try {
			soip = eqlClient.getReportSoip(report.getId());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		
		for(Element e : soip){
			String code = e.getMeaning().getCode();
			if(code.equals("at0003"))
				bias = ((ST)e.getValue()).getValue();
			else if(code.equals("at0004"))
				unbias = ((ST)e.getValue()).getValue();
			else if(code.equals("at0005"))
				impress = ((ST)e.getValue()).getValue();
			else if(code.equals("at0006"))
				plan = ((ST)e.getValue()).getValue();
		}
		
		
		if(bias != null)
			report.setBiased(bias);
		else
			report.setBiased("");
		
		if(unbias != null)
			report.setUnbiased(unbias);
		else
			report.setUnbiased("");
		
		if(impress != null)
			report.setImpressions(impress);
		else
			report.setImpressions("");
		
		if(plan != null)
			report.setPlan(plan);
		else
			report.setPlan("");			
		
		return report;
	}


	@Override
	public boolean checkLoginInfo(long user, String centre, char[] password) {
		try {
			return eqlClient.getUserExists(user);
		} catch (RejectException re) {
			System.out.println("Error connecting");
			re.printStackTrace();
			return false;
		}
	}


	@Override
	public List<String> getFacilities() {
		ArrayList<String> rtn = new ArrayList<String>();
		List<HealthcareFacility> facs;
		try {
			facs = eqlClient.getAllHealthcareFacilities();
			for(HealthcareFacility f:facs) {
				rtn.add(f.getIdentifier().getRoot()+"/"+f.getIdentifier().getExtension());
			}
		} catch (RejectException re) {
			re.printStackTrace();
			System.out.print("Connection refused");
		}	
		return rtn;
	}

	@Override
	public List<CSIGPatient> getPatients() {
		ArrayList<CSIGPatient> rtn = new ArrayList<CSIGPatient>();
		try {
			List<SubjectOfCare> subjects = eqlClient.getAllSubjectsOfCare();
			for(SubjectOfCare s : subjects)
				rtn.add(new CSIGPatient(s.getIdentifier()));
		} catch (RejectException e) {
			e.printStackTrace();
		}
		
		return rtn;
	}

	@Override
	public List<CSIGFacultative> getFacultatives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSIGFacultative createFacultative() {
		try {
			Performer p = reportClient.createPerformer("");
			return new CSIGFacultative(p.getIdentifier());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CSIGFacility createFacility() {
		try {
			HealthcareFacility fac = reportClient.createHealthcareFacility("");
			return new CSIGFacility(fac.getIdentifier());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CSIGPatient createPatient() {
		try {
			EHRExtract subject = reportClient.createSubjectOfCare("");
			return new CSIGPatient(subject.getSubjectOfCare());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return null;
	}	

	
	private class ConceptsOrderer implements Comparator<CSIGConcept> {
	    @Override
	    public int compare(CSIGConcept o1, CSIGConcept o2) {
	        return Integer.compare(o1.start, o2.start);
	    }
	}
	
	@Override
	public Report fillSoipConcepts(Report report) {
		
		  /* CLUSTER[at0008] occurrences matches {0..1} matches {  -- Lista de elementos
               parts existence matches {0..1} cardinality matches {0..*; ordered} matches {
                   CLUSTER[at0009] occurrences matches {0..*} matches {  -- Elemento
                       parts existence matches {0..1} cardinality matches {0..5; ordered; unique} matches {
                           ELEMENT[at0010] occurrences matches {0..1} matches {  -- Código
                               value existence matches {0..1} matches {
                                   ST occurrences matches {0..1} matches {  
                                       value existence matches {0..1} matches {*}
                                   }
                               }
                           }
                           ELEMENT[at0011] occurrences matches {0..1} matches {  -- Inicio
                               value existence matches {0..1} matches {
                                   INT occurrences matches {0..1} matches {  
                                       value existence matches {1..1} matches {*}
                                   }
                               }
                           }
                           ELEMENT[at0012] occurrences matches {0..1} matches {  -- Fin
                               value existence matches {0..1} matches {
                                   INT occurrences matches {0..1} matches {  
                                       value existence matches {1..1} matches {*}
                                   }
                               }
                           }
                           ELEMENT[at0013] occurrences matches {0..1} matches {  -- Path
                               value existence matches {0..1} matches {
                                   ST occurrences matches {0..1} matches {  -- ST
                                       value existence matches {0..1} matches {*}
                                   }
                               }
                           }
                           ELEMENT[at0014] occurrences matches {0..1} matches {  -- Nodo
                               value existence matches {0..1} matches {
                                   ST occurrences matches {0..1} matches {  
                                       value existence matches {0..1} matches {*}
                                   }
                               }
                           }
                           ELEMENT[at0015] occurrences matches {0..1} matches {  -- Terminología
                               value existence matches {0..1} matches {
                                   ST occurrences matches {0..1} matches {  
                                       value existence matches {0..1} matches {*}
                                   }
                               }
                           }
                       }
                       structure_type existence matches {1..1} matches {
                           STRUCTURE_TYPE occurrences matches {1..1} matches {
                               value existence matches {0..1} matches {"list"}
                           }
                       }
                   }
               }
               structure_type existence matches {1..1} matches {
                   STRUCTURE_TYPE occurrences matches {1..1} matches { 
                       value existence matches {0..1} matches {"list"}
                   }
               }*/
		Cluster concepts = null;
		//int textPosition = 0;
		//Magic number 23: offset due to non printed archetype nomenclature
		int biasEnd = report.getBiased().length()+23;
		int unbiasEnd = biasEnd + report.getUnbiased().length();
		int impresEnd = unbiasEnd + report.getImpressions().length();
		
		ArrayList<CSIGConcept> biasConcepts = new ArrayList<CSIGConcept>();
		ArrayList<CSIGConcept> unbiasConcepts = new ArrayList<CSIGConcept>();
		ArrayList<CSIGConcept> impresConcepts = new ArrayList<CSIGConcept>();
		ArrayList<CSIGConcept> planConcepts = new ArrayList<CSIGConcept>();
		
		HashSet<String> conceptsCDCV = new HashSet<String>(); //to get synonyms
		HashMap<String, List<CSIGConcept>> synonyms =
				new HashMap<String, List<CSIGConcept>>();
		
		report.setBiasedConcepts(biasConcepts);
		report.setUnbiasedConcepts(unbiasConcepts);
		report.setImpressionsConcepts(impresConcepts);
		report.setPlanConcepts(planConcepts);
		
		try {
			concepts = eqlClient.getConceptInformationForReportId(report.getII());
		} catch (RejectException e) {
			e.printStackTrace();
			return null;
		}
		if(concepts != null)
		for(Item i : concepts.getParts()) {			
			Cluster conceptCluster = (Cluster)i;
			List<Item> params = conceptCluster.getParts();
			CDCV code = (CDCV)((Element)params.get(0)).getValue();
			INT start = (INT)((Element)params.get(1)).getValue();
			INT end = (INT)((Element)params.get(2)).getValue();
			
			try {
				//FIXME: not here but exception if no synonyms found
				if(!code.getCodeSystemName().equals("SIGAIM"))
				conceptsCDCV.add(dadlManager.serialize(model.unbind(code),false));
				
			} catch (ReferenceModelException e) {
				System.err.println("Error serializing CDCV concept");
				e.printStackTrace();
			}
			
			//ST term = (ST)((Element)params.get(5)).getValue();
			if(start.getValue() < biasEnd)
				biasConcepts.add(new CSIGConcept(code,
						start.getValue()-23, end.getValue()-23));
			else if (start.getValue() < unbiasEnd)
				unbiasConcepts.add(new CSIGConcept(code,
						start.getValue()-biasEnd-1, end.getValue()-biasEnd-1));
			else if(start.getValue() < impresEnd)
				impresConcepts.add(new CSIGConcept(code, 
						start.getValue()-unbiasEnd-2, end.getValue()-unbiasEnd-2));
			else
				planConcepts.add(new CSIGConcept(code,
						start.getValue()-impresEnd-3, end.getValue()-impresEnd-3));
		}
		
		ConceptsOrderer orderer = new ConceptsOrderer();
		Collections.sort(biasConcepts, orderer);
		Collections.sort(unbiasConcepts, orderer);
		Collections.sort(impresConcepts, orderer);
		Collections.sort(planConcepts, orderer);
		
		try {
			Map<CDCV,Set<CDCV>> syn=termiClient.getSynonymsForConcepts("", new ArrayList<String>(conceptsCDCV));
			for(Entry<CDCV,Set<CDCV>> entry : syn.entrySet()) {
				List<CSIGConcept> list = new ArrayList<CSIGConcept>();
				
				//synonyms.add(entry.getKey().toString(), )
				System.out.println("Synonyms for: "+entry.getKey().getCodeSystemName()+entry.getKey().getCode());
				for(CDCV synonym : entry.getValue()) {
					System.out.println(">>>"+synonym.getCodeSystemName()+" "+synonym.getCode()+ " ("+synonym.getDisplayName().getValue()+")");
					list.add(new CSIGConcept(synonym));
				}
				synonyms.put(CSIGConcept.getConceptId(entry.getKey()), list);
			}
			report.setSynonyms(synonyms);
		} catch (RejectException e) {
			System.err.println("Error getting synonyms for Report");
			e.printStackTrace();
		}
			
		return report;
	}
	
	@Override
	public II getEHRIdFromPatient(long patientId){
		try {
			return eqlClient.getEHRIdFromSubject(patientId);
		} catch (RejectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Report createReport(String bias, String unbias, String impressions,
			String plan, FunctionalRole composer, II ehrId, CDCV status) {
		String text = "Zona Subjetivo. "+bias+" Zona Objetivo. "+unbias+" Zona Impresión. "+impressions+
				" Zona Plan. "+plan;
		Composition newReport = null;
		try {
			II rootArchetypeId= new II();
			rootArchetypeId.setRoot("CEN-EN13606-COMPOSITION.InformeClinicoNotaSOIP.v1");
			newReport = reportClient.createReport("createReport", ehrId, composer, text, true, rootArchetypeId);
		} catch (RejectException e) {
			e.printStackTrace();
		}
		if(newReport != null) {
			Report r = new Report(this);
			r.setPatient(new CSIGPatient(ehrId));
			r.setFacultative(composer.getPerformer().getRoot() + "/" + composer.getPerformer().getExtension());
			r.setId(newReport.getRcId());
			return r;
		} else return null;
	}

}
