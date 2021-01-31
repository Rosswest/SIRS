# SIRS
SIRS Model (with immunity) with GUI

The SIRS model simulates the spread of disease by cycling through three possible states:
  Susceptible -> Infected -> Recovered -> Susceptible
 
Each transition as its own probability.
  - S -> I : Infection
  - I -> R : Recovery
  - R -> S : Relapse

The rules are as follows:
 - Each step, a random cell is selected and advanced N seperate times
 - When advanced a cell behaves depending on its current state:
  - Susceptible: If a neighbour is infected, probability of changing to infected (I) is P(S->I)
  - Infected: Probability of changing to recovered (R) is P(I->R)
  - Recovered: Probability of changing to susceptible (S) is P(R->S)
  - Immune: Nothing
