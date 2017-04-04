package model;

/**
 * Modèle pour les ordres donnés par la simulation
 * à l'environnement. Encapsule un modèle d'enregistrement
 * contenant déjà le nom de l'agent et rajoute l'indice de
 * l'entité à traiter.
 */
public class SimulationModel extends Model {
	private RegisterModel agent;
	private int index;
	
	public SimulationModel() { }
	
	public SimulationModel(RegisterModel agent, int index) {
		this.agent = agent;
		this.index = index;
	}

	public RegisterModel getAgent() {
		return agent;
	}

	public void setAgent(RegisterModel agent) {
		this.agent = agent;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
