package sirs;

public class SIRSModel {
	private int width; //lattice width
	private int height; //lattice height
	private int ticks; //number of cell updates that have been run
	private double infectionProbability; //probability of S -> I
	private double recoveryProbability; // probability of I -> R
	private double relapseProbability; // probability of R -> S
	private double immuneProportion; //proportion of cells immune to infection
	private double infectedProportion = 0.1; //initial proportion of cells which are infected
	private State[][] agents; //cells representing an individual
	private CellShape cellShape; //Graphical representation of cells
	
	/**
	 * Defaults
	 */
	public static final CellShape DEFAULT_CELL_SHAPE = CellShape.RECTANGULAR;
	public static final double DEFAULT_INFECTION_PROBABILITY = 0.5;
	public static final double DEFAULT_RECOVERY_PROBABILITY = 0.5;
	public static final double DEFAULT_RELAPSE_PROBABILITY = 0.5;

	/**
	 * Initialises SIRS Lattice of given width and height, with immunity enabled.
	 * Probability of Infected, Recovery and Relapse are set to default.
	 * @param width Width of Lattice
	 * @param height Height of Lattice
	 * @param immuneProportion Proportion of population who are immune
	 */
	public SIRSModel(int width, int height, double immuneProportion) {
		this.width = width;
		this.height = height;
		this.immuneProportion = immuneProportion;
		recoveryProbability = DEFAULT_RECOVERY_PROBABILITY;
		infectionProbability= DEFAULT_INFECTION_PROBABILITY;
		relapseProbability = DEFAULT_RELAPSE_PROBABILITY;
		cellShape = DEFAULT_CELL_SHAPE;
		initialise();
	}

	/**
	 * Returns Enum representing graphical shape of cells
	 * @return
	 */
	public CellShape getCellShape() {
		return cellShape;
	}

	public void setCellShape(CellShape cellShape) {
		this.cellShape = cellShape;
	}

	/**
	 * Sets initial proportion of immune agents.
	 * @return
	 */
	public void setImmuneProportion(double immuneProportion) {
		this.immuneProportion = immuneProportion;
	}

	/**
	 * Returns proportion of immune individuals.
	 * @return
	 */
	public double getImmuneProprtion() {
		return immuneProportion;
	}

	/**
	 * Initialises SIRS Lattice of given width and height, with immunity enabled.
	 * Probability of Infected, Recovery and Relapse are set to provided values.
	 * @param width Width of Lattice
	 * @param height Height of Lattice
	 * @param infectionProbability probability of S -> I
	 * @param recoveryProbabilityprobability of I -> R
	 * @param relapseProbability probability of R -> S
	 * @param immuneProportion Proportion of agents who are immune
	 */
	public SIRSModel(int width, int height, double infectionProbability, double recoveryProbability, double relapseProbability, double immuneProportion) {
		this.width = width;
		this.height = height;
		this.infectionProbability = infectionProbability;
		this.recoveryProbability = recoveryProbability;
		this.relapseProbability = relapseProbability;
		this.immuneProportion = immuneProportion;
		cellShape = DEFAULT_CELL_SHAPE;
		initialise();
	}

	/**
	 * Initialises a SIRS Lattice of given height and width based on the contents of a previous lattice, with immunity enabled.
	 * If lattice size does not match the given width and height, errors will occur during calculation.
	 * @param width Width of Lattice
	 * @param height Height of Lattice
	 * @param agents 2D array of State[] representing the contents of a previous lattice.
	 * @param immuneProportion
	 */
	public SIRSModel(int width, int height, State[][] agents, double immuneProportion) {
		this.width = width;
		this.height = height;
		this.agents = agents;
		this.immuneProportion = immuneProportion;
		recoveryProbability = DEFAULT_RECOVERY_PROBABILITY;
		infectionProbability= DEFAULT_INFECTION_PROBABILITY;
		relapseProbability = DEFAULT_RELAPSE_PROBABILITY;
		cellShape = DEFAULT_CELL_SHAPE;
	}


