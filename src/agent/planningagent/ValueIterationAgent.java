package agent.planningagent;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
import environnement.gridworld.ActionGridworld;
import environnement.gridworld.EtatGrille;
import environnement.gridworld.GridworldMDP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cet agent met a jour sa fonction de valeur avec value iteration et choisit
 * ses actions selon la politique calculee.
 *
 * @author laetitiamatignon
 *lien utile : http://burlap.cs.brown.edu/tutorials/cpl/p3.html
 */
public class ValueIterationAgent extends PlanningValueAgent {

	/**
	 * discount facteur
	 */
	protected double gamma;
	//*** VOTRE CODE

	protected Map<Etat, Double> v;

	/**
	 *
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma, MDP mdp) {

		super(mdp);
		this.gamma = gamma;
		v = new HashMap<Etat, Double>();

		//initialisation de toutes les valeurs de la grille à 0
		for (Etat e  : mdp.getEtatsAccessibles()) {
			v.put(e , 0.0);
		}

	}

	public ValueIterationAgent(MDP mdp) {
		this(0.9, mdp);

	}

	/**
	 *
	 * Mise a jour de V: effectue UNE iteration de value iteration
	 * Vpi(s) évalue l’intérêt sur le long terme de suivre une politique pi à partir de l’état s
	 * Dans chaque état choisit l’action qui va maximiser le retour espéré
	 */
	@Override
	public void updateV() {
		//delta est utilise pour detecter la convergence de l'algorithme
		//lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
		//delta < epsilon
		this.delta = 0.0;
		//*** VOTRE CODE


		List<Etat> etats = this.getMdp().getEtatsAccessibles();
		Map<Etat, Double> nouveauV = new HashMap<>();

		for (Etat e : etats) {

			//actions possibles
			List<Action> listeActions = this.getMdp().getActionsPossibles(e);

			Double meilleureAction = - Double.MAX_VALUE;

			for (Action a : listeActions) {

				try {
					Map<Etat, Double> listeEtatsTransitions = this.getMdp().getEtatTransitionProba(e, a);
					Double somme = 0.0;

					somme = sommeTransition(e, a, listeEtatsTransitions);

					if (somme > meilleureAction) {
						meilleureAction = somme;
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

			if (!mdp.estAbsorbant(e)) { nouveauV.put(e, meilleureAction); }

		}

		//mise a jour de delta
		//convergence
		Double deltaTmp;
		Double ancienneV = 0.0;
		Double nouvelleV = 0.0;

		for (Etat e : etats ) {
			ancienneV = getValeur(e);
			if (nouveauV.get(e) != null) nouvelleV = nouveauV.get(e);
			deltaTmp = Math.abs(ancienneV - nouvelleV);

			if (this.delta < deltaTmp) {
				this.delta = deltaTmp;
			}
		}

		v = nouveauV;



		// mise a jour vmax et vmin pour affichage du gradient de couleur:
		//vmax est la valeur de max pour tout s de V
		//JAVA 8
		super.vmax = v.entrySet().stream().mapToDouble(e -> e.getValue()).max().getAsDouble();
		super.vmin = v.entrySet().stream().mapToDouble(e -> e.getValue()).min().getAsDouble();


		//******************* a laisser a la fin de la methode
		this.notifyObs();
	}

	private Double sommeTransition(Etat e, Action a, 
			Map<Etat, Double> listeEtatsTransitions) {
		Double somme=0.0;

		for (Map.Entry<Etat, Double> s : listeEtatsTransitions.entrySet()) {

			Etat suivant = s.getKey();
			Double transition = s.getValue();
			double recompense = this.getMdp().getRecompense(e, a, s.getKey());
			somme += transition * (recompense + this.gamma * getValeur(suivant));
		}
		return somme;
	}

	/**
	 * renvoi l'action executee par l'agent dans l'etat e
	 */
	@Override
	public Action getAction(Etat e) {

		if (!this.getPolitique(e).isEmpty())  {
			return this.getPolitique(e).get(0);
		}
		else return ActionGridworld.NONE;

	}

	@Override
	public double getValeur(Etat _e) {

		if (v.containsKey(_e)) {
			return v.get(_e);
		}
		return 0.0;
	}

	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si
	 * aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {

		List<Action> meilleursActions = new ArrayList<Action>();
		List<Action> listeActionsPossibles = this.getMdp().getActionsPossibles(_e);
		Map<Action, Double> calculs = new HashMap <Action, Double>();

		//*** VOTRE CODE

		for(Action a : listeActionsPossibles ) {

			try {
				Map<Etat, Double> valeursTransitions = this.getMdp().getEtatTransitionProba(_e, a);

				Double somme = sommeTransition(_e, a, valeursTransitions);
				calculs.put(a, somme);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		double valeurMeilleurAction = - Double.MAX_VALUE;

		for(Map.Entry<Action, Double> action : calculs.entrySet())
		{
			if(action.getValue() > valeurMeilleurAction)
			{
				valeurMeilleurAction = action.getValue();
				meilleursActions.clear();
				meilleursActions.add(action.getKey());
			}

			else if(action.getValue() == valeurMeilleurAction)
			{
				meilleursActions.add(action.getKey());
			}
		}

		return meilleursActions;

	}

	@Override
	public void reset() {
		super.reset();

		v.clear();

		List<Etat> etats = this.getMdp().getEtatsAccessibles();

		for (Etat e : etats) {
			this.v.put(e, 0.0);
		}
		/*-----------------*/
		this.notifyObs();

	}

	@Override
	public void setGamma(double arg0) {
		this.gamma = arg0;
	}

}
