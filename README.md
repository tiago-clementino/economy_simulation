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

