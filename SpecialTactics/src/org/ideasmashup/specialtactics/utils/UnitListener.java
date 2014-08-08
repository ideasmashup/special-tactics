package org.ideasmashup.specialtactics.utils;

import bwapi.Unit;

public interface UnitListener {

    public void onUnitDiscover(Unit unit);
    public void onUnitEvade(Unit unit);
    public void onUnitShow(Unit unit);
    public void onUnitHide(Unit unit);
    public void onUnitCreate(Unit unit);
    public void onUnitDestroy(Unit unit);
    public void onUnitMorph(Unit unit);
    public void onUnitRenegade(Unit unit);
    public void onUnitComplete(Unit unit);

}
