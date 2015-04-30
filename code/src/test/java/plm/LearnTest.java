package plm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.hibernate.Criteria;
import org.hl7.v3.*;
public class LearnTest {
	
	public static enum PARAMS {
		ptBirthDate,
		ptMinBP
	}

	@Test
	public final void test() {
		Patient theTemplate = getTemplate();
		Patient thePt = getThePatient();
		Map<PARAMS,Object> paramValues = extractParameters(thePt, theTemplate);
		
		Criteria query = buildQuery(theTemplate,paramValues);
		
		List<Patient> patientsPotentiallyLikeMine = runQuery( query );
		
		for ( Patient p : patientsPotentiallyLikeMine ) {
			System.out.println( p.getIds().get(0) );
		}
	}
	
	private List<Patient> runQuery(Criteria query) {
		List<Patient> pats = query.list();
		return pats;
	}


	private Criteria buildQuery(Patient theTemplate,
								Map<PARAMS, Object> paramValues) {
		Criteria query = HibernateUtil.openSession().createCriteria( theTemplate.getClass() );
		return query;
	}

	private Patient getThePatient() {
		return new Patient();
	}

	public Patient getTemplate() {
		Patient p = new Patient()
			.withIds( new II().withRoot( "mc").withExtension( "123" ), 
					  new II().withRoot( "az" ).withExtension( "00-123" ) )
			.withPlayer( new Person().withBirthTime( new TS().withValue( PARAMS.ptBirthDate.toString() ) ) )
		.withParticipations( new Participation()
								.withTypeCode( ParticipationType.SBJ )
								.withAct( new Observation()
											.withMoodCode( ActMood.DEF )
											.withClassCode( ActClass.OBS )
											.withCode( new CD().withCode("14-455").withCodeSystem("LOINC") )
											.withValues( new IVL_PQ()
															.withLow( new IVXB_PQ().withValue( PARAMS.ptMinBP.toString() ).withUnit("mmHg") )
															.withHigh( new IVXB_PQ().withValue("").withUnit("") ) )
											.withEffectiveTime( new IVL_TS() ) ) 
											);
			
		return p;
	}
	
	
	

	
	
	public Map<PARAMS,Object> extractParameters( Patient thePt, Patient template ) {
		Map<PARAMS,Object> map = new HashMap<PARAMS,Object>();
		
		for ( PARAMS p : PARAMS.values() ) {
			if ( contains(template,p) ) {
				Object value = extract( thePt, p, template );
				if ( value != null ) {
					map.put( p, value );
				}
			}
		}
		
		return map;		
	}

	private Object extract(Patient thePt, PARAMS p, Patient theTemplate) {
		switch( p ) {
		case ptBirthDate:
				return ((Person) thePt.getPlayer()).getBirthTime();
		case ptMinBP :
				PQ thePtBp = new PQ().withValue("100").withUnit("mmHg"); //TODO findMinBP(thePt,theTemplate);
				Double val = Double.valueOf(thePtBp.getValue()); 
				val = val * 0.8; // TODO adjust( val, theTemplate )
				return new PQ().withValue(val.toString()).withUnit("Hgmm");
		default:
			return null;
		}
	}

	private boolean contains(Patient template, PARAMS ptbirthdate) {
		return true;
	}
}
