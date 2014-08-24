# September sprint specifications

## Architecture

### General API

#### Consumer-Needs-Producer

Currently we have a ```Consumer```-```Needs``` system where a consumer adds its needs to the global needs manager ```Needs.getInstance().add(need)``` and the later is in charge of fulfilling those needs by itself (e.g. waiting for units to be created and dispatching these units to consumers that accept these units).

Right now a Base produces workers non-stop (each new one is dispatched to a consumer - MineralPatch), so when all patches are saturated extra workers are useless and stay idle.

Base now implements Producer so that it only builds workers when it has consumers waiting

#### Units

#### Resources

#### Supplies


### Agents


#### Commands
