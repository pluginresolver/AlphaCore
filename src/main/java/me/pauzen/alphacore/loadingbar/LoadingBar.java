/*
 *  Created by Filip P. on 2/2/15 11:24 PM.
 */

package me.pauzen.alphacore.loadingbar;

import me.pauzen.alphacore.players.CorePlayer;

public class LoadingBar {

    private float current = 0F;
    private CorePlayer corePlayer;

    private int   previousLevel;
    private float previousXP;

    public LoadingBar(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
        previousLevel = corePlayer.getPlayer().getLevel();
        previousXP = corePlayer.getPlayer().getExp();
    }

    private float xpPerTick;

    private boolean displaying = false;

    public LoadingBar display(int ticks) {
        xpPerTick = (float) 1 / ticks;

        displaying = true;
        corePlayer.getPlayer().setExp(0.0F);

        LoadingBarManager.getManager().registerBar(this);
        corePlayer.setLoadingBar(this);
        return this;
    }

    public float getCurrent() {
        return this.current;
    }

    public void update() {
        if (displaying) {

            corePlayer.getPlayer().setExp(current += xpPerTick);

            if (1 - current < xpPerTick) {
                revert();
            }
        }
    }

    public void setPreviousXP(float value) {
        this.previousXP = value;
    }

    public float getPreviousXP() {
        return this.previousXP;
    }

    public void revert() {
        displaying = false;
        corePlayer.getPlayer().setExp(previousXP);
        corePlayer.getPlayer().setLevel(previousLevel);
        new LoadedEvent(corePlayer).call();
        corePlayer.setLoadingBar(null);
        LoadingBarManager.getManager().deregisterBar(this);
    }

    public CorePlayer getPlayer() {
        return corePlayer;
    }

    public static LoadingBar displayLoadingBar(CorePlayer corePlayer, int ticks) {
        return new LoadingBar(corePlayer).display(ticks);
    }
}
