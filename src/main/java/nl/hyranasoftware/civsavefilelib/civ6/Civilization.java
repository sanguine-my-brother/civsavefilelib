/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.civsavefilelib.civ6;

import java.util.Objects;

/**
 *
 * @author danny_000
 */
public class Civilization {
    

    
    private boolean fullciv;
    private String playername;
    private boolean humanPlayer;
    private FullCivName civName;
    private String civPassword;
    

    public Civilization() {
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


    public boolean isHumanPlayer() {
        return humanPlayer;
    }

    public void setHumanPlayer(boolean humanPlayer) {
        this.humanPlayer = humanPlayer;
    }


    public FullCivName getCivName() {
        return civName;
    }

    public void setCivName(FullCivName civName) {
        this.civName = civName;
    }

    public String getCivPassword() {
        return civPassword;
    }

    public void setCivPassword(String civPassword) {
        this.civPassword = civPassword;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.civName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Civilization other = (Civilization) obj;
        if (this.civName != other.civName) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    
    
    
    
}
