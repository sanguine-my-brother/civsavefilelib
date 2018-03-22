/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.civsavefilelib;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import nl.hyranasoftware.civsavefilelib.civ6.Civ6Analyzer;
import nl.hyranasoftware.civsavefilelib.civ6.Civ6Writer;
import nl.hyranasoftware.civsavefilelib.civ6.Civilization;
import nl.hyranasoftware.civsavefilelib.civ6.Location;

/**
 *
 * @author danny_000
 */
//https://stackoverflow.com/questions/28206027/modifying-contents-of-a-binary-file-in-java
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String fileName = "C:\\Users\\danny_000\\programming\\civsavefilelib\\src\\main\\java\\nl\\hyranasoftware\\civsavefilelib\\4players.Civ6Save";

        File file = new File(fileName);
        RandomAccessFile fh = new RandomAccessFile(file, "rw");
        Civ6Analyzer civ6 = new Civ6Analyzer();
        List<Civilization> civs = civ6.analyseSaveFile(fh);
        System.out.println("done");
        RandomAccessFile xd = new RandomAccessFile(file, "rw");
        Civ6Writer civ6writer = new Civ6Writer();
        int i = 0;
        
        for (Civilization civ : civs) {
            if (!civ.isHumanPlayer() && civ.isFullciv()) {
                fh.seek(0l);
                Location loc = civ6.findCiv(civ, fh);
                civ6writer.changeCivToHuman(loc, xd, "ConvertedHuman");
                break;
            }
        }
        
        

    }

}
