# Overview

## Architecture

### Execution flow

We are using separate concurrent threads that contain specialized "operators" (e.g. specialized "commander" processes for defense, economy, attacks, micromanagement, etc).

Because BWAPI and Starcraft are single-threaded (e.g. game runs in a loop calling onFrame() every few milliseconds), to avoid concurent execution of orders, all final orders are put onto an "orders stack".

All these stacks are ordered in a hierarchy:

- **level 0** (lowest-level): 
    - raw BWAPI orders/calls (like move unit X to coordinates Y)
    - all units that should receive orders have the corresponding orders entries contained here
    - every onFrame()
The highest-level one contains

- **level 5** (highest-level):
    - here are pushed all strategic or overall requests
        - need 10 marines asap
        - need an expansion at (x,y)
        - need defense at (x,y)
    
The [Scheduler grunts](#) translate each level of orders into lower-level instructions, depending on things like priorities and player "mood". For exemple, if the mood is at "aggessive" the schedulers will prioritize army production orders all down to the level 0, and military units will be produces

### Eco-system

Everything is organized around several "stacks" of ressources/units/etc whose content increases and/or gets consumed over time.

Th

Producer agents

### Stacks - Pools - Schedules

#### Stacks

#### Schedules

#### Pools



- 
### Operators


- 
- multi-threaded (each "operator" has it's own thread)
- messaging between ops done using "pools" of requests/orders
- combat and civilian units do an "ant-colony" mapping by "dropping" information on explored tiles
    - 

