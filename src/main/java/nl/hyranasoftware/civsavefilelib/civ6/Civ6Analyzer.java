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
            int c;
            while ((c = ra.read()) != -1) {
                if (c == civBegin[0]) {
                    c = ra.read();
                    if (c == civBegin[1]) {
                        c = ra.read();
                        if (c == civBegin[2]) {
                            civs.add(findCivs(ra));
                        }
                    }
                }
            }
            for (Civilization civ : civs) {
                analyseCiv(civ, ra);
            }
            ra.close();
            return civs;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }

    private Civilization findCivs(RandomAccessFile fh) {
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
            return new Civilization(beginCiv, endCiv);

        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    private void analyseCiv(Civilization civ, RandomAccessFile fh) throws IOException {
        fh.seek(civ.getBeginpos());
        //Get Civ name
        while (fh.getFilePointer() != civ.getEndpos()) {
            int c = fh.read();
            if (c == civName[0]) {
                c = fh.read();
                if (c == civName[1]) {
                    c = fh.read();
                    if (c == civName[2]) {
                        fh.read();
                        civ.setCivName(getCivName(fh));
                        if (civ.getCivName() != null) {
                            civ.setFullciv(true);
                            civ.setHumanPlayer(isHuman(civ, fh));
                            if (civ.isHumanPlayer()) {
                                civ.setPlayername(findPlayerName(civ, fh));
                                civ.setCivPassword(findCivPassword(civ, fh));
                                fh.seek(civ.getEndpos());
                            }
                        } else {
                            civ.setFullciv(false);
                        }

                    }
                }
            }
        }

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
                            System.out.println(fh.read());
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Civ6Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    private FullCivName getCivName(RandomAccessFile fh) throws IOException {
        int c;
        boolean foundCivName = false;
        String name = "";
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

        FullCivName civenum = null;
        try {
            civenum = FullCivName.valueOf(name);
        } catch (Exception ex) {

        }
        System.out.println(civenum);
        return civenum;
    }

    private boolean isHuman(Civilization civ, RandomAccessFile fh) throws IOException {
        fh.seek(civ.getBeginpos());
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
                                civ.setHumanPlayerPos(fh.getFilePointer() - 1);

                            }
                        }
                    }
                }
            }
        }
        System.out.println(human);
        return human;

    }

    private String findPlayerName(Civilization civ, RandomAccessFile fh) throws IOException {
        fh.seek(civ.getBeginpos());
        int c = 0;
        String playerName = "";
        boolean foundPlayerName = false;
        while (fh.getFilePointer() != civ.getEndpos() && !foundPlayerName) {
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
        System.out.println(playerName);
        return playerName;
    }

    private String findCivPassword(Civilization civ, RandomAccessFile fh) throws IOException {
        fh.seek(civ.getBeginpos());
        int c = 0;
        String playerPasswordstring = "";
        boolean foundPlayerPassword = false;
        while(fh.getFilePointer() != civ.getEndpos() && !foundPlayerPassword){
            c = fh.read();
            if(c == playerPassword[0]){
                c = fh.read();
                if(c == playerPassword[1]){
                    c = fh.read();
                    if(c == playerPassword[2]){
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
                            if(c == 0x46){
                                c = fh.read();
                                if(c == 0xfc){
                                    break;
                                }
                            }
                        }
                    }
                }
                
            }
        }
        System.out.println(playerPasswordstring);
        return playerPasswordstring;
    }

}
