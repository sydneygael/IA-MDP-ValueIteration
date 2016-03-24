package agent.planningagent;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
import environnement.gridworld.ActionGridworld;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cet agent choisit une action aleatoire parmi toutes les autorisees dans
 * chaque etat
 *
 * @author lmatignon
 *
 */
public class AgentRandom extends PlanningValueAgent {

	public AgentRandom(MDP _m) {
		super(_m);
	}

	@Override
	public Action getAction(Etat e) {
		
		List<Action> listActions = this.getMdp().getActionsPossibles(e);

		if ( listActions.size() > 0) { 
			// on renvoie une action au hasard
			return listActions.get(this.rand.nextInt(listActions.size()));
		}
		else return null;
	}

	@Override
	public double getValeur(Etat _e) {
		return 0.0;
	}

	@Override
	public List<Action> getPolitique(Etat _e) {
		List<Action> list = this.getMdp().getActionsPossibles(_e);
		return list;
	}

	@Override
	public void updateV() {
		System.out.println("l'agent random ne planifie pas");
	}

	@Override
	public void setGamma(double parseDouble) {
		// TODO Auto-generated method stub

	}

}
