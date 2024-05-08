# Analysing the Effectiveness of Honesty Stimulating Solutions in Online Decentralized Markets
## An Agent-based Simulation Comparison


Online transactions involve protocols of exchange of values where an active party usually prepays for goods or services. The passive party may act dishonestly and never deliver the paid for goods or services. Incomplete transactions cause multi-billion dollars losses annually just for e-commerce. Centralized markets bypass this problem with a centralized intermediary auditor - e.g., Amazon.com. For decentralized markets - e.g., OpenBazaar - literature and practice have typically adopted solutions built around security deposits, reputation systems, or decentralized arbitrators. This article carries out a comparison, by means of agent-based simulation (ABS), among such decentralized solutions in the literature and also, our proposals for suitable combinations of their main features. One of the proposed combinations offers superior performance in terms of transactions completion rate (success), even with high rates of dishonesty among the population. The article contributes to ABS applications to the prototyping of decentralized transaction protocols in untrustworthy online environments.

### COMPARED SOLUTIONS


This work proposes solutions to encourage honesty among agents in non-verifiable transactions. To this end, one or more features of solutions from literature are combined in each proposed solution. The collection of said multiple- and single-featured solutions form a super set of honesty-incentive solutions that include features of honesty inference or guarantee for non-verifiable transactions. The performances of the solutions in this super set are then compared and ranked according to their effectiveness (i.e., their capacity to stimulate honesty and hence, improve transactions completion rate – “success”). Table below specifies the features of all solutions in the super set being considered.

| Solution features | A | B | C | D | E | F | G | H | I |
|-------------------|---|---|---|---|---|---|---|---|---|
| Arbitrator | X | X |   |   | X | X |   |   |   |
| Categories | X |   | X |   | X |   | X |   |   |
| Security Deposit |   |   | X | X |   |   | X | X |   |
| Feedback |   |   |   |   | X | X | X | X |   |

Table 1. Proposed Solutions (A, B, C, E and G) and Solutions from the Literature - D; F; and, H.

'A' is the solution that combines an auditor and differentiation of the honesty of each agent by category of values negotiated, so a given agent can trust another for one type (category) of transaction but not trust the same agent for another type (category) of transaction. 'B' uses only a trusted and decentralized intermediate auditor in its solution. 'C' uses security deposits and differentiates trust between agents by categories. 'D' uses only security deposits to secure the solution. 'E' uses an intermediary auditor and trust based not on previous relationships, but on feedback given by agents regarding the honesty of others, in addition to classifying the honesty of agents by transaction category. Following the same reasoning we have the solutions 'F', 'G' and 'H'. 'I' offers no features to deal with dishonesty. It is included here to gage potential ''absolute'' gains by the other solutions.


### SIMULATION EXPERIMENT

#### Methodology

The hypothesis to be tested here points to the fact that it is possible to encourage honesty using decentralized solutions even in the presence of non-verifiable operations. The objective is to identify superior honesty incentive strategies that, even without verifying the operations, are capable of rewarding and encouraging honesty in a population of agents with diverse categories of values exchanged and honesty profiles. To this end, Agent-Based Simulation~(ABS) was used to record the effectiveness of each agent separately and of the population as a whole through multiple simulations runs for different configurations of mutually exclusive solutions at different honesty rates.

#### Simulation Design

An Agent-Based Simulation (ABS) of decentralized markets where agents produce and trade values according to the applied solution was performed. In addition to reproducing such interactions, parameters such as honesty, memory, risk, bankruptcy and success were incorporated. 

The simulation environment consists of a $256\times 256$ 2D Grid, containing an initial total of 100 agents that may grow to a maximum of 1,000 agents as the simulation run progresses. 

### SIMULATION RESULTS

For each solution in Table 1, the population's performance, the success of the simulated economy and the effectiveness of the solutions in avoiding unsuccessful transactions are presented.

#### Format and Metrics

The dataset resulting from multiple runs of the ABS comprises two distinct tables: one containing data for each final record of every run, and the other containing a random sampling of various sequential states of the agent population throughout the simulation. The table documenting final records of each simulation run has as result only one key field, a Boolean indicating the success or failure of each simulation (based on whether all agents perish or not, before the completion of the simulation run).

Similarly, the table capturing partial simulation records throughout the process includes three primary fields: a proportional balance of unsuccessful transactions up to that point, the total balance of unsuccessful transactions avoided by the employed solution, and the proportional balance of successful transactions. The experiment consisted of 81,000 simulation runs across nine distinct honesty rates in {0.1, 0.2, ..,0.9}.

Analysis of the data revealed a density curve resembling a normal distribution for the population's average success rate. Statistical inference, employing confidence intervals of 95% certainty level, was utilized to assess the success of the population and the diversity of agents. Furthermore, a Spearman correlation analysis was conducted to examine the relationship between the total number of unsuccessful transactions avoided and total failures at each simulation time step, across the various tested solutions.


#### Economics Performance

Overall, 63% of the total number of simulation runs resulted in success for the population. Solution 'A' achieved 74% success across the entire experiment, 'I' without any form of transaction verification achieved 49% success. The performance of each compared solution across the range of honesty rate values is presented in Table 2 below.


|  Solutions  | 10% | 20% |  30% |  40% |   50% |   60% |   70% |   80% |   90% |
|-------------|-----|-----|------|------|-------|-------|-------|-------|-------|
| A           | 0.0 | 0.0 | 41.7 | 88.9 | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 |
| B           | 0.0 | 0.0 | 22.2 | 62.5 |  90.0 |  91.7 | 100.0 | 100.0 | 100.0 |
| C           | 0.0 | 0.0 |  9.1 | 36.4 |  81.8 | 100.0 | 100.0 | 100.0 | 100.0 |
| D           | 0.0 | 0.0 |  0.0 | 66.7 |  81.8 | 100.0 | 100.0 | 100.0 | 100.0 |
| E           | 0.0 | 0.0 | 18.2 | 54.5 |  90.0 | 100.0 | 100.0 | 100.0 | 100.0 |
| F           | 0.0 | 0.0 | 12.5 | 40.0 |  70.0 | 100.0 | 100.0 | 100.0 | 100.0 |
| G           | 0.0 | 0.0 |  0.0 | 54.5 |  77.8 |  87.5 | 100.0 | 100.0 | 100.0 |
| H           | 0.0 | 0.0 | 16.7 | 60.0 |  63.6 | 100.0 | 100.0 | 100.0 | 100.0 |
| I           | 0.0 | 0.0 |  0.0 | 30.0 |  60.0 | 100.0 | 100.0 | 100.0 | 100.0 |

Table 2. Population success estimation for each compared solution in Table 1.

Looking more closely at Table 2, we see that no solution presents any success for an honesty rate less than or equal to 0.2 (20%), and all present perfect performances for honesty rates greater than or equal to 0.7 (70%). Actual competition among the compared solutions occurs only in the range of honesty rates from 0.3 (30%) to 0.6 (60%), a fact that in itself demonstrates an open potential for the improvement of transaction validation solutions containing non-verifiable operations. In the competition range, 'A' demonstrates significant dominance over the others. This indicates that there may be a transaction verification solution capable of satisfactorily stimulating agents’ honesty when the overall population's honesty rate equals or exceeds 30%.
