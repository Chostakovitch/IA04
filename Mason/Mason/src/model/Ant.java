package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;
import sim.util.Int2D;
import util.Constants;

public class Ant implements Steppable {
	private final int DISTANCE_DEPLACEMENT;
	private final int DISTANCE_PERCEPTION;
	private final int CHARGE_MAX;
	
	private int energy;
	private int effectiveCharge;
	private GridModel model;
	
	private Stoppable stop;
	
	private Random random = new Random();
	
	private Set<Int2D> foodFound = new HashSet<>();
	
	private int yDir = Constants.GRID_SIZE;
	
	private int number = 0;

	public Ant(int number) {
		DISTANCE_DEPLACEMENT = 3;
		DISTANCE_PERCEPTION = 6;
		CHARGE_MAX = 1;
		
		this.number = number;
		energy = Constants.MAX_ENERGY;
		effectiveCharge = 0;
	}
	
	@Override
	public void step(SimState state) {
		model = (GridModel) state;
		Int2D pos = model.getGrid().getObjectLocation(this);
		FoodGroup food = null;
		//Plus d'�nergie apr�s un d�placement
		if(energy == 0)
			suicide();
		
		//Si on peut charger de la nourriture, on la charge
		if(effectiveCharge < CHARGE_MAX && (food = hasFood(pos)) != null) {
			chargeResource(food);
		}
		
		//Si on a rien � charger et qu'on a une charge disponible et que l'�nergie est basse, on la consomme
		else if(energy < Constants.MAX_ENERGY - Constants.FOOD_ENERGY && effectiveCharge > 0) {
			consumeCharge();
		}
		
		/* Sinon, on bouge vers la nourriture la plus proche (effective ou sauvegard�e valide hors du champ de vision)
		 * ou, dans le cas o� aucune nourriture n'est disponible ou sauvegard�e, sur une ligne verticale avec des marges
		 * de DISTANCE_DEPLACEMENT et un d�placement en abscisses al�atoire'
		 */
		else {
			move(pos, getClosestFood(pos));
		}
	}
	
	//D�place la fourmi au plus pr�s de newPos (en fonction de DISTANCE_DEPLACEMENT)
	/**
	 * D�place la fourmi au plus pr�s de la position demand�e, selon la distance maximale
	 * @param oldPos Position actuelle de la fourmi
	 * @param newPos Position demand�e pour la fourmi
	 * @see DISTANCE_DEPLACEMENT
	 */
	private void move(Int2D oldPos, Int2D newPos) {
		int dist = Math.max(Math.abs(newPos.y - oldPos.y), Math.abs(newPos.x - oldPos.x));
		//Il faut trouver une nouvelle position proche de newPos
		if(dist > DISTANCE_DEPLACEMENT) {
			do {
				int x = newPos.x;
				int y = newPos.y;
				//La plus grande diff�rence est en ordonn�es, on diminue l'�cart � cet endroit
				if(Math.abs(newPos.y - oldPos.y) > Math.abs(newPos.x - oldPos.x))
					y += ((newPos.y - oldPos.y) > 0 ? -1 : 1);
				//R�ciproquement
				else 
					x+= ((newPos.x - oldPos.x) > 0 ? -1 : 1);
				newPos = new Int2D(x, y);
			} while(Math.max(Math.abs(newPos.y - oldPos.y), Math.abs(newPos.x - oldPos.x)) > DISTANCE_DEPLACEMENT);
		}
		//Le d�placement consomme de l'�nergie
		if(energy == 1) suicide();
		else {
			--energy;
			model.getGrid().setObjectLocation(this, newPos);
		}
	}
	