	/**
	 * Initial setup of lattice.
	 */
	private void initialise() {
		ticks = 0;
		agents = new State[height][width];
		reset();
	}

	/**
	 * Return probability of S->I
	 * @return
	 */
	public double getInfectionProbability() {
		return infectionProbability;
	}

	/**
	 * Set probability of S->I
	 * @param infectionProbability
	 */
	public void setInfectionProbability(double infectionProbability) {
		this.infectionProbability = infectionProbability;
	}

	/**
	 * Return probability of I->R
	 * @return
	 */
	public double getRecoveryProbability() {
		return recoveryProbability;
	}

	/**
	 * Set probability of I->R
	 * @param recoveryProbability
	 */
	public void setRecoveryProbability(double recoveryProbability) {
		this.recoveryProbability = recoveryProbability;
	}

	/**
	 * Return lattice width
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return lattice height.
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return 2D array representing population.
	 * @return
	 */
	public State[][] getAgents() {
		return agents;
	}
	
	/**
	 * Return String representation of lattice.
	 */
	public String toString() {
		String out = "";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				out += agents[i][j] + " ";
			}
			out += "\r\n";
		}
		return out;
	}

	/**
	 * Return HTML friendly String representation of lattice.
	 * Useful for JLabels.
	 * @return
	 */
	public String toHTMLString() {
		String out = "<HTML>";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				out += agents[i][j] + " ";
			}
			out += "<BR>";
		}
		out += "</HTML>";
		return out;
	}

	/**
	 * Immunizes a random cell.
	 */
	public void immunizeRandomCell() {
		boolean infected = false;
		while (!infected) {
			int x = Randoms.randomInt(0,width-1);
			int y = Randoms.randomInt(0,height-1);
			if (agents[y][x] != State.IMMUNE) {
				agents[y][x] = State.IMMUNE;
				infected = true;
			}
		}
	}
	
	/**
	 * Infects a random cell.
	 */
	public void infectRandomCell() {
		boolean infected = false;
		while (!infected) {
			int x = Randoms.randomInt(0,width-1);
			int y = Randoms.randomInt(0,height-1);
			if (agents[y][x] != State.RECOVERED || agents[y][x] != State.INFECTED) {
				agents[y][x] = State.INFECTED;
				infected = true;
			}
		}
	}

	/**
	 * Updates all agents based on their current neighbours.
	 */
	public void updateParallel() {
		State[][] nextState = new State[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				nextState[i][j] = agents[i][j];
				State actor = agents[i][j];
				if (actor == State.SUSCEPTIBLE) {
					if (actor != State.IMMUNE) {
						if (actor == State.SUSCEPTIBLE) {
							//check for infection chance
							State[] adjacentStates = getAdjacentStates(j,i);

							boolean infectionChance = false;
							for (State state : adjacentStates) {
								if (state == State.INFECTED) {
									infectionChance = true;
								}
							}

							if (infectionChance) {
								double r = Math.random();

								if (r <= infectionProbability) {
									nextState[i][j] = State.INFECTED;
								}
							}
						} else if (actor == State.RECOVERED) {
							double r = Math.random();

							if (r <= relapseProbability) {
								nextState[i][j] = State.SUSCEPTIBLE;
							}
						} else {
							double r = Math.random();

							if (r <= recoveryProbability) {
								nextState[i][j] = State.RECOVERED;
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				agents[i][j] = nextState[i][j];
			}
		}
		ticks++;

	}

	/**
	 * Updates a single agent based on its current neighbours.
	 */
	public void updateSequential() {
		int x = Randoms.randomInt(0,width-1);
		int y = Randoms.randomInt(0,height-1);
		State actor = agents[y][x];
		if (actor != State.IMMUNE) {
			if (actor == State.SUSCEPTIBLE) {
				//check for infection chance
				State[] adjacentStates = getAdjacentStates(x,y);

				boolean infectionChance = false;
				for (State state : adjacentStates) {
					if (state == State.INFECTED) {
						infectionChance = true;
					}
				}

				if (infectionChance) {
					double r = Math.random();

					if (r <= infectionProbability) {
						agents[y][x] = State.INFECTED;
					}
				}
			} else if (actor == State.RECOVERED) {
				double r = Math.random();

				if (r <= relapseProbability) {
					agents[y][x] = State.SUSCEPTIBLE;
				}
			} else {
				double r = Math.random();

				if (r <= recoveryProbability) {
					agents[y][x] = State.RECOVERED;
				}
			}
		}
		ticks++;
	}

	/**
	 * Return number of transitions attempted.
	 * @return
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * Return an array of States representing the adjacent states of a given x,y in the lattice.
	 * @param x X position of the agent in the lattice
	 * @param y Y position of the agent in the lattice
	 * @return
	 */
	public State[] getAdjacentStates(int x, int y) {
		State leftState = null;
		State rightState = null;
		State bottomState = null;
		State topState = null;
		int stateCount = 0;
		int statesFilled = 0;

		if (x == 0) {
			//fill rightState only
			rightState = agents[y][x+1];
			stateCount += 1;
		} else if (x == width-1){
			//fill leftState only
			leftState = agents[y][x-1];
			stateCount += 1;
		} else{
			//fill both leftState and rightState
			leftState = agents[y][x-1];
			rightState = agents[y][x+1];
			stateCount += 2;
		}

		if (y == 0) {
			//fill topState only
			topState = agents[y+1][x];
			stateCount += 1;
		} else if (y == height-1) {
			//fill bottomState only
			bottomState = agents[y-1][x];
			stateCount += 1;
		} else {
			//fill both bottomState and topState
			topState = agents[y+1][x];
			bottomState = agents[y-1][x];
			stateCount += 2;
		}



		State[] adjacentStates = new State[stateCount];

		if (leftState != null) {
			adjacentStates[statesFilled] = leftState;
			statesFilled++;
		}

		if (rightState != null) {
			adjacentStates[statesFilled] = rightState;
			statesFilled++;
		}

		if (topState != null) {
			adjacentStates[statesFilled] = topState;
			statesFilled++;
		}

		if (bottomState != null) {
			adjacentStates[statesFilled] = bottomState;
			statesFilled++;
		}

		return adjacentStates;		
	}

	/**
	 * Return the state at a given x,y in the lattice.
	 * @param x X position in the lattice
	 * @param y Y position in the lattice
	 * @return
	 */
	public State getState(int x, int y) {
		return agents[y][x];
	}


	/**
	 * Return probability of R->S
	 * @return
	 */
	public double getRelapseProbability() {
		return relapseProbability;
	}

/**
 * Set probability of R->S
 * @param relapseProbability
 */
	public void setRelapseProbability(double relapseProbability) {
		this.relapseProbability = relapseProbability;
	}


	/**
	 * Sets up the lattice to its initial state by making all sites susceptible, then applying number of S,I and R sites based on proportions.
	 */
	public void reset() {
		ticks = 0;
		int immuneCount = (int) ((width * height) * immuneProportion);
		int infectedCount = (int)((width * height) * infectedProportion);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				agents[i][j] = State.SUSCEPTIBLE;
			}
		}

		for (int i = 0; i < infectedCount; i++) {
			this.infectRandomCell();
		}
		
		for (int i = 0; i < immuneCount; i++) {
			this.immunizeRandomCell();
		}
	}

	/**
	 * Return initial proportion of infected agents.
	 * @return
	 */
	public double getInfectedProportion() {
		return infectedProportion;
	}

	/**
	 * Set initial proportion of infected agents.
	 * @param infectedProportion
	 */
	public void setInfectedProportion(double infectedProportion) {
		this.infectedProportion = infectedProportion;
	}

	/**
	 * Return proportion of immune agents.
	 * @return
	 */
	public double getImmuneProportion() {
		return immuneProportion;
	}

	/**
	 * Sets the size of a square lattice, and re-initialises it.
	 * @param size
	 */
	public void setSize(int size) {
		width = size;
		height = size;
		initialise();
	}

}
