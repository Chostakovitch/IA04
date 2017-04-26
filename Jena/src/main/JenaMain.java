package main;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class JenaMain {
	public static void runSelectQuery(String queryPath, Model model) {
		Query query = QueryFactory.read(queryPath);
		QueryExecution qExec = QueryExecutionFactory.create(query, model);
		ResultSet r = qExec.execSelect();
		ResultSetFormatter.out(System.out, r);
		qExec.close();
	}
	
	public static void runDistantQuery(String queryPath, String endPoint) {
		Query query = QueryFactory.read(queryPath);
		configureProxy();
		QueryExecution qExec = QueryExecutionFactory.sparqlService(endPoint, query);
		ResultSet r = qExec.execSelect();
		ResultSetFormatter.out(System.out, r);
		qExec.close();
	}
	
	public static void configureProxy() {
		System.setProperty("http.proxyHost", "proxyweb.utc.fr");
		System.setProperty("http.proxyPort", "3128");
	}
	
	public static void main(String[] args) {
		String ontoPath = JenaMain.class.getResource("/foaf.n3").getPath();
		String queryPath = JenaMain.class.getResource("/q4.sparql").getPath();		
		String endPoint = "http://linkedgeodata.org/sparql";
		
		//Requêtes locales
		/*Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel(model, ontoPath);
		runSelectQuery(queryPath, model);*/
		
		//Requêtes distantes
		runDistantQuery(queryPath, endPoint);
	}
}