	/**
	 * Renvoie la position du groupe de nourriture le plus proche
	 * (au sens du d�placement, la diagonale comptant pour une unit�)
	 * de la position pass�e en param�tre.
	 * Met �galement � jour foodFound pour enlever les nourritures obsol�tes.
	 * @param pos Position centrale 
	 * @return Position du groupe de nourriture le plus proche,
	 * ou position al�atoire si la distance de perception ne permet pas de trouver un groupe de nourriture
	 */
	private Int2D getClosestFood(Int2D pos) {
		int radius = 1;
		int x = pos.x;
		int y = pos.y;
		Int2D newPos = null;
		boolean found = false;
		//Important : ne pas oublier de mettre � jour la liste sur la case actuelle
		if(hasFood(pos) == null && foodFound.contains(pos)) foodFound.remove(pos);
		//On incr�mente le rayon jusqu'� trouver de la nourriture (il en existe n�cessairement)
		while(radius < DISTANCE_PERCEPTION) {
			//C�t� gauche
			Int2D tempPos;
			for(int tempX = x - radius, tempY = y - radius; tempY < y + radius; ++tempY) {
				tempPos = new Int2D(tempX, tempY);
				if(hasFood(tempPos) != null) {
					if(found == false) {
						newPos = tempPos;
						found = true;
					} 
					else foodFound.add(tempPos);
				}
				//Cas de la sauvegarde d'une position maintenant obsol�te
				else if(foodFound.contains(tempPos)) foodFound.remove(tempPos);
			}
			//C�t� droit
			for(int tempX = x + radius, tempY = y - radius; tempY < y + radius; ++tempY) {
				tempPos = new Int2D(tempX, tempY);
				if(hasFood(tempPos) != null) {
					if(found == false) {
						newPos = tempPos;
						found = true;
					} 
					else foodFound.add(tempPos);
				}
				//Cas de la sauvegarde d'une position maintenant obsol�te
				else if(foodFound.contains(tempPos)) foodFound.remove(tempPos);
			}
			//C�t� haut
			for(int tempY = y - radius, tempX = x - radius; tempX < x + radius; ++tempX) {
				tempPos = new Int2D(tempX, tempY);
				if(hasFood(tempPos) != null) {
					if(found == false) {
						newPos = tempPos;
						found = true;
					} 
					else foodFound.add(tempPos);
				}
				//Cas de la sauvegarde d'une position maintenant obsol�te
				else if(foodFound.contains(tempPos)) foodFound.remove(tempPos);
			}
			//C�t� bas
			for(int tempY = y + radius, tempX = x - radius; tempX < x + radius; ++tempX) {
				tempPos = new Int2D(tempX, tempY);
				if(hasFood(tempPos) != null) {
					if(found == false) {
						newPos = tempPos;
						found = true;
					} 
					else foodFound.add(tempPos);
				}
				//Cas de la sauvegarde d'une position maintenant obsol�te
				else if(foodFound.contains(tempPos)) foodFound.remove(tempPos);
			}
			++radius;
		}
		
		//La fourmi a trouv� de la nourriture, on l'y envoie
		if(found) {
			return newPos;
		}
		
		//La fourmi n'a pas trouv� de la nourriture et se dirige vers la nourriture sauvegard�e la plus proche
		if(foodFound.size() != 0) {
			return getClosestSavedFood(pos);
		}
		
		/* La fourmi n'a aucune id�e d'o� aller, on la fait faire des ligne verticales avec une marge de sa distance de perception */
		if(yDir == Constants.GRID_SIZE && Constants.GRID_SIZE - y <= DISTANCE_PERCEPTION) yDir = 0;
		else if(yDir == 0 && y <= DISTANCE_PERCEPTION) yDir = Constants.GRID_SIZE;
		return new Int2D(random.nextInt(Constants.GRID_SIZE), yDir);
	}
	
	/**
	 * Retourne la position de nourriture sauvegard�e la plus proche
	 * @param pos Position actuelle
	 * @return Position sauvegard�e
	 * @see foodFound
	 */
	private Int2D getClosestSavedFood(Int2D pos) {
		Int2D result = null;
		int minDist = Integer.MAX_VALUE;
		for(Int2D newPos : foodFound) {
			int dist = Math.max(Math.abs(newPos.y - pos.y), Math.abs(newPos.x - pos.x));
			if(dist < minDist) {
				result = newPos;
				minDist = dist;
			}
		}
		return result;
	}
	
	/**
	 * Consomme une ressource sur la grille
	 * @param fg Groupe de nourriture duquel pr�lever
	 */
	private void eatResource(FoodGroup fg) {
		fg.decQuantity();
		energy += Constants.FOOD_ENERGY;
		checkAndRefill(fg);
	}
	
	/**
	 * Charge une ressource pr�sente sur la grille
	 * @param fg Groupe de nourriture duquel pr�lever
	 */
	private void chargeResource(FoodGroup fg) {
		fg.decQuantity();
		++effectiveCharge;
		checkAndRefill(fg);
	}
	
	/**
	 * Consomme une ressource stock�e pr�alablement
	 */
	private void consumeCharge() {
		effectiveCharge--;
		energy += Constants.FOOD_ENERGY;
	}
	
	/**
	 * V�rifie si le groupe de nourriture est vide, et si oui, le supprimer
	 * et en recr�e un autre � une position al�atoire
	 * @param fg Groupe de nourriture � v�rifier
	 */
	private void checkAndRefill(FoodGroup fg) {
		if(fg.empty()) {
			model.getGrid().remove(fg);
			model.getGrid().setObjectLocation(new FoodGroup(), new Int2D(random.nextInt(model.getGrid().getWidth()), random.nextInt(model.getGrid().getHeight())));
		}
	}
	
	/**
	 * Tue l'insecte courant : cas o� son �nergie vaut 0
	 */
	private void suicide() {
		model.getGrid().remove(this);
		model.decNumInsects();
		getStop().stop();
	}
	
	/**
	 * V�rifie si une case de la grille contient de la nourriture, 
	 * et si oui, renvoie le groupe associ�
	 * @param pos Position sur la grille 
	 * @return null si aucun groupe trouv�, FoodGroup sinon
	 */
	private FoodGroup hasFood(Int2D pos) {
		Bag bag = model.getGrid().getObjectsAtLocation(pos.x, pos.y);
		if(bag != null) {
			for(Object o : bag) {
				if(o instanceof FoodGroup) {
					return (FoodGroup)o;
				}
			}
		}
		return null;
	}

	public Stoppable getStop() {
		return stop;
	}

	public void setStop(Stoppable stop) {
		this.stop = stop;
	}

	@Override
	public String toString() {
		return "" + number + " " + energy + " " + effectiveCharge;
	}
	
	
}
