package uo.asw.tests.cucumber.steps;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uo.asw.InciDashboardE5bApplication;
import uo.asw.dbManagement.DBManagementFacade;
import uo.asw.dbManagement.model.Filter;
import uo.asw.dbManagement.model.Incidence;

@ContextConfiguration(classes=InciDashboardE5bApplication.class, loader=SpringApplicationContextLoader.class)
@SpringBootTest
public class MarcarIncidenciasComoPeligrosasPorTagsSteps {
	
	@Autowired
	private DBManagementFacade dbManagement;
	
	private Incidence[] incidences = new Incidence[3];
	
	private String tag;
	
	// Escenario 1: Marcar incidencias con tags como peligrosas
	
	@Given("^una lista de incidencias:$")
	public void una_lista_de_incidencias(List<IncidenceWith2TagsData> incidencesData) throws Throwable {
	    int i = 0;
		for (IncidenceWith2TagsData incidenceData : incidencesData) {
	    		// Guardamos los tags en un set
	    		Set<String> tags = new HashSet<>();
	    		tags.add(incidenceData.tag1);
	    		tags.add(incidenceData.tag2);
	    		
	    		// Creamos una incidencia con esa informacion y la guardamos en la lista
	    		incidences[i] = new Incidence(incidenceData.name, incidenceData.description, tags);
	    		i++;
		}
	}

	@Given("^el tag \"([^\"]*)\"$")
	public void el_tag(String tag) throws Throwable {
	    this.tag = tag;
	}

	@When("^el filtro se configura para marcar como peligrosas las incidencias que contengan el tag$")
	public void el_filtro_se_configura_para_marcar_como_peligrosas_las_incidencias_que_contengan_el_tag() throws Throwable {
	    // Cargamos el filtro de la BD
		Filter filter = dbManagement.getFilter();
	    
		// Lo configuramos para marcar como peligrosas las incidencias que contenga el tag indicado
	    filter.setFilterResponse("markAsDangerous").
			setApplyOn("tag").
			setFilterOperation("contains").
			setTag(tag);
	    
	    // Salvamos el filtro en BD
	    dbManagement.updateFilter(filter);
	}
	
	@When("^se aplica el filtro sobre la lista de incidencias$")
	public void se_aplica_el_filtro_sobre_la_lista_de_incidencias() throws Throwable {
		// Recuperamos el filtro de la BD
		Filter filter = dbManagement.getFilter();
		
		// Aplicamos el filtro a cada una de las incidencias
		for (int i = 0; i < incidences.length; i++) {
			incidences[i] = filter.applyFilter(incidences[i]);
		}
	}

	@Then("^solamente la incidencia inc(\\d+) es marcada como peligrosa$")
	public void solamente_la_incidencia_inc_es_marcada_como_peligrosa(int arg1) throws Throwable {
		// Revisamos los nombres de las incidencias
		assertEquals("inc1", incidences[0].getName());
		assertEquals("inc2", incidences[1].getName());
		assertEquals("inc3", incidences[2].getName());
		
		// Comprobamos que la unica incidencia marcada como peligrosa sea la que se llama inc1
		assertEquals(true, incidences[0].isDangerous());
		assertEquals(false, incidences[1].isDangerous());
		assertEquals(false, incidences[2].isDangerous());
	}
	
	/**
	 * Clase auxiliar para recoger los datos de las incidencias con tags del feature
	 * @author carlos
	 */
	public static class IncidenceWith2TagsData {
		public String name;
		public String description;
		public String tag1;
		public String tag2;
	}
	
}
