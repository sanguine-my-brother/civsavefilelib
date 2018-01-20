/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.civsavefilelib.civ6;

/**
 *
 * @author danny_000
 */
public class Civilization {
    
    private long beginpos;
    private long endpos;
    private boolean fullciv;
    private String playername;
    private long playernamePos;
    private boolean humanPlayer;
    private long humanPlayerPos;
    private FullCivName civName;
    

    public Civilization() {
    }

    public Civilization(long beginpos, long endpos) {
        this.beginpos = beginpos;
        this.endpos = endpos;
    }
    

    public long getBeginpos() {
        return beginpos;
    }

    public void setBeginpos(long beginpos) {
        this.beginpos = beginpos;
    }

    public long getEndpos() {
        return endpos;
    }

    public void setEndpos(long endpos) {
        this.endpos = endpos;
    }

    public boolean isFullciv() {
        return fullciv;
    }

    public void setFullciv(boolean fullciv) {
        this.fullciv = fullciv;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public long getPlayernamePos() {
        return playernamePos;
    }

    public void setPlayernamePos(long playernamePos) {
        this.playernamePos = playernamePos;
    }

    public boolean isHumanPlayer() {
        return humanPlayer;
    }

    public void setHumanPlayer(boolean humanPlayer) {
        this.humanPlayer = humanPlayer;
    }

    public long getHumanPlayerPos() {
        return humanPlayerPos;
    }

    public void setHumanPlayerPos(long humanPlayerPos) {
        this.humanPlayerPos = humanPlayerPos;
    }

    public FullCivName getCivName() {
        return civName;
    }

    public void setCivName(FullCivName civName) {
        this.civName = civName;
    }
    
    
    
    
}
