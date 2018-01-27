/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.civsavefilelib.civ6;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.civsavefilelib.main;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author danny_000
 */
public class Civ6Analyzer {

    final int[] aiOrPlayer = new int[]{0x95, 0xb9, 0x42, 0xce};
    final int[] civBegin = new int[]{0x31, 0xEB, 0x88, 0x62};
    final int[] playerNameArray = new int[]{0xfd, 0x6b, 0xb9, 0xda};
    final int[] civEnd = new int[]{0x58, 0xba};
    final int[] civName = new int[]{0x2f, 0x5c, 0x5e, 0x9d};
    final int[] civLevel = new int[]{0xAB, 0x55, 0xCA, 0x05};
    final int[] delimiter = new int[]{0x21, 0x01};
    final int[] playerPassword = new int[]{0x6c, 0xd1, 0x7c, 0x6e};

    public List<Civilization> analyseSaveFile(RandomAccessFile ra) {
        try {
            List<Civilization> civs = new ArrayList();
            List<Location> civLocs = new ArrayList();
            int c;
            while ((c = ra.read()) != -1) {
                if (c == civBegin[0]) {
                    c = ra.read();
                    if (c == civBegin[1]) {
                        c = ra.read();
                        if (c == civBegin[2]) {
                            civLocs.add(findCivs(ra));
                        }
                    }
                }
            }
            for (Location loc : civLocs) {
                civs.add(analyseCiv(ra, loc));
            }
            return civs;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    private Location findCivs(RandomAccessFile fh) {
        try {
            long beginCiv = fh.getFilePointer();
            long endCiv = 0l;
            int read;
            while ((read = fh.read()) != -1) {
                if (read == civEnd[0]) {
                    read = fh.read();
                    if (read == civEnd[1]) {
                        endCiv = fh.getFilePointer();
                        break;
                    }
                }
            }
            Location location = new Location(beginCiv, endCiv);
            return location;

        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    private Civilization analyseCiv(RandomAccessFile fh, Location loc) throws IOException {
        Civilization civ = new Civilization();
        fh.seek(loc.getBegin());
        //Get Civ name
        int c = 0;
        while (fh.getFilePointer() != loc.getEnd() && (c = fh.read()) != -1) {
            
            if (c == civName[0]) {
                c = fh.read();
                if (c == civName[1]) {
                    c = fh.read();
                    if (c == civName[2]) {
                        fh.read();
                        civ.setCivName(getCivName(fh, loc));
                        if (civ.getCivName() != null) {
                            civ.setFullciv(true);
                            civ.setHumanPlayer(isHuman(civ, fh, loc));
                            if (civ.isHumanPlayer()) {
                                civ.setPlayername(findPlayerName(civ, fh, loc));
                                civ.setCivPassword(findCivPassword(civ, fh, loc));
                                fh.seek(loc.getEnd());
                            }
                        } else {
                            civ.setFullciv(false);
                        }

                    }
                }
            }
        }
        return civ;

    }

    private boolean minorOrFullCiv(long begin, long end, RandomAccessFile fh) {
        try {
            fh.seek(begin);
            int c;
            while ((c = fh.read()) != -1) {
                if (c == civLevel[0]) {
                    c = fh.read();
                    if (c == civLevel[1]) {
                        c = fh.read();
                        if (c == civLevel[2]) {
                            System.out.println("MinorOrFull" + fh.read());
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Civ6Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    private FullCivName getCivName(RandomAccessFile fh, Location loc) throws IOException {
        int c;
        boolean foundCivName = false;
        String name = "";
        while ((c = fh.read()) != -1) {
            if (c == civName[0]) {
                c = fh.read();
                if (c == civName[1]) {
                    c = fh.read();
                    if (c == civName[2]) {
                        while (!foundCivName) {
                            c = fh.read();
                            if (c == 0x21) {
                                c = fh.read();
                                if (c == 0x01) {
                                    //fh.seek((fh.getFilePointer() - 1));
                                    while (true) {
                                        c = fh.read();
                                        if (c == 0xb1 || c == 0xee || c == 0x40) {
                                            foundCivName = true;
                                            break;
                                        }
                                        if (c != 0x00) {
                                            name = name + (new Character((char) c).toString());
                                        }

                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        FullCivName civenum = null;
        try {
            civenum = FullCivName.valueOf(name);
        } catch (Exception ex) {

        }
        System.out.println("getcivname" + civenum);
        return civenum;
    }

    private boolean isHuman(Civilization civ, RandomAccessFile fh, Location loc) throws IOException {
        fh.seek(loc.getBegin());
        int c = 0;
        boolean human = false;
        boolean foundHumanity = false;
        while (!foundHumanity) {
            c = fh.read();
            if (c == aiOrPlayer[0]) {
                c = fh.read();
                if (c == aiOrPlayer[1]) {
                    c = fh.read();
                    if (c == aiOrPlayer[2]) {
                        while (!foundHumanity) {
                            c = fh.read();
                            if (c != 0x00) {
                                if (c == 0x03) {
                                    human = true;
                                    foundHumanity = true;
                                } else if (c == 0x01) {
                                    human = false;
                                    foundHumanity = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("findhumanity" + human);
        return human;

    }

    private String findPlayerName(Civilization civ, RandomAccessFile fh, Location loc) throws IOException {
        fh.seek(loc.getBegin());
        int c = 0;
        String playerName = "";
        boolean foundPlayerName = false;
        while (fh.getFilePointer() != loc.getEnd() && !foundPlayerName) {
            c = fh.read();
            if (c == playerNameArray[0]) {
                c = fh.read();
                if (c == playerNameArray[1]) {
                    c = fh.read();
                    if (c == playerNameArray[2]) {
                        while (!foundPlayerName) {
                            c = fh.read();
                            if (c == 0x21) {
                                c = fh.read();
                                if (c == 0x01) {
                                    boolean endOfName = false;
                                    while (!endOfName) {
                                        c = fh.read();
                                        if (c == 0xc7) {
                                            c = fh.read();
                                            if (c == 0xfc) {
                                                endOfName = true;
                                                foundPlayerName = true;
                                                break;
                                            }
                                        }
                                        if (c != 0x00) {
                                            playerName = playerName + (new Character((char) c).toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("playername" + playerName);
        return playerName;
    }

    private String findCivPassword(Civilization civ, RandomAccessFile fh, Location loc) throws IOException {
        fh.seek(loc.getBegin());
        int c = 0;
        String playerPasswordstring = "";
        boolean foundPlayerPassword = false;
        while (fh.getFilePointer() != loc.getEnd() && !foundPlayerPassword) {
            c = fh.read();
            if (c == playerPassword[0]) {
                c = fh.read();
                if (c == playerPassword[1]) {
                    c = fh.read();
                    if (c == playerPassword[2]) {
                        while (!foundPlayerPassword) {
                            c = fh.read();
                            if (c == 0x21) {
                                c = fh.read();
                                if (c == 0x01) {
                                    boolean endOfPassword = false;
                                    while (!endOfPassword) {
                                        c = fh.read();
                                        if (c == 0xcb) {
                                            c = fh.read();
                                            if (c == 0x21) {
                                                endOfPassword = true;
                                                foundPlayerPassword = true;
                                                break;
                                            }
                                        }
                                        if (c != 0x00) {
                                            playerPasswordstring = playerPasswordstring + (new Character((char) c).toString());
                                        }
                                    }
                                }
                            }
                            if (c == 0x46) {
                                c = fh.read();
                                if (c == 0xfc) {
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
        System.out.println("password" + playerPasswordstring);
        return playerPasswordstring;
    }

    public Location findCiv(Civilization civ, RandomAccessFile fh) {
        try {
            long beginCiv = fh.getFilePointer();
            long endCiv = 0l;
            int read;
            while ((read = fh.read()) != -1) {
                if (read == civEnd[0]) {
                    read = fh.read();
                    if (read == civEnd[1]) {
                        endCiv = fh.getFilePointer();
                        Location loc = new Location(beginCiv, endCiv);
                        if (civ.getCivName() == getCivName(fh, loc)) {
                            return loc;
                        }
                    }
                }
            }
            Location location = new Location(beginCiv, endCiv);
            return location;

        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Civilization resyncCiv(Civilization civ, RandomAccessFile ra) {
        List<Civilization> tempCivs = analyseSaveFile(ra);
        for (Civilization tempCiv : tempCivs) {
            if (tempCiv.equals(civ)) {
                return tempCiv;
            }
        }
        return civ;
    }

}
